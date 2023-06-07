package com.taosdata.demo;

import com.taosdata.demo.util.DynamicRecordUtil;
import org.junit.Assert;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;


public class DynamicRecordTimestampUtilTest {
    String schema = "long ts; int c1; float c2;";

    @Test
    public void getDynamicRecordClass() throws Exception {
        // given
        DynamicRecordUtil util = new DynamicRecordUtil();
        // when
        Class<?> clazz = util.getDynamicRecordClass(schema);

        // then
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor tsDescriptor = Arrays.stream(beanInfo.getPropertyDescriptors())
                                                .filter(i -> i.getName().equals("ts"))
                                                .findFirst()
                                                .get();

        Method setTs = tsDescriptor.getWriteMethod();
        Method getTs = tsDescriptor.getReadMethod();

        long ts = System.currentTimeMillis();
        Object instance = clazz.newInstance();
        setTs.invoke(instance, ts);

        Assert.assertEquals(ts, getTs.invoke(instance));
    }

    @Test
    public void getDynamicRecordDeserializer() throws Exception {
        // given
        DynamicRecordUtil util = new DynamicRecordUtil();
        Class<?> clazz = Class.forName("com.taosdata.jdbc.tmq.ReferenceDeserializer");

        // when
        Class<?> deserializer = util.getDynamicRecordDeserializer(clazz);

        // then
        Assert.assertEquals("com.taosdata.demo.DynamicRecordDeserializerClass", deserializer.getTypeName());
        Assert.assertEquals("com.taosdata.jdbc.tmq.ReferenceDeserializer", deserializer.getSuperclass().getTypeName());
    }

}