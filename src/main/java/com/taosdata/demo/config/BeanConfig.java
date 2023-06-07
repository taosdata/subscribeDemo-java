package com.taosdata.demo.config;

import com.taosdata.demo.formatter.CsvFormatter;
import com.taosdata.demo.formatter.Formatter;
import com.taosdata.demo.formatter.JsonFormatter;
import com.taosdata.demo.formatter.KeyValueFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Value("${record-formatter.type: csv}")
    private String formatter;
    @Value("${record-formatter.csv.delimiter:,}")
    private String csvDelimiter;
    @Value("${record-formatter.csv.with-title:false}")
    private boolean csvWithTitle;

    @Bean
    public Formatter formatter() {
        if ("json".equalsIgnoreCase(formatter)) {
            return new JsonFormatter();
        }
        if ("csv".equalsIgnoreCase(formatter)) {
            if ("\\t".equals(csvDelimiter))
                return new CsvFormatter(csvWithTitle, '\t');
            else
                return new CsvFormatter(csvWithTitle, csvDelimiter.charAt(0));
        }
        return new KeyValueFormatter();
    }
}
