package com.taosdata.demo.formatter;

import com.taosdata.demo.entity.RecordMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFormatterTest {

    @Test
    public void without_partition_offset() {
        // when
        JsonFormatter f = new JsonFormatter(false);
        String actual = f.format(RecordMap.m);
        // then
        String expect = "{\"ts\":123,\"f1\":1,\"f2\":2,\"f3\":3,\"f4\":4,\"f5\":5.55,\"f6\":6.6666,\"f7\":7,\"f8\":8," +
                "\"f9\":9,\"f10\":10,\"f11\":11.11,\"f12\":12.2222,\"f13\":\"abc\",\"f14\":\"北京\"}";
        assertEquals(expect, actual);
    }

    @Test
    public void with_partition_offset() {
        // when
        JsonFormatter f = new JsonFormatter(true);
        String actual = f.format(RecordMap.m);
        // then
        String expect = "{\"dbName\":\"dbName\",\"topic\":\"topicName\",\"vGroupId\":1,\"offset\":1," +
                "\"ts\":123,\"f1\":1,\"f2\":2,\"f3\":3,\"f4\":4,\"f5\":5.55,\"f6\":6.6666,\"f7\":7,\"f8\":8,\"f9\":9," +
                "\"f10\":10,\"f11\":11.11,\"f12\":12.2222,\"f13\":\"abc\",\"f14\":\"北京\"}";
        assertEquals(expect, actual);
    }

}