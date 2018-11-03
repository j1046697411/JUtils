package org.jzl.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/10/31
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public interface Response {

    int getResponseCode();

    Body getBody();
    Headers getHeaders();

    interface Body {
        int bodyLength();

        InputStream getStream();

        String getText() throws IOException;

        <T> T get(Converter<T> converter) throws IOException;

        interface Converter<T> {
            T transfer(Body body) throws IOException;
        }
    }
}
