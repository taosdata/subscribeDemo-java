package com.taosdata.demo.deserializer;

import com.taosdata.demo.util.TimestampUtil;
import com.taosdata.jdbc.tmq.Deserializer;
import com.taosdata.jdbc.tmq.DeserializerException;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

public class NewMapDeserializer implements Deserializer<Map<String, Object>> {

    public static final String TIMESTAMP_FORMAT = "deserializer.timestamp.format";
    public static final String TIMESTAMP_PRECISION = "deserializer.timestamp.precision";
    public static final String BINARY_AS_STRING = "deserializer.binary.as.string";

    @Getter
    private String timestampFormat = "long";
    @Getter
    private String timestampPrecision = "ms";
    private boolean binaryAsString = true;

    @Override
    public void configure(Map<?, ?> configs) {
        if (configs.containsKey(TIMESTAMP_FORMAT)) {
            timestampFormat = (String) configs.get(TIMESTAMP_FORMAT);
        }
        if (configs.containsKey(TIMESTAMP_PRECISION)) {
            timestampPrecision = (String) configs.get(TIMESTAMP_PRECISION);
        }
        if (configs.containsKey(BINARY_AS_STRING)) {
            binaryAsString = Boolean.parseBoolean((String) configs.get(BINARY_AS_STRING));
        }
    }

    @Override
    public Map<String, Object> deserialize(ResultSet data, String topic,
            String dbName) throws DeserializerException, SQLException {
        Map<String, Object> map = new LinkedHashMap<>();

        ResultSetMetaData metaData = data.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String colName = metaData.getColumnLabel(i);
            int colType = metaData.getColumnType(i);

            Object value;
            if (Types.TIMESTAMP == colType) {
                value = TimestampUtil.transform(data.getObject(colName), timestampFormat, timestampPrecision);
            } else if (binaryAsString && Types.BINARY == colType) {
                value = data.getString(colName);
            } else {
                value = data.getObject(colName);
            }
            map.put(colName, value);
        }

        return map;
    }

}