package com.taosdata.demo.formatter;

import com.taosdata.demo.entity.RecordMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvFormatterTest {

    @Test
    public void title1() {
        // when
        CsvFormatter f = CsvFormatter.builder().withTitle(false).delimiter('\t').build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "123\t1\t2\t3\t4\t5.55\t6.6666\t7\t8\t9\t10\t11.11\t12.2222\tabc\t北京";
        assertEquals(expect, actual);
    }

    @Test
    public void title2() {
        // when
        CsvFormatter f = CsvFormatter.builder().withTitle(true).delimiter('\t').build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "ts\tf1\tf2\tf3\tf4\tf5\tf6\tf7\tf8\tf9\tf10\tf11\tf12\tf13\tf14\n" +
                "123\t1\t2\t3\t4\t5.55\t6.6666\t7\t8\t9\t10\t11.11\t12.2222\tabc\t北京";
        assertEquals(expect, actual);
    }

    @Test
    public void delimiter1() {
        // when
        CsvFormatter f = CsvFormatter.builder().withTitle(false).delimiter(',').build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "123,1,2,3,4,5.55,6.6666,7,8,9,10,11.11,12.2222,abc,北京";
        assertEquals(expect, actual);
    }

    @Test
    public void delimiter2() {
        // when
        CsvFormatter f = CsvFormatter.builder().withTitle(true).delimiter(',').build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "ts,f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14\n" +
                "123,1,2,3,4,5.55,6.6666,7,8,9,10,11.11,12.2222,abc,北京";
        assertEquals(expect, actual);
    }

    @Test
    public void with_partition_offset() {
        // when
        CsvFormatter f = CsvFormatter.builder().withTitle(true).delimiter(',').withPartitionOffset(true).build();
        String actual = f.format(RecordMap.m);
        // then
        String expect = "dbName,topic,vGroupId,offset,ts,f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14\n" +
                "dbName,topicName,1,1,123,1,2,3,4,5.55,6.6666,7,8,9,10,11.11,12.2222,abc,北京";
        assertEquals(expect, actual);
    }

}