package com.taosdata.demo.util;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;

public class TimestampUtilTest {

    @Test
    public void transform() {
        Object actual = TimestampUtil.transform(1678246449579L, "datetime", "ms");
        Assert.assertEquals("2023-03-08 11:34:09.579", actual);
    }

    @Test
    public void testFormatObject() {
        // given
        final Object ms = 1678246449579L;
        final Object us = 1678246449579123L;
        final Object ns = 1678246449579123456L;
        final Timestamp ts1 = Timestamp.from(Instant.ofEpochSecond(1678246449, 579000000));
        final Timestamp ts2 = Timestamp.from(Instant.ofEpochSecond(1678246449, 579123000));
        final Timestamp ts3 = Timestamp.from(Instant.ofEpochSecond(1678246449, 579123456));
        // when and then
        String actual = TimestampUtil.format(ms, "ms");
        Assert.assertEquals("2023-03-08 11:34:09.579", actual);

        actual = TimestampUtil.format(us, "us");
        Assert.assertEquals("2023-03-08 11:34:09.579123", actual);

        actual = TimestampUtil.format(ns, "ns");
        Assert.assertEquals("2023-03-08 11:34:09.579123456", actual);

        actual = TimestampUtil.format(ts1, "ms");
        Assert.assertEquals("2023-03-08 11:34:09.579", actual);

        actual = TimestampUtil.format(ts2, "us");
        Assert.assertEquals("2023-03-08 11:34:09.579123", actual);

        actual = TimestampUtil.format(ts3, "ns");
        Assert.assertEquals("2023-03-08 11:34:09.579123456", actual);
    }

    @Test
    public void testFormatLong() {
        long ts = 0;
        String actual = TimestampUtil.format(ts, "ms");
        Assert.assertEquals("1970-01-01 08:00:00.000", actual);

        actual = TimestampUtil.format(ts, "us");
        Assert.assertEquals("1970-01-01 08:00:00.000000", actual);

        actual = TimestampUtil.format(ts, "ns");
        Assert.assertEquals("1970-01-01 08:00:00.000000000", actual);

        actual = TimestampUtil.format(1678246449579L, "ms");
        Assert.assertEquals("2023-03-08 11:34:09.579", actual);

        actual = TimestampUtil.format(1678246449579123L, "us");
        Assert.assertEquals("2023-03-08 11:34:09.579123", actual);

        actual = TimestampUtil.format(1678246449579123456L, "ns");
        Assert.assertEquals("2023-03-08 11:34:09.579123456", actual);
    }
}