package org.jzl.net;

import org.jzl.utils.DateUtil;
import org.jzl.utils.ObjectUtil;
import org.jzl.utils.StringUtil;
import org.jzl.utils.text.Joiner;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class Cookie {

    private static final String MAX_AGE = "max-age";
    private static final String HTTP_ONLY = "httpOnly";
    private static final String SECURE = "secure";
    private static final String PATH = "path";
    private static final String DOMAIN = "domain";
    private static final String EXPIRES = "expires";

    private String name;//cookie名称（该值不能为null）
    private String value;//cookie值（该值不能为null）

    private String domain;//对于那个域名有效
    private String path;//对于那个路径有效
    private Date expires;//过期时间
    private long maxAge;//缓存时间长度（优先于expires）
    private boolean httpOnly;//是否允许用户通过脚本修改
    private boolean secure;//安全标记（ssl连接才发送）

    private Cookie(String name, String value, String domain, String path, Date expires, long maxAge, boolean httpOnly, boolean secure) {
        ObjectUtil.requireNonNull(name);
        ObjectUtil.requireNonNull(value);

        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.expires = expires;
        this.maxAge = maxAge;
        this.httpOnly = httpOnly;
        this.secure = secure;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isSecure() {
        return secure;
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("; ");
        joiner.join(name + "=" + value);
        if (ObjectUtil.nonNull(domain)) {
            joiner.join(DOMAIN + "=" + domain);
        }
        if (ObjectUtil.nonNull(path)) {
            joiner.join(PATH + "=" + path);
        }
        if (maxAge > 0) {
            joiner.join(MAX_AGE + "=" + maxAge);
        }
        if (ObjectUtil.nonNull(expires)) {
            joiner.join(EXPIRES + "=" + DateUtil.format(expires));
        }
        if (httpOnly) {
            joiner.join(HTTP_ONLY);
        }
        if (secure) {
            joiner.join(SECURE);
        }
        return joiner.toString();
    }

    public static Cookie parse(URL url, String cookie) {
        return parse(url, System.currentTimeMillis(), cookie);
    }

    public static List<Cookie> parseAll(URL url, Headers headers){
        List<String> values = headers.values(Headers.SET_COOKIE);
        List<Cookie> cookies = null;
        for (String value : values){
            if (cookies == null){
                cookies = new ArrayList<>();
            }
            cookies.add(parse(url, value));
        }
        return cookies == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(cookies);
    }

    public static Cookie parse(URL url, long currentTime, String cookie) {
        String[] values = cookie.split(";");

        Cookie.Builder builder = new Cookie.Builder();
        for (String date : values) {
            if (StringUtil.isEmpty(date)) {
                continue;
            }
            int index = date.indexOf("=");
            if (index == -1) {
                if (date.equalsIgnoreCase(HTTP_ONLY)) {
                    builder.httpOnly();
                } else if (date.equalsIgnoreCase(SECURE)) {
                    builder.secure();
                } else {
                }
            } else {
                String name = date.substring(0, index).trim();
                String value = date.substring(index + 1);
                if (name.equalsIgnoreCase(MAX_AGE)) {
                    builder.maxAge(parseMaxAge(value));
                } else if (name.equalsIgnoreCase(DOMAIN)) {
                    builder.domain(value);
                } else if (name.equalsIgnoreCase(PATH)) {
                    builder.path(value);
                } else if (name.equalsIgnoreCase(EXPIRES)) {
                    builder.expires(value);
                } else {
                    builder.name(name);
                    builder.value(value);
                }
            }
        }
        return builder.build();
    }

    private static long parseMaxAge(String value) {
        try {
            long maxAge = Long.parseLong(value);
            return maxAge < 0 ? Long.MAX_VALUE : maxAge;
        } catch (NumberFormatException e) {
            if (value.matches("-?[0-9]+]")) {
                return value.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            throw e;
        }
    }

    public static class Builder {

        private String name;//cookie名称（该值不能为null）
        private String value;//cookie值（该值不能为null）

        private String domain;//对于那个域名有效
        private String path;//对于那个路径有效
        private Date expires;//过期时间
        private long maxAge;//缓存时间长度（优先于expires）
        private boolean httpOnly;//是否允许用户通过脚本修改
        private boolean secure;//安全标记（ssl连接才发送）

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder path(String path) {
            if (!path.startsWith("/")) {
                throw new RuntimeException("path 必须 \"/\"开头");
            }
            this.path = path;
            return this;
        }

        public Builder expires(Date expires) {
            this.expires = expires;
            return this;
        }

        public Builder expires(String expires) {
            this.expires = DateUtil.parseCookieDate(expires);
            return this;
        }

        public Builder maxAge(long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder httpOnly() {
            this.httpOnly = true;
            return this;
        }

        public Builder secure() {
            this.secure = true;
            return this;
        }

        public Cookie build() {
            return new Cookie(this.name, this.value, this.domain, this.path, this.expires, this.maxAge, this.httpOnly, this.secure);
        }
    }
}
