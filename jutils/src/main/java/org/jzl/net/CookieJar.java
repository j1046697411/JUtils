package org.jzl.net;

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
public interface CookieJar {
    void saveCookies(URL url, List<Cookie> cookies);

    List<Cookie> loadForRequest(URL url);
}
