package org.jzl.utils;

import org.jzl.fun.IPredicate;
import org.jzl.utils.text.Joiner;

import java.util.regex.Pattern;

/**
 * Created by JZL on 2018/7/18
 */
public final class StringUtil {

    public static String STRING_EMPTY = "";

    private StringUtil() {
        throw new RuntimeException();
    }

    public static boolean isEmpty(CharSequence text) {
        return ObjectUtil.isNull(text) || text.length() == 0;
    }

    public static String trim(String text) {
        if (ObjectUtil.nonNull(text)) {
            return text.trim();
        }
        return text;
    }

    public static String trimAll(String text, IPredicate.ICharPredicate predicate) {
        if (isEmpty(text) || ObjectUtil.isNull(predicate)) {
            return text;
        }
        StringBuilder builder = new StringBuilder();
        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (!predicate.test(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static String trimSpaceAll(String text) {
        return trimAll(ObjectUtil.requireNonNull(text), new IPredicate.ICharPredicate() {
            @Override
            public boolean test(char c) {
                return Character.isSpaceChar(c);
            }
        });
    }

    public static String trimWhitespaceAll(String text) {
        return trimAll(text, new IPredicate.ICharPredicate() {
            @Override
            public boolean test(char c) {
                return Character.isWhitespace(c);
            }
        });
    }

    public static <T> boolean matches(String text, IPredicate<String> predicate) {
        return ObjectUtil.requireNonNull(predicate).test(ObjectUtil.requireNonNull(text));
    }

    public static boolean matches(String text, final Pattern pattern) {
        return matches(text, new IPredicate<String>() {
            @Override
            public boolean test(String target) {
                return pattern.matcher(target).matches();
            }
        });
    }

    public static boolean matches(String text, String regex) {
        return matches(text, Pattern.compile(regex));
    }

    public static String replaceAll(String text, Pattern pattern, String replacement) {
        return ObjectUtil.requireNonNull(pattern)
                .matcher(ObjectUtil.requireNonNull(text))
                .replaceAll(ObjectUtil.requireNonNull(replacement));
    }

    public static String replaceAll(String text, String target, String replacement) {
        return replaceAll(text, Pattern.compile(target), replacement);
    }

    public static int length(CharSequence text) {
        if (isEmpty(text)) {
            return 0;
        }
        return text.length();
    }

    public static String toLowerCase(String text) {
        if (isEmpty(text)) {
            return text;
        }
        return text.toLowerCase();
    }

    public static String toUpperCase(String text) {
        if (isEmpty(text)) {
            return text;
        }
        return text.toUpperCase();
    }

    public static String toHexString(byte[] bytes, String delimiter, boolean isUpperCase) {
        ObjectUtil.requireNonNull(bytes);
        Joiner joiner = Joiner.on(ObjectUtil.get(delimiter, STRING_EMPTY));
        for (byte b : bytes) {
            int i = b & 0xff;
            if (i < 16) {
                joiner.join('0' + Integer.toHexString(i));
            } else {
                joiner.join(Integer.toHexString(i));
            }
        }
        if (isUpperCase) {
            return joiner.toString().toUpperCase();
        } else {
            return joiner.toString();
        }
    }

    public static String toHexString(byte[] bytes, String delimiter) {
        return toHexString(bytes, delimiter, true);
    }

    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, ":");
    }

    public static Joiner join(CharSequence prefix, String delimiter, String suffix) {
        return Joiner.on(prefix, delimiter, suffix);
    }

    public static Joiner join(String delimiter) {
        return Joiner.on(delimiter);
    }
}
