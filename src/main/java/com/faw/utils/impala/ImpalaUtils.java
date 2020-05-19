package com.faw.utils.impala;


import com.alibaba.druid.pool.DruidDataSource;

import com.faw.utils.io.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;


public class ImpalaUtils {

    private static Logger logger = Logger.getLogger(ImpalaUtils.class);
    public static String USING_DB = "";
    public static String using = "";
    public static String using_view = "";
    public static String USING_VIEW = "";
    private static String IMPALA_JDBC_DRIVER = "";
    private static String CONNECTION_URL = "jdbc:impala://{0}:21050/;AuthMech=1;KrbRealm={1};KrbHostFQDN={0};KrbServiceName=impala";

    @Autowired
    public DataSource oracleDataSource;

    static {
        initKerberos();
    }

    public static void initKerberos() {
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
            Configuration conf = new Configuration();
            conf.set("hadoop.security.authentication", PropertiesUtils.getInstance().getProperty("HADOOP_SECURITY"));
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(PropertiesUtils.getInstance().getProperty("KERBEROS_USERNAME"), KEYTAB_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        }

        logger.info("Kerberos初始化完成");
    }

    public static DataSource getDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        try{
            druidDataSource.setDriverClassName(PropertiesUtils.getInstance().getProperty("IMPALA_JDBC_DRIVER"));
            druidDataSource.setUrl(CONNECTION_URL);
            druidDataSource.setMaxActive(50);
            druidDataSource.setMinIdle(1);
            druidDataSource.setMaxWait(10000);
            druidDataSource.setInitialSize(3);
            druidDataSource.setValidationQuery("select 1");
            druidDataSource.setFilters("stat");
            druidDataSource.setName("impala");
            druidDataSource.setTimeBetweenConnectErrorMillis(600000);
            druidDataSource.setUseGlobalDataSourceStat(true);
        }catch (Exception e){
            e.printStackTrace();
            logger.debug(e.getMessage());
        }

        return druidDataSource;
    }

    public static DataSource getOracleDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        try{
            druidDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            druidDataSource.setUrl("jdbc:oracle:thin:@10.226.95.174:1521:SCPDCP");
            druidDataSource.setUsername("FAW_TEST");
            druidDataSource.setPassword("FAW_TEST");
        }catch (Exception e){
            e.printStackTrace();
            logger.debug(e.getMessage());
        }

        return druidDataSource;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        //conn = getOracleDataSource().getConnection();
        try {
            // 解决 24小时后，kerberos认证的ticket失效问题，每次获取用户前，检测下是否过期，过期的话重新登录
            UserGroupInformation.getLoginUser().checkTGTAndReloginFromKeytab();
            UserGroupInformation logUser = UserGroupInformation.getLoginUser();
            if (null == logUser) {
                throw new RuntimeException("登录用户为空!");
            }
            conn = logUser.doAs(new PrivilegedAction<Connection>() {
                @Override
                public Connection run() {
                    Connection connection = null;
                    ResultSet rs = null;
                    PreparedStatement ps = null;
                    try {
                        connection = DriverManager.getConnection(CONNECTION_URL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return connection;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        }
        return conn;
    }

    private static void close(Connection conn, Statement stmt, ResultSet rset) {
        try {
            if (null != rset) {
                rset.close();
            }
            if (null != stmt) {
                stmt.close();
            }
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        }
    }

    public static List<Map<String, String>> pageQuery(String sql) {
        List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            List<String> columns = new ArrayList<String>();
            int columnSize = metaData.getColumnCount();
            for (int i = 1; i <= columnSize; i++) {
                columns.add(metaData.getColumnName(i).toUpperCase());
            }
            while (rs.next()) {
                Map<String, String> rowMap = new LinkedHashMap<String, String>();
                for (int i = 0; i < columnSize; i++) {
                    String columnValue = rs.getString(i + 1);
                    if (StringUtils.isBlank(columnValue)) {
                        columnValue = "";
                    }
                    rowMap.put(columns.get(i), columnValue);
                }
                rows.add(rowMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        } finally {
            close(connection, null, null);
        }
        return rows;
    }

    //获取总记录数
    public static Long totalCount(String sql) {
        Long totalCount = 0L;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                totalCount = rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        } finally {
            close(connection, null, null);
        }
        return totalCount;
    }


    public static List<Map<String, Object>> pageQueryForMap(String sql) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            List<String> columns = new ArrayList<String>();
            int columnSize = metaData.getColumnCount();
            for (int i = 1; i <= columnSize; i++) {
                columns.add(metaData.getColumnName(i).toUpperCase());
            }
            while (rs.next()) {
                Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
                for (int i = 0; i < columnSize; i++) {
                    Object obj = rs.getObject(i + 1);
                    rowMap.put(columns.get(i), obj);
                }
                rows.add(rowMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        } finally {
            close(connection, null, null);
        }
        return rows;
    }


    public static List<List<Object>> pageQueryForList(String sql) {
        List<List<Object>> rows = new ArrayList<List<Object>>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnSize = metaData.getColumnCount();
            while (rs.next()) {
                List<Object> row = new ArrayList<Object>();
                for (int i = 0; i < columnSize; i++) {
                    Object obj = rs.getObject(i + 1);
                    row.add(obj);
                }
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.ERROR, e.getMessage());
        } finally {
            close(connection, null, null);
        }
        return rows;
    }


    //为更进一步异常信息,错误信息向上反馈
    //code:1表示success,code:2表示failure
    public static Map<String, String> execSql(String sql) {
        Connection connection = null;
        Statement stmt = null;
        Map<String, String> result = new HashMap<String, String>();
        result.put("code", "1");
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", "2");
            String msg = e.getMessage();
            if (StringUtils.isNotBlank(msg)) {
                result.put("msg", msg.length() > 1000 ? msg.substring(0, 1000) : msg);
            } else {
                result.put("msg", "");
            }
        } finally {
            close(connection, stmt, null);
        }
        return result;
    }

    public static Integer refreshTable(String tab_name) {
        String sql = " refresh " + tab_name + " ";
        Connection connection = null;
        Statement stmt = null;
        Integer rs = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeUpdate(sql);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, stmt, null);
        }
        return rs;
    }


    /**
     * 清空impala关联kudu的临时表数据
     *
     * @param tab_name
     */
    public static boolean delImpalaWholeData(String tab_name) {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate(" delete from " + ImpalaUtils.USING_DB + tab_name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, null, null);
        }
        return false;
    }


    public static void moveTempData2Master(String tab_name, String tab_name_temp) {
        String sql = " upsert into table " + ImpalaUtils.USING_DB + tab_name + " select * from " + ImpalaUtils.USING_DB + tab_name_temp + " ";
        System.out.println(" moveing data: " + sql);
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, stmt, null);
        }
    }

    public static Integer execDeleteSql(String sql) {
        Connection connection = null;
        Statement stmt = null;
        Integer rs = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeUpdate(sql);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 只获取某一列数值
     *
     * @param sql
     * @return
     */
    public static List<String> getSingleColumn(String sql) {
        List<String> columns = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String columnVal = rs.getString(1);
                if (StringUtils.isBlank(columnVal)) {
                    columnVal = "";
                }
                columns.add(columnVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, null, null);
        }
        return columns;
    }

    public static List<String> getSingleRow(String sql) {
        List<String> row = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnSize = metaData.getColumnCount();
            Boolean flag = true;
            while (rs.next() && flag) {
                for (int i = 0; i < columnSize; i++) {
                    String columnVal = rs.getString(i + 1);
                    if (StringUtils.isBlank(columnVal)) {
                        columnVal = "";
                    }
                    row.add(columnVal);
                }
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, null, null);
        }
        return row;
    }

    /**
     * 只获取某一对数值
     *
     * @param sql
     * @return
     */
    public static Map<String, String> getKeyValPairColumn(String sql) {
        Map<String, String> columnsMap = new LinkedHashMap<String, String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String key = rs.getString(1);
                if (StringUtils.isBlank(key)) {
                    key = "";
                }
                String value = rs.getString(1);
                if (StringUtils.isBlank(value)) {
                    value = "";
                }
                columnsMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(connection, null, null);
        }
        return columnsMap;
    }

    public static void main(String[] args) {
        try {
            String sqlBase = "select productbase,avg_month_cp8,year_,month_ from qadw.qadw_fisview_init_dlq_month_productbase_v";
            String sqlCartype = "select car_type,avg_month_cp8,year_,month_,factory from qadw.qadw_fisview_init_dlq_month_v";
            List<Map<String, Object>> page2 = pageQueryForMap(sqlCartype);
            for(Map<String, Object> temp : page2){
                System.out.println(UUID.randomUUID().toString()+"&"+temp.get("FACTORY")+"&"+temp.get("CAR_TYPE")+"&"+temp.get("AVG_MONTH_CP8")+"&"+temp.get("YEAR_")+"&"+temp.get("MONTH_"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
