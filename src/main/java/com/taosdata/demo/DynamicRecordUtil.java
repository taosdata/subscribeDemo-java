package com.taosdata.demo;

import com.taosdata.jdbc.tmq.ReferenceDeserializer;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicRecordUtil {
    private static final String dynamicRecordClassName = "com.taosdata.demo.DynamicRecordClass";
    private static final String dynamicRecordDeserializerClassName = "com.taosdata.demo.DynamicRecordDeserializerClass";

    public Class<?> getDynamicRecordDeserializer(Class clazz) {
        TypeDescription.Generic typeDefinitions = TypeDescription.Generic.Builder.parameterizedType(
                ReferenceDeserializer.class, clazz).build();
        DynamicType.Unloaded<?> deserializerType = new ByteBuddy().subclass(typeDefinitions)
                                                                  .name(dynamicRecordDeserializerClassName)
                                                                  .make();
        return deserializerType.load(this.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                               .getLoaded();
    }

    public Class<?> getDynamicRecordClass(String fields) throws Exception {
        return getDynamicRecordClass(toLine(fields));
    }

    private Class<?> getDynamicRecordClass(List<String[]> fields) throws Exception {
        if (fields == null || fields.size() == 0)
            return new ByteBuddy().subclass(Object.class)
                                  .name(dynamicRecordClassName)
                                  .make()
                                  .load(this.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                                  .getLoaded();

        DynamicType.Builder<Object> builder = new ByteBuddy().subclass(Object.class).name(dynamicRecordClassName);
        DynamicType.Builder.FieldDefinition.Optional<Object> field = null;
        for (String[] f : fields) {
            String fieldType = f[0];
            String fieldName = f[1];

            Class clazz;
            try {
                clazz = getFieldType(fieldType);
            } catch (ClassNotFoundException e) {
                throw new Exception("failed to getFieldType: " + f[0] + ":" + f[1] + ", cause: " + e.getMessage(), e);
            }

            if (field == null)
                field = builder.defineProperty(fieldName, clazz);
            else
                field = field.defineProperty(fieldName, clazz);
        }
        assert field != null;
        DynamicType.Unloaded<Object> dynamicType = field.withHashCodeEquals().withToString().make();
        return dynamicType.load(this.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
    }

    private Class getFieldType(String fieldType) throws Exception {
        if (fieldType.equals("byte"))
            return byte.class;
        else if (fieldType.equals("short"))
            return short.class;
        else if (fieldType.equals("int")) {
            return int.class;
        } else if (fieldType.equals("long"))
            return long.class;
        else if (fieldType.equals("float"))
            return float.class;
        else if (fieldType.equals("double"))
            return double.class;
        else if (fieldType.equals("char"))
            return char.class;
        else if (fieldType.equals("boolean"))
            return boolean.class;
        else if (fieldType.equals("byte[]"))
            return byte[].class;
        try {
            return Class.forName(fieldType);
        } catch (ClassNotFoundException e) {
            throw new Exception("failed to get field type: " + fieldType + ", cause: " + e.getMessage(), e);
        }
    }

    private List<String[]> toLine(String schema) {
        schema = schema.replaceAll("\n", "");
        return Arrays.stream(schema.split(";"))
                     .filter(i -> !i.trim().isEmpty())
                     .map(i -> i.trim().replaceAll("\\s+", " "))
                     .map(i -> i.split(" "))
                     .collect(Collectors.toList());
    }
}
