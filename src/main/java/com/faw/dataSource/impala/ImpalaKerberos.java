package com.faw.dataSource.impala;

import com.faw.utils.io.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author liushengbin
 * @version 1.0
 * @description 连接impala的Kerberos认证
 * @email liushengbin7@gmail.com
 * @date 2019/4/26 12:00 PM
 */
@Configuration
@Slf4j
public class ImpalaKerberos {

    @Value("${spring.datasource.druid.kudu.isKerberos}")
    private Boolean isKerberos;

    public static String USING_DB = "";
    public static String using = "";
    public static String using_view = "";
    public static String USING_VIEW = "";
    private static String IMPALA_JDBC_DRIVER = "";
    private static String CONNECTION_URL = "jdbc:impala://{0}:21050/;AuthMech=1;KrbRealm={1};KrbHostFQDN={0};KrbServiceName=impala";


    @Bean("initKerberos")
    public ImpalaKerberos initKerberos() {
        if(isKerberos) {
            USING_DB = PropertiesUtils.getInstance().getProperty("USING_DB");
            using = PropertiesUtils.getInstance().getProperty("USING_DB");
            USING_VIEW = PropertiesUtils.getInstance().getProperty("USING_VIEW");
            using_view = PropertiesUtils.getInstance().getProperty("USING_VIEW");
            IMPALA_JDBC_DRIVER = PropertiesUtils.getInstance().getProperty("IMPALA_JDBC_DRIVER");
            CONNECTION_URL = MessageFormat.format(CONNECTION_URL, PropertiesUtils.getInstance().getProperty("IMPALA_DAEMON"), PropertiesUtils.getInstance().getProperty("KERBEROS_REAM"));
            try {
                String KEYTAB_PATH = PropertiesUtils.getInstance().getProperty("KEYTAB_PATH");
                String KRB5_PATH = PropertiesUtils.getInstance().getProperty("KRB5_PATH");

//            ClassPathResource keytabPath = new ClassPathResource("config/dcpuser.keytab");
//            ClassPathResource krbonfPath = new ClassPathResource("config/krb5.conf");
//            System.out.println("keytabPath:" + keytabPath.getFile().getAbsolutePath());
//            System.out.println("krbonfPath:" + krbonfPath.getFile().getAbsolutePath());
////            URL keytabPath = Thread.currentThread().getContextClassLoader().getResource("/config/dcpuser.keytab");
////            URL krbonfPath = Thread.currentThread().getContextClassLoader().getResource("/config/krb5.conf");
//            KEYTAB_PATH = keytabPath.getFile().getPath();
//            KRB5_PATH = krbonfPath.getFile().getPath();

                System.setProperty("java.security.krb5.conf", KRB5_PATH);
                org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
                conf.set("hadoop.security.authentication", PropertiesUtils.getInstance().getProperty("HADOOP_SECURITY"));
                UserGroupInformation.setConfiguration(conf);
                UserGroupInformation.loginUserFromKeytab(PropertiesUtils.getInstance().getProperty("KERBEROS_USERNAME"), KEYTAB_PATH);
            } catch (IOException e) {
                e.printStackTrace();
                log.debug(e.getMessage());
            }

            log.info("Kerberos初始化完成");
        }
        return new ImpalaKerberos();
    }

}
