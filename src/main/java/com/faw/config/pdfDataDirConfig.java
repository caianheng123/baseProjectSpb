package com.faw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Jiazh on 2020/6/18.
 */
@Component
@ConfigurationProperties(prefix = "pdfdata.dirobj")
@Data
public class pdfDataDirConfig {

    private Map<String,String> targetMap;

    private Map<String,List<String>> shareMaplist;
}
