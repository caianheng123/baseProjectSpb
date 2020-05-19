package com.faw.dataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import com.faw.utils.io.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 *
 * @author liushengbin
 * @email liushengbin7@gmail.com
 * @date 2018/8/19 0:41
 */
@Configuration
@Slf4j
public class DynamicDataSourceConfig {

    @Value("${spring.datasource.druid.kudu.enabled}")
    private Boolean kuduEnabled;

    @Value("${spring.datasource.druid.kudu.url}")
    private String kuduConnUrl;

    private static String CONNECTION_URL = "jdbc:impala://{0}:21050/;AuthMech=1;KrbRealm={1};KrbHostFQDN={0};KrbServiceName=impala";

    @Value("${spring.datasource.druid.kudu.driverClassName}")
    private String kuduDriverClassName;

    @Value("${spring.datasource.druid.kudu.username}")
    private String kuduUsername;

    @Value("${spring.datasource.druid.kudu.password}")
    private String kuduPassword;

    @Bean
    @ConfigurationProperties("spring.datasource.druid.first")
    public DataSource firstDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.second")
    public DataSource secondDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
//    @ConfigurationProperties("spring.datasource.druid.kudu")
    @DependsOn("initKerberos")
    public DataSource kuduDataSource() throws Exception {
        DruidDataSource druidDataSource = new DruidDataSource();
        if(kuduEnabled){
//            String connUrl = "jdbc:impala://192.168.1.46:21050/kudu_test";
            CONNECTION_URL = MessageFormat.format(CONNECTION_URL, PropertiesUtils.getInstance().getProperty("IMPALA_DAEMON"), PropertiesUtils.getInstance().getProperty("KERBEROS_REAM"));

            log.debug("CONNECTION_URL:"+ CONNECTION_URL);
            kuduConnUrl = MessageFormat.format(kuduConnUrl, PropertiesUtils.getInstance().getProperty("IMPALA_DAEMON"), PropertiesUtils.getInstance().getProperty("KERBEROS_REAM"));
            druidDataSource.setDriverClassName(kuduDriverClassName);
            druidDataSource.setUrl(CONNECTION_URL);
            druidDataSource.setMaxActive(100);
            druidDataSource.setMinIdle(5);
            druidDataSource.setMaxWait(10000);
            druidDataSource.setInitialSize(3);
            druidDataSource.setTestWhileIdle(true);
            druidDataSource.setValidationQuery("select 1");
            druidDataSource.setFilters("stat");
            druidDataSource.setName("impala");
            druidDataSource.setUseGlobalDataSourceStat(true);
        }

        return druidDataSource;
    }


    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource firstDataSource, DataSource secondDataSource, DataSource kuduDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.FIRST, firstDataSource);
        targetDataSources.put(DataSourceNames.SECOND, secondDataSource);
        targetDataSources.put(DataSourceNames.KUDU, kuduDataSource);
        return new DynamicDataSource(firstDataSource, targetDataSources);
    }

    @Bean(name = {"oracleJdbcTemplate"})
    public JdbcTemplate getOracleJdbcTemplate() throws Exception {
        JdbcTemplate oracleJdbcTemplate = new JdbcTemplate(this.firstDataSource());
        log.debug("oracleJdbcTemplate初始化完成");
        return oracleJdbcTemplate;
    }

    @Bean(name = {"oracleNamedParameterJdbcTemplate"})
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() throws Exception {
        NamedParameterJdbcTemplate oracleJdbcTemplate = new NamedParameterJdbcTemplate(this.firstDataSource());
        log.debug("NamedParameterJdbcTemplate初始化完成");
        return oracleJdbcTemplate;
    }

//    @Bean(name = {"impalaJdbcTemplate"})
//    public JdbcTemplate getImpalaJdbcTemplate() throws Exception{
//        JdbcTemplate impalaJdbcTemplate = new JdbcTemplate(this.kuduDataSource());
//        log.debug("impalaJdbcTemplate初始化完成");
//        return impalaJdbcTemplate;
//        com.cloudera.impala.jdbc41.DataSource dataSource = new com.cloudera.impala.jdbc41.DataSource();
//        if(kuduEnabled) {
////        String connUrl = this.impalaUrl + ":" + this.impalaPort + "/" + this.impalaScheme + ";auth=noSasl";
////        String connUrl = "jdbc:impala://192.168.1.46:21050/kudu_test;auth=noSasl";
//            ImpalaUtils.initKerberos();
//            try {
//                UserGroupInformation logUser = UserGroupInformation.getLoginUser();
//                if (null == logUser) {
//                    throw new RuntimeException("登录用户为空!");
//                }
//                dataSource = logUser.doAs(new PrivilegedAction<com.cloudera.impala.jdbc41.DataSource>() {
//                    @Override
//                    public com.cloudera.impala.jdbc41.DataSource run() {
//                        com.cloudera.impala.jdbc41.DataSource dataSource = new com.cloudera.impala.jdbc41.DataSource();
//                        kuduConnUrl = MessageFormat.format(kuduConnUrl, PropertiesUtils.getInstance().getProperty("IMPALA_DAEMON"), PropertiesUtils.getInstance().getProperty("KERBEROS_REAM"));
//                        dataSource.setURL(kuduConnUrl);
//                        dataSource.setUserID(kuduUsername);
//                        dataSource.setPassword(kuduPassword);
//                        return dataSource;
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return new JdbcTemplate(dataSource);
//    }
}
