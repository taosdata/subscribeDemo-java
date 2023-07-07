package com.taosdata.demo.config;

import com.taosdata.demo.formatter.CsvFormatter;
import com.taosdata.demo.formatter.Formatter;
import com.taosdata.demo.formatter.JsonFormatter;
import com.taosdata.demo.formatter.KeyValueFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class BeanConfig {
    @Value("${record-formatter.type: csv}")
    private String formatter;
    @Value("${record-formatter.csv.delimiter:,}")
    private String csvDelimiter;
    @Value("${record-formatter.csv.with-title:false}")
    private boolean csvWithTitle;
    @Value("${record-formatter.with-partition-offset: false}")
    private boolean withPartitionOffset;
    @Value("${subscriber.output-file}")
    private String outputFile;

    @Bean
    public Formatter formatter() {
        if ("json".equalsIgnoreCase(formatter)) {
            return JsonFormatter.builder().withPartitionOffset(withPartitionOffset).build();
        }
        if ("csv".equalsIgnoreCase(formatter)) {
            if ("\\t".equals(csvDelimiter)) {
                return CsvFormatter.builder()
                                   .withTitle(csvWithTitle)
                                   .delimiter('\t')
                                   .withPartitionOffset(withPartitionOffset)
                                   .build();
            } else {
                return CsvFormatter.builder()
                                   .withTitle(csvWithTitle)
                                   .delimiter(csvDelimiter.charAt(0))
                                   .withPartitionOffset(withPartitionOffset)
                                   .build();
            }
        }
        return KeyValueFormatter.builder().withPartitionOffset(withPartitionOffset).build();
    }

    @Bean
    public PrintWriter writer() throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
    }
}
