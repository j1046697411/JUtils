package org.jzl.net.internal;

import org.jzl.net.Headers;
import org.jzl.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public class HttpHeaders implements Headers {
    private String[] mNameAndValues;

    private HttpHeaders(String... nameAndValues) {
        this.mNameAndValues = nameAndValues;
    }


    @Override
    public Set<String> names() {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        int size = size();
        for (int i = 0; i < size; i++) {
            names.add(name(i));
        }
        return Collections.unmodifiableSet(names);
    }

    @Override
    public boolean hasHeader(String name) {
        return values(name).size() != 0;
    }

    @Override
    public List<String> values(String name) {
        List<String> values = new ArrayList<>();
        int size = size();
        for (int i = 0; i < size; i++) {
            if (name(i).equalsIgnoreCase(name)) {
                values.add(value(i));
            }
        }
        return Collections.unmodifiableList(values);
    }

    @Override
    public String get(String name) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (name(i).equalsIgnoreCase(name)) {
                return value(i);
            }
        }
        return null;
    }

    @Override
    public int size() {
        return mNameAndValues.length / 2;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String name(int index) {
        return mNameAndValues[index * 2];
    }

    @Override
    public String value(int index) {
        return mNameAndValues[index * 2 + 1];
    }

    @Override
    public String toString() {
        Set<String> names = names();
        StringBuilder builder = new StringBuilder();
        for (String name : names){
            List<String> values = values(name);
            builder.append(name).append("(").append(values.size()).append(")").append(":").append(values).append("\n");
        }
        return builder.toString();
    }

    public static class Builder {
        private List<String> mNameAndValues;

        public Builder() {
            mNameAndValues = new ArrayList<>();
        }

        public Builder addAll(Headers headers) {
            int size = headers.size();
            for (int i = 0; i < size; i++) {
                add(headers.name(i), headers.value(i));
            }
            return this;
        }

        public Builder addLine(String line) {
            int index = line.indexOf(":", 1);
            if (index == -1) {
                throw new RuntimeException();
            }
            add(line.substring(0, index), line.substring(index + 1));
            return this;
        }

        public Builder setLenient(String line) {
            int index = line.indexOf(":", 1);
            if (index != -1) {
                add(line.substring(0, index), line.substring(index + 1));
            } else {
                add("", line);
            }
            return this;
        }

        public Builder setLenient(String name, String value) {
            return add(name, value);
        }

        public Builder add(String name, String value) {
            ObjectUtil.requireNonNull(name);
            ObjectUtil.requireNonNull(value);

            mNameAndValues.add(name);
            mNameAndValues.add(value.trim());
            return this;
        }

        public Headers build() {
            return new HttpHeaders(this.mNameAndValues.toArray(new String[mNameAndValues.size()]));
        }
    }
}
