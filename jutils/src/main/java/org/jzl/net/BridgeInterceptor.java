package org.jzl.net;

import org.jzl.net.internal.HttpRequest;
import org.jzl.utils.StringUtil;
import org.jzl.utils.chain.Chain;
import org.jzl.utils.text.Joiner;

import java.net.URL;
import java.util.List;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/02
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
class BridgeInterceptor implements Chain.Interceptor<Response, Request> {

    private HttpClient mClient;

    BridgeInterceptor(HttpClient client) {
        this.mClient = client;
    }

    @Override
    public Response intercept(Chain<Response, Request> chain, Request request) {
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.url(request.getUrl());
        builder.method(request.getRequestMethod());
        builder.body(request.getBody());
        //设置默认的请求头
        setDefaultHeaders(request.getUrl(), builder, request);
        //继续执行请求
        Response response = chain.proceed(builder.build());
        //保存cookies
        saveCookies(request.getUrl(), response);
        return response;
    }

    private void setDefaultHeaders(URL url, HttpRequest.Builder builder, Request request) {
        Headers requestHeaders = request.getHeaders();
        Headers defaultHeaders = mClient.getDefaultHeaders();
        if (!defaultHeaders.isEmpty()) {
            builder.headers(defaultHeaders);
        }
        if (!requestHeaders.isEmpty()) {
            builder.headers(requestHeaders);
        }

        String cookies = getCookieHeader(url);
        if (!StringUtil.isEmpty(cookies)) {
            builder.header(Headers.COOKIE, getCookieHeader(url));
        }
    }

    private String getCookieHeader(URL url) {
        List<Cookie> cookies = mClient.cookieJar().loadForRequest(url);
        Joiner joiner = Joiner.on("; ");
        for (Cookie cookie : cookies) {
            joiner.join(cookie.getName() + "=" + cookie.getValue());
        }
        return joiner.toString();
    }

    private void saveCookies(URL url, Response response) {
        mClient.cookieJar().saveCookies(url, parseAllCookies(url, response.getHeaders()));
    }

    private List<Cookie> parseAllCookies(URL url, Headers headers) {
        return Cookie.parseAll(url, headers);
    }
}
