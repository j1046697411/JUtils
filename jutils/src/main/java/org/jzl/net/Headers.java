package org.jzl.net;

import java.util.List;
import java.util.Set;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public interface Headers {
    String COOKIE = "cookie";
    String SET_COOKIE = "Set-cookie";
    String USER_AGENT = "User-Agent";

    Set<String> names();

    List<String> values(String name);

    String get(String name);

    int size();

    String name(int index);

    String value(int index);

    boolean hasHeader(String name);

    boolean isEmpty();
}
