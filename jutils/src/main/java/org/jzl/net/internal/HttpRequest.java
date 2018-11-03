package org.jzl.net.internal;

import org.jzl.net.Headers;
import org.jzl.net.Request;

import java.net.URL;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class HttpRequest implements Request {

    private URL mUrl;
    private Method mMethod;
    private Body mBody;
    private Headers mHeaders;

    private HttpRequest(URL url, Method method, Body body, HttpHeaders.Builder builder) {
        this.mUrl = url;
        this.mMethod = method;
        this.mBody = body;
        this.mHeaders = builder.build();
    }

    @Override
    public URL getUrl() {
        return mUrl;
    }

    @Override
    public Method getRequestMethod() {
        return mMethod;
    }

    @Override
    public Body getBody() {
        return mBody;
    }

    @Override
    public Headers getHeaders() {
        return mHeaders;
    }

    public static final class Builder {
        private HttpHeaders.Builder builder;
        private URL url;
        private Method method = Method.GET;
        private Body body;

        public Builder() {
            this.builder = new HttpHeaders.Builder();
        }

        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        public Builder url(String url) {
            try {
                this.url = new URL(url);
                return this;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        public Builder body(Body body) {
            this.body = body;
            return this;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder headers(Headers headers) {
            this.builder.addAll(headers);
            return this;
        }

        public Builder headers(HttpHeaders.Builder builder) {
            this.builder.addAll(builder.build());
            return this;
        }

        public Builder header(String name, String value) {
            this.builder.add(name, value);
            return this;
        }

        public Request build() {
            return new HttpRequest(url, method, body, builder);
        }
    }
}
