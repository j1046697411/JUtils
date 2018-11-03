package org.jzl.net;

import org.jzl.Version;
import org.jzl.net.internal.HttpHeaders;
import org.jzl.net.internal.HttpUrlCall;
import org.jzl.utils.ObjectUtil;
import org.jzl.utils.chain.Chain;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class HttpClient implements Call.Factory<Response, Request> {

    private ClientChain mClientChain;
    private HttpHeaders.Builder mBuilder;
    private CookieJar mCookies;


    public HttpClient() {
        this.mClientChain = new ClientChain(this);
        this.mBuilder = new HttpHeaders.Builder();
        mClientChain.addInterceptor(new BridgeInterceptor(this));
        mCookies = new CookieJar() {
            @Override
            public void saveCookies(URL url, List<Cookie> cookies) {
                System.out.println(url + "|" + cookies);
            }

            @Override
            public List<Cookie> loadForRequest(URL url) {
                return Collections.EMPTY_LIST;
            }
        };
        defaultHeaders();
    }

    private void defaultHeaders(){
        mBuilder.add(Headers.USER_AGENT, Version.CURRENT_VALUE.getUserAgent());
    }

    public Headers getDefaultHeaders() {
        return mBuilder.build();
    }

    public CookieJar cookieJar() {
        return mCookies;
    }

    public HttpClient addInterceptor(Chain.Interceptor<Response, Request> interceptor) {
        mClientChain.addInterceptor(interceptor);
        return this;
    }

    public HttpClient addHeader(String name, String value) {
        mBuilder.add(name, value);
        return this;
    }

    public HttpClient addHeaders(HttpHeaders headers) {
        mBuilder.addAll(headers);
        return this;
    }

    public HttpClient setCookieJar(CookieJar cookieJar) {
        if (ObjectUtil.nonNull(cookieJar)) {
            this.mCookies = cookieJar;
        }
        return this;
    }

    @Override
    public Call<Response, Request> newCall(Request request) {
        Call<Response, Request> call = new HttpUrlCall(this, request);
        call.addInterceptor(mClientChain);
        return call;
    }

    private final class ClientChain extends Chain.AbstractChain<Response, Request> {

        private HttpClient mClient;

        ClientChain(HttpClient client) {
            this.mClient = client;
        }

        @Override
        protected Response lastIntercept(Chain<Response, Request> chain, Request request) {
            return chain.proceed(request);
        }
    }
}
