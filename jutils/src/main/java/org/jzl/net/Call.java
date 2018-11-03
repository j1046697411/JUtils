package org.jzl.net;

import org.jzl.utils.chain.Chain;

import java.io.IOException;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/10/31
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public interface Call<RES extends Response, REQ extends Request> {

    //用户创建出来的请求
    REQ request();

    //请求在传递过程中可能发生改变，这是真实用于网络的请求（如果使用的缓存，可能不该值就为null）
    REQ realRequest();

    RES execute() throws IOException;

    void cancel();

    void enqueue(Callback<RES, REQ> callback);

    boolean isExecuted();

    boolean isCanceled();

    Call<RES, REQ> addInterceptor(Chain.Interceptor<RES, REQ> interceptor);

    interface Factory<RES extends Response, REQ extends Request> {
        Call<RES, REQ> newCall(REQ request);
    }

    interface Callback<RES extends Response, REQ extends Request> {

        void onFailure(Call<RES, REQ> call, Exception e);

        void onResponse(Call<RES, REQ> call, RES response) throws IOException;
    }
}
