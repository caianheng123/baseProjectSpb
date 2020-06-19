package com.faw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiazh on 2020/6/18.
 */
@Component
@ConfigurationProperties(prefix = "onlinedata.dirobj")
@Data
public class OnlineDataDirConfig {

    private Map<String,String> targetMap;

    private Map<String,List<String>> shareMaplist;
}
