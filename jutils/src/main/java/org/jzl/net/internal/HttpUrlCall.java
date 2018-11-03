package org.jzl.net.internal;

import org.jzl.net.Call;
import org.jzl.net.Headers;
import org.jzl.net.HttpClient;
import org.jzl.net.Request;
import org.jzl.net.Response;
import org.jzl.utils.ObjectUtil;
import org.jzl.utils.chain.Chain;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class HttpUrlCall implements Call<Response, Request> {

    private HttpClient mClient;
    private CallChain mCallChain;
    private boolean mIsExecuted;
    private boolean mIsCanceled;
    private Request mRequest;
    private Request mRealRequest;

    public HttpUrlCall(HttpClient client, Request request) {
        this.mClient = ObjectUtil.requireNonNull(client);
        this.mRequest = ObjectUtil.requireNonNull(request);
        this.mCallChain = new CallChain(this);
        this.mIsExecuted = false;
        this.mIsCanceled = false;
        this.addInterceptor(new CallServerInterceptor(this));
    }

    @Override
    public Response execute() throws IOException {
        if (isExecuted()) {
            throw new RuntimeException("call executed");
        }
        return mCallChain.proceed(mRequest);
    }

    @Override
    public void enqueue(Callback<Response, Request> callback) {
        try {
            callback.onResponse(this, execute());
        } catch (Exception e) {
            callback.onFailure(this, e);
        }
    }

    @Override
    public void cancel() {
    }

    @Override
    public Request request() {
        return mRequest;
    }

    @Override
    public Request realRequest() {
        return mRealRequest;
    }

    @Override
    public boolean isCanceled() {
        return mIsCanceled;
    }

    @Override
    public boolean isExecuted() {
        return mIsExecuted;
    }

    @Override
    public Call<Response, Request> addInterceptor(Chain.Interceptor<Response, Request> interceptor) {
        mCallChain.addInterceptor(interceptor);
        return this;
    }

    public interface EventListener<RES extends Response, REQ extends Request> {
        void onCancel(Call<RES, REQ> call);
    }

    private class CallChain extends Chain.AbstractChain<Response, Request> {
        private HttpUrlCall mCall;

        public CallChain(HttpUrlCall call) {
            this.mCall = call;
        }

        @Override
        protected Response lastIntercept(Chain<Response, Request> chain, Request request) {
            throw new RuntimeException("没有设置正式的请求实现（CallServerInterceptor）");
        }
    }

    private static class CallServerInterceptor implements Chain.Interceptor<Response, Request> {
        private HttpUrlCall mCall;

        private CallServerInterceptor(HttpUrlCall call){
            this.mCall = call;
        }

        @Override
        public Response intercept(Chain<Response, Request> chain, Request request) {
            try {
                HttpURLConnection connection = openHttpURLConnection(request.getUrl());
                connection.setRequestMethod(request.getRequestMethod().toString());
                updateHeaderFields(connection, request);
                if (request.getBody() != null && request.getRequestMethod() == Request.Method.POST) {
                    connection.setDoInput(true);
                    request.getBody().writeTo(connection.getInputStream());
                }
                HttpResponse.Builder builder = new HttpResponse.Builder();
                builder.setResponseCode(connection.getResponseCode());
                if (connection.getResponseCode() < 400) {
                    builder.bodyLength(connection.getContentLength());
                    builder.inputStream(connection.getInputStream());
                }
                setHeaderFields(builder, connection);
                mCall.mRealRequest = request;
                return builder.build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void updateHeaderFields(HttpURLConnection connection, Request request) {
            Headers headers = request.getHeaders();
            Set<String> names = headers.names();
            for (String name : names) {
                List<String> values = headers.values(name);
                for (String value : values) {
                    connection.addRequestProperty(name, value);
                }
            }
        }

        private void setHeaderFields(HttpResponse.Builder builder, HttpURLConnection connection) {
            Map<String, List<String>> headers = connection.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String name = entry.getKey();
                List<String> values = entry.getValue();
                for (String value : values) {
                    if (ObjectUtil.isNull(value) || ObjectUtil.isNull(name)) {
                        continue;
                    }
                    builder.header(name, value);
                }
            }
        }

        private HttpURLConnection openHttpURLConnection(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }
}
