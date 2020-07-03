package com.faw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Jiazh on 2020/7/3.
 */
@Component
@ConfigurationProperties(prefix = "pdfextract.confobj")
@Data
public class PdfClownKeyConfig {

    private Map<String,Integer> superAlmost;

    private Map<String,Integer> stablePassRate;
}
