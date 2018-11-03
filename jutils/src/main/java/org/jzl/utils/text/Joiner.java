package org.jzl.utils.text;

import org.jzl.utils.ObjectUtil;
import org.jzl.utils.StringUtil;

/**
 * <pre>
 *     author : jzl
 *     time   : 2018/09/06
 *     desc   : 字符串快速拼接工具
 *     version: 1.0
 * </pre>
 */
public class Joiner {
    private String mPrefix;
    private String mDelimiter;
    private String mSuffix;

    private StringBuilder mValue;

    private String mEmptyValue;

    private Joiner(CharSequence prefix, String delimiter, String suffix) {
        ObjectUtil.requireNonNull(prefix);
        ObjectUtil.requireNonNull(delimiter);
        ObjectUtil.requireNonNull(suffix);

        this.mPrefix = prefix.toString();
        this.mDelimiter = delimiter;
        this.mSuffix = suffix;
        mEmptyValue = mPrefix + mSuffix;

    }

    public static Joiner on(CharSequence prefix, String delimiter, String suffix) {
        return new Joiner(prefix, delimiter, suffix);
    }

    public static Joiner on(String delimiter) {
        return on(StringUtil.STRING_EMPTY, delimiter, StringUtil.STRING_EMPTY);
    }

    private StringBuilder prepareBuilder() {
        if (mValue == null) {
            mValue = new StringBuilder(mPrefix);
        } else {
            mValue.append(mDelimiter);
        }
        return mValue;
    }

    public Joiner join(CharSequence text) {
        prepareBuilder().append(text);
        return this;
    }

    public Joiner join(CharSequence... texts) {
        int length = texts.length;
        for (int i = 0; i < length; i++) {
            prepareBuilder().append(texts[i]);
        }
        return this;
    }

    public Joiner merge(Joiner joiner) {
        ObjectUtil.requireNonNull(joiner);
        if (joiner.mValue != null) {
            prepareBuilder().append(joiner.mValue, joiner.mPrefix.length(), joiner.mValue.length());
        }
        return this;
    }

    @Override
    public String toString() {
        if (mValue == null) {
            return mEmptyValue;
        } else {
            if (StringUtil.STRING_EMPTY.equals(mSuffix)) {
                return mValue.toString();
            } else {
                int length = mValue.length();
                String result = mValue.append(mSuffix).toString();
                mValue.setLength(length);
                return result;
            }
        }
    }

    public int length() {
        return mValue == null ? mEmptyValue.length() : (mValue.length() + mSuffix.length());
    }
}
