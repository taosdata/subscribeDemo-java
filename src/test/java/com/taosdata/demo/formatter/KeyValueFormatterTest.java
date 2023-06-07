package com.taosdata.demo.formatter;

import com.taosdata.demo.entity.RecordMap;
import org.junit.Assert;
import org.junit.Test;

public class KeyValueFormatterTest {

    @Test
    public void format() {
        // when
        Formatter f = new KeyValueFormatter();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "ts:123\tf1:1\tf2:2\tf3:3\tf4:4\tf5:5.55\tf6:6.6666\tf7:7\tf8:8\tf9:9\tf10:10\tf11:11.11\tf12:12.2222\tf13:abc\tf14:北京\t";
        Assert.assertEquals(expect, actual);
    }
}