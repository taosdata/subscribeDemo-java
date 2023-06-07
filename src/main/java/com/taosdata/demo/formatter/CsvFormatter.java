package com.taosdata.demo.formatter;

import java.util.Map;

public class CsvFormatter implements Formatter {

    private final boolean withTitle;
    private final char delimiter;
    private boolean first = true;

    public CsvFormatter(boolean withTitle, char delimiter) {
        this.withTitle = withTitle;
        this.delimiter = delimiter;
    }

    @Override
    public String format(Map<String, Object> record) {
        if (withTitle && first) {
            first = false;
            return title(record) + "\n" + line(record);
        }
        return line(record);
    }

    private String title(Map<String, Object> record) {
        StringBuilder title = new StringBuilder();
        for (String key : record.keySet()) {
            title.append(key).append(delimiter);
        }
        return title.substring(0, title.length() - 1);
    }

    private String line(Map<String, Object> record) {
        StringBuilder sb = new StringBuilder();
        for (String key : record.keySet()) {
            sb.append(record.get(key)).append(delimiter);
        }
        return sb.substring(0, sb.length() - 1);
    }

}