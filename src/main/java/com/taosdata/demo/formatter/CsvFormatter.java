package com.taosdata.demo.formatter;

import com.taosdata.jdbc.tmq.ConsumerRecord;
import lombok.Builder;

import java.util.Map;

@Builder
public class CsvFormatter implements Formatter {

    private boolean withTitle;
    private char delimiter;
    private boolean withPartitionOffset;

    private boolean notFirst;

    @Override
    public String format(ConsumerRecord<Map<String, Object>> record) {
        if (withTitle && !notFirst) {
            notFirst = true;
            return title(record.value()) + "\n" + line(record);
        }
        return line(record);
    }

    private String title(Map<String, Object> record) {
        StringBuilder title = new StringBuilder();
        if (withPartitionOffset) {
            title.append("dbName").append(delimiter);
            title.append("topic").append(delimiter);
            title.append("vGroupId").append(delimiter);
            title.append("offset").append(delimiter);
        }
        for (String key : record.keySet()) {
            title.append(key).append(delimiter);
        }
        return title.substring(0, title.length() - 1);
    }

    private String line(ConsumerRecord<Map<String, Object>> record) {
        StringBuilder sb = new StringBuilder();
        if (withPartitionOffset) {
            sb.append(record.getDbName()).append(delimiter);
            sb.append(record.getTopic()).append(delimiter);
            sb.append(record.getVGroupId()).append(delimiter);
            sb.append(record.getOffset()).append(delimiter);
        }

        Map<String, Object> value = record.value();
        for (String key : value.keySet()) {
            sb.append(value.get(key)).append(delimiter);
        }
        return sb.substring(0, sb.length() - 1);
    }

}