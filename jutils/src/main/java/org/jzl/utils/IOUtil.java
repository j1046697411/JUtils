package org.jzl.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class IOUtil {
    public static int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOUtil() {
        throw new RuntimeException();
    }

    public static void copyStream(InputStream in, OutputStream out, byte[] buffer) throws IOException {
        ObjectUtil.requireNonNull(in);
        ObjectUtil.requireNonNull(out);
        ObjectUtil.requireNonNull(buffer);

        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.flush();
    }

    public static void copyStream(InputStream in, OutputStream out, int bufferSize) throws IOException {
        copyStream(in, out, new byte[bufferSize]);
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        copyStream(in, out, DEFAULT_BUFFER_SIZE);
    }

    public static byte[] copyStreamToByteArray(InputStream in, int estimatedSize) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(0, estimatedSize));
        copyStream(in, out);
        return out.toByteArray();
    }

    public static String copyStreamToString(InputStream in, int estimatedSize, Charset charset) throws IOException {
        byte[] bytes = copyStreamToByteArray(in, estimatedSize);
        return new String(bytes, 0, bytes.length, charset);
    }

    public static void close(Closeable closeable) {
        if (ObjectUtil.nonNull(closeable)) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static void flush(Flushable flush) {
        if (ObjectUtil.nonNull(flush)) {
            try {
                flush.flush();
            } catch (IOException e) {
            }
        }
    }
}
