package com.taosdata.demo.deserializer;

import com.taosdata.demo.util.TimestampUtil;
import com.taosdata.jdbc.tmq.Deserializer;
import com.taosdata.jdbc.tmq.DeserializerException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

public class NewMapDeserializer implements Deserializer<Map<String, Object>> {

    public static final String TIMESTAMP_FORMAT = "deserializer.timestamp.format";
    public static final String TIMESTAMP_PRECISION = "deserializer.timestamp.precision";

    private String timestampFormat = "long";
    private String timestampPrecision = "ms";

    @Override
    public void configure(Map<?, ?> configs) {
        if (configs.containsKey(TIMESTAMP_FORMAT)) {
            timestampFormat = (String) configs.get(TIMESTAMP_FORMAT);
        }
        if (configs.containsKey(TIMESTAMP_PRECISION)) {
            timestampPrecision = (String) configs.get(TIMESTAMP_PRECISION);
        }
    }

    @Override
    public Map<String, Object> deserialize(ResultSet data) throws DeserializerException, SQLException {
        Map<String, Object> map = new LinkedHashMap<>();

        ResultSetMetaData metaData = data.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String colName = metaData.getColumnLabel(i);
            int colType = metaData.getColumnType(i);

            Object value;
            if (Types.TIMESTAMP == colType) {
                value = TimestampUtil.transform(data.getObject(colName), timestampFormat, timestampPrecision);
            } else {
                value = data.getObject(colName);
            }
            map.put(colName, value);
        }

        return map;
    }

}