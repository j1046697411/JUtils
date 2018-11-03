package org.jzl;

import org.jzl.net.Call;
import org.jzl.net.HttpClient;
import org.jzl.net.Request;
import org.jzl.net.Response;
import org.jzl.net.internal.HttpRequest;
import org.jzl.utils.chain.Chain;

import java.io.IOException;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class JavaMain {
    public static void main(String[] args) {
        HttpClient client = new HttpClient();
        client.addInterceptor(new Chain.Interceptor<Response, Request>() {
            @Override
            public Response intercept(Chain<Response, Request> chain, Request request) {
                System.out.println("-----add client Interceptor-----");
                return chain.proceed(request);
            }
        }).addInterceptor(new Chain.Interceptor<Response, Request>() {
            @Override
            public Response intercept(Chain<Response, Request> chain, Request request) {
                System.out.println("-----add client Interceptor 2-----");
                return chain.proceed(request);
            }
        });

        client.newCall(new HttpRequest.Builder().url("https://www.baidu.com/s?wd=qq")
                .header("test", "test").method(Request.Method.GET).build())
                .addInterceptor(new Chain.Interceptor<Response, Request>() {
                    @Override
                    public Response intercept(Chain<Response, Request> chain, Request request) {
                        System.out.println("-------newCall--------");
                        return chain.proceed(request);
                    }
                }).enqueue(new Call.Callback<Response, Request>() {
            @Override
            public void onFailure(Call<Response, Request> call, Exception e) {
                System.out.println("-------onFailure--------");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call<Response, Request> call, Response response) throws IOException {
                System.out.println(call.request().getUrl());
                System.out.println(call.request().getHeaders());
                System.out.println(call.realRequest().getHeaders());
                System.out.println("------- ^ _ ^ --------");
                System.out.println("responseCode:" + response.getResponseCode());
                System.out.println("body:" + response.getBody().getText());
                System.out.println(response.getHeaders().toString());
            }
        });
    }
}
