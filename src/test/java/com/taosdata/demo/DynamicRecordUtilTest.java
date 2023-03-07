package com.taosdata.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DynamicRecordUtilTest {
    @Value("${schemaConfig}")
    private String schemaTxt;

    @Test
    public void getDynamicRecordClass() throws Exception {
        // given
        String schema = new String(Files.readAllBytes(Paths.get(schemaTxt)));
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
        String schema = new String(Files.readAllBytes(Paths.get(schemaTxt)));
        Class<?> clazz = util.getDynamicRecordClass(schema);

        // when
        Class<?> deserializer = util.getDynamicRecordDeserializer(clazz);

        // then
        Assert.assertEquals("com.taosdata.demo.DynamicRecordDeserializerClass", deserializer.getTypeName());
        Assert.assertEquals("com.taosdata.jdbc.tmq.ReferenceDeserializer", deserializer.getSuperclass().getTypeName());
    }

}