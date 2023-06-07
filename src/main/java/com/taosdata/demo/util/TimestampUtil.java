package com.taosdata.demo.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimestampUtil {

    public static final String MS_PRECISION = "ms";
    public static final String US_PRECISION = "us";
    public static final String NS_PRECISION = "ns";
    public static final String DATETIME_FORMAT = "datetime";

    public static Object transform(Object ts, String format, String precision) {
        if (DATETIME_FORMAT.equalsIgnoreCase(format)) {
            return format(ts, precision);
        }
        return ts;
    }

    public static String format(Object ts, String precision) {
        if (ts instanceof Long) {
            return format((Long) ts, precision);
        }
        if (ts instanceof Timestamp) {
            return format((Timestamp) ts, precision);
        }
        return ts.toString();
    }

    public static String format(Timestamp ts, String precision) {
        if (MS_PRECISION.equalsIgnoreCase(precision)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(ts);
        }
        if (US_PRECISION.equalsIgnoreCase(precision)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            return formatter.format(ts.toLocalDateTime());
        }
        if (NS_PRECISION.equalsIgnoreCase(precision)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
            return formatter.format(ts.toLocalDateTime());
        }
        return String.valueOf(ts.getTime());
    }

    public static String format(Long ts, String precision) {
        if (MS_PRECISION.equalsIgnoreCase(precision)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(new Date(ts));
        }
        if (US_PRECISION.equalsIgnoreCase(precision)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            return formatter.format(LocalDateTime.ofEpochSecond(ts / 1000_000, (int) (ts % 1000_000) * 1000,
                    OffsetDateTime.now().getOffset()));
        }
        if (NS_PRECISION.equalsIgnoreCase(precision)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
            return formatter.format(LocalDateTime.ofEpochSecond(ts / 1000_000_000, (int) (ts % 1000_000_000),
                    OffsetDateTime.now().getOffset()));
        }
        return String.valueOf(ts);
    }
}
