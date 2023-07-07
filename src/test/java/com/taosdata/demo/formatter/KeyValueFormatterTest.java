package com.taosdata.demo.formatter;

import com.taosdata.demo.entity.RecordMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyValueFormatterTest {

    @Test
    public void without_partition_offset() {
        // when
        Formatter f = KeyValueFormatter.builder().build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "ts:123\tf1:1\tf2:2\tf3:3\tf4:4\tf5:5.55\tf6:6.6666\tf7:7\tf8:8\tf9:9\tf10:10\tf11:11.11\tf12:12.2222\tf13:abc\tf14:北京\t";
        assertEquals(expect, actual);
    }

    @Test
    public void with_partition_offset() {
        // when
        Formatter f = KeyValueFormatter.builder().withPartitionOffset(true).build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "dbName:dbName\ttopic:topicName\tvGroupId:1\toffset:1\tts:123\tf1:1\tf2:2\tf3:3\tf4:4\tf5:5.55\tf6:6.6666\tf7:7\tf8:8\tf9:9\tf10:10\tf11:11.11\tf12:12.2222\tf13:abc\tf14:北京\t";
        assertEquals(expect, actual);
    }
}