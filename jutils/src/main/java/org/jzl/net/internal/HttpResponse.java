package org.jzl.net.internal;

import org.jzl.net.Headers;
import org.jzl.net.Response;
import org.jzl.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class HttpResponse implements Response {

    private int mResponseCode;
    private Body mBody;
    private Headers mHeaders;

    public HttpResponse(int responseCode, Body mBody, Headers mHeaders) {
        this.mResponseCode = responseCode;
        this.mBody = mBody;
        this.mHeaders = mHeaders;
    }

    @Override
    public int getResponseCode() {
        return mResponseCode;
    }

    @Override
    public Body getBody() {
        return mBody;
    }

    @Override
    public Headers getHeaders() {
        return mHeaders;
    }

    private static class HttpBoyd implements Body {
        private int mBodyLength;
        private InputStream inputStream;
        private Charset charset;

        private HttpBoyd(int mBodyLength, InputStream inputStream, Charset charset) {
            this.mBodyLength = mBodyLength;
            this.inputStream = inputStream;
            this.charset = charset;
        }

        @Override
        public int bodyLength() {
            return mBodyLength;
        }

        @Override
        public <T> T get(Converter<T> converter) throws IOException {
            return converter.transfer(this);
        }

        @Override
        public InputStream getStream() {
            return inputStream;
        }

        @Override
        public String getText() throws IOException {
            return get(new Converter<String>() {
                @Override
                public String transfer(Body body) throws IOException {
                    return IOUtil.copyStreamToString(body.getStream(), 0, charset);
                }
            });
        }

        public static class Builder {
            private int mBodyLength;
            private InputStream inputStream;
            private Charset charset = Charset.forName("UTF-8");

            public Builder bodyLength(int length) {
                this.mBodyLength = length;
                return this;
            }

            public Builder inputStream(InputStream inputStream) {
                this.inputStream = inputStream;
                return this;
            }

            public Builder charset(String name) {
                this.charset = Charset.forName(name);
                return this;
            }

            public Body build() {
                return new HttpBoyd(mBodyLength, inputStream, charset);
            }
        }
    }

    public static class Builder {
        private HttpBoyd.Builder mBodyBuilder;
        private HttpHeaders.Builder mHeadersBuilder;
        private int mResponseCode = -1;

        public Builder() {
            this.mBodyBuilder = new HttpBoyd.Builder();
            this.mHeadersBuilder = new HttpHeaders.Builder();
        }

        public Builder bodyLength(int length) {
            mBodyBuilder.bodyLength(length);
            return this;
        }

        public Builder inputStream(InputStream inputStream) {
            mBodyBuilder.inputStream(inputStream);
            return this;
        }

        public Builder charset(String name) {
            mBodyBuilder.charset(name);
            return this;
        }

        public Builder headers(Headers headers) {
            this.mHeadersBuilder.addAll(headers);
            return this;
        }

        public Builder headers(HttpHeaders.Builder builder) {
            this.mHeadersBuilder.addAll(builder.build());
            return this;
        }

        public Builder header(String name, String value) {
            this.mHeadersBuilder.add(name, value);
            return this;
        }

        public Builder setResponseCode(int responseCode){
            this.mResponseCode = responseCode;
            return this;
        }

        public Response build() {
            return new HttpResponse(mResponseCode,mBodyBuilder.build(), mHeadersBuilder.build());
        }

    }
}
