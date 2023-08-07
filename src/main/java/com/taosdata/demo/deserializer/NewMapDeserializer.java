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
    public static final String CALCULATE_LATENCY = "deserializer.calculate.latency";

    public static final String LATENCY = "__LATENCY__";

    @Getter
    private String timestampFormat = "long";
    @Getter
    private String timestampPrecision = "ms";
    private boolean binaryAsString = true;
    private boolean calculateLatency = false;

    @Override
    public void configure(Map<?, ?> configs) {
        if (configs.containsKey(TIMESTAMP_FORMAT))
            timestampFormat = (String) configs.get(TIMESTAMP_FORMAT);
        if (configs.containsKey(TIMESTAMP_PRECISION))
            timestampPrecision = (String) configs.get(TIMESTAMP_PRECISION);
        if (configs.containsKey(BINARY_AS_STRING))
            binaryAsString = Boolean.parseBoolean((String) configs.get(BINARY_AS_STRING));
        if (configs.containsKey(CALCULATE_LATENCY))
            calculateLatency = Boolean.parseBoolean((String) configs.get(CALCULATE_LATENCY));
    }

    @Override
    public Map<String, Object> deserialize(ResultSet rs, String topic,
            String dbName) throws DeserializerException, SQLException {
        Map<String, Object> map = new LinkedHashMap<>();

        boolean isFirstTimestamp = true;
        long ts = System.currentTimeMillis();

        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String colName = metaData.getColumnLabel(i);
            int colType = metaData.getColumnType(i);

            Object value;
            if (Types.TIMESTAMP == colType) {
                value = TimestampUtil.transform(rs.getObject(colName), timestampFormat, timestampPrecision);
                if (isFirstTimestamp) {
                    ts = rs.getLong(colName);
                    isFirstTimestamp = false;
                }
            } else if (binaryAsString && Types.BINARY == colType) {
                value = rs.getString(colName);
            } else {
                value = rs.getObject(colName);
            }
            map.put(colName, value);
        }

        if (calculateLatency) {
            long latency = calculateLatency(ts);
            map.put(LATENCY, latency);
        }

        return map;
    }

    private long calculateLatency(long ts) {
        long now = System.currentTimeMillis();
        if (ts > 9999_999_999_999_999L)
            return now * 1000_000 - ts;
        else if (ts > 9999_999_999_999L)
            return now * 1000 - ts;
        else
            return now - ts;
    }

}