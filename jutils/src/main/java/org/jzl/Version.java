package org.jzl;

import org.jzl.utils.text.Joiner;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/02
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public enum Version {
    V_0_0_1(" ^ _ ^ ", "0.0.1");

    public static final Version CURRENT_VALUE = V_0_0_1;

    private Version(String name, String value) {
        this.name = name;
        this.value = value;
        this.userAgent = Joiner.on("/").join(name).join(value).toString();
    }

    private String userAgent;
    private String value;
    private String name;

    public String getUserAgent() {
        return userAgent;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
