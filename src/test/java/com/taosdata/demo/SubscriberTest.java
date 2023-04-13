package com.taosdata.demo;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class SubscriberTest {

    Subscriber subscriber = new Subscriber();

    @Test
    void formatTimestamp() {
        long ts = 0;
        String actual = (String) subscriber.formatTimestamp(ts, "ms");
        Assert.assertEquals("1970-01-01 08:00:00.000", actual);

        actual = (String) subscriber.formatTimestamp(ts, "us");
        Assert.assertEquals("1970-01-01 08:00:00.000000", actual);

        actual = (String) subscriber.formatTimestamp(ts, "ns");
        Assert.assertEquals("1970-01-01 08:00:00.000000000", actual);

        actual = (String) subscriber.formatTimestamp(1678246449579L, "ms");
        Assert.assertEquals("2023-03-08 11:34:09.579", actual);

        actual = (String) subscriber.formatTimestamp(1678246449579123L, "us");
        Assert.assertEquals("2023-03-08 11:34:09.579123", actual);

        actual = (String) subscriber.formatTimestamp(1678246449579123456L, "ns");
        Assert.assertEquals("2023-03-08 11:34:09.579123456", actual);
    }
}