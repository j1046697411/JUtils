package org.jzl.utils;

/**
 * Created by JZL on 2018/7/18
 */
public final class ObjectUtil {

    private ObjectUtil() {
        throw new RuntimeException();
    }

    public static boolean isNull(Object target) {
        return target == null;
    }

    public static boolean nonNull(Object target) {
        return target != null;
    }

    public static <T> T requireNonNull(T target, String message) {
        if (isNull(target)) {
            throw new NullPointerException(message);
        }
        return target;
    }

    public static <T> T requireNonNull(T target) {
        return requireNonNull(target, "");
    }

    public static <T> T get(T target, T defaultValue) {
        return isNull(target) ? defaultValue : target;
    }

    public static boolean equals(Object obj1, Object obj2) {
        if (isNull(obj1) || isNull(obj2)) {
            return false;
        }
        return obj1.equals(obj2);
    }

    public static int hashCode(Object target) {
        if (nonNull(target)) {
            return target.hashCode();
        }
        return -1;
    }

    public static String toString(Object target) {
        return String.valueOf(target);
    }
}
