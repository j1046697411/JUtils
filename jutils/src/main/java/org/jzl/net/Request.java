package org.jzl.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/10/31
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public interface Request {

    URL getUrl();

    Method getRequestMethod();

    Body getBody();

    Headers getHeaders();

    interface Body {
        void writeTo(InputStream in) throws IOException;
    }

    enum Method {
        GET("GET"), POST("POST"), HEAD("HEAD"),TRACE("TRACE"), OPTIONS("OPTIONS");
        private String value;

        Method(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
