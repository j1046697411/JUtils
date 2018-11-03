package org.jzl.utils;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/02
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */

//PSTM=1541124010; expires=Thu, 31-Dec-37 23:55:55 GMT; max-age=2147483647; path=/; domain=.baidu.com
public class DateUtil {
    //EEE, dd MMM yyyy HH:mm:ss 'GMT' 标准的cookie时间格式

    //Sun, 18 Jan 2038 00:00:00 GMT www.iamwawa.cn
    //Thu, 31-Dec-37 23:55:55 GMT www.baidu.com

    private static final ThreadLocal<DateFormat> STANDARD_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        }
    };

    private static final String[] BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS = new String[]{
            // HTTP formats required by RFC2616 but with any timezone.
            "EEE, dd MMM yyyy HH:mm:ss zzz", // RFC 822, updated by RFC 1123 with any TZ
            "EEEE, dd-MMM-yy HH:mm:ss zzz", // RFC 850, obsoleted by RFC 1036 with any TZ.
            "EEE MMM d HH:mm:ss yyyy", // ANSI C's asctime() format
            // Alternative formats.
            "EEE, dd-MMM-yyyy HH:mm:ss z",
            "EEE, dd-MMM-yyyy HH-mm-ss z",
            "EEE, dd MMM yy HH:mm:ss z",
            "EEE dd-MMM-yyyy HH:mm:ss z",
            "EEE dd MMM yyyy HH:mm:ss z",
            "EEE dd-MMM-yyyy HH-mm-ss z",
            "EEE dd-MMM-yy HH:mm:ss z",
            "EEE dd MMM yy HH:mm:ss z",
            "EEE,dd-MMM-yy HH:mm:ss z",
            "EEE,dd-MMM-yyyy HH:mm:ss z",
            "EEE, dd-MM-yyyy HH:mm:ss z",

            /* RI bug 6641315 claims a cookie of this format was once served by www.yahoo.com */
            "EEE MMM d yyyy HH:mm:ss z",
    };

    private static DateFormat[] BROWSER_COMPATIBLE_DATE_FORMATS = new DateFormat[BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length];

    public static Date parseCookieDate(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        Date date = parseDate(value, STANDARD_DATE_FORMAT.get());
        if (ObjectUtil.nonNull(date)){
            return date;
        }
        for (int i = 0; i < BROWSER_COMPATIBLE_DATE_FORMATS.length; i++) {
            if (ObjectUtil.isNull(BROWSER_COMPATIBLE_DATE_FORMATS[i])) {
                DateFormat dateFormat = new SimpleDateFormat(BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS[i], Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                BROWSER_COMPATIBLE_DATE_FORMATS[i] = dateFormat;
            }
            date = parseDate(value, BROWSER_COMPATIBLE_DATE_FORMATS[i]);
            if (ObjectUtil.nonNull(date)) {
                return date;
            }
        }
        return null;
    }

    public static Date parseDate(String value, DateFormat format) {
        ParsePosition position = new ParsePosition(0);
        Date date = format.parse(value, position);
        if (position.getIndex() == value.length()) {
            return date;
        } else {
            return null;
        }

    }

    public static String format(Date date) {
        return format(STANDARD_DATE_FORMAT.get(), date);
    }

    public static String format(DateFormat format, Date date) {
        return format.format(date);
    }
}
