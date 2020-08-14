/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: prodJob
 * Author:   JG
 * Date:     2020/5/20 11:35
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.jobView.jobs;

import com.faw.config.OnlineDataDirConfig;
import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import com.faw.utils.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author JG
 * @create 2020/5/20
 * @since 1.0.0
 */
@Component("prodJob")
public class ProdJob {

    @Autowired
    private OnlineDataDirConfig onlineDataDirConfig;
    //数据抽取日志输出文件夹
    @Value("${abf.log.logFile}")
    private String logFilePath;  //本地文件夹的日志输出地址

    @Value("${abf.log.localSupplementLog}")
    private String localSupplementLog; //本地补充日志

    //数据抽取日志输出文件夹
    @Value("${abf.log.firstReadLog}")
    private String firstReadLogPath;  //共享文件夹的日志输出地址

    @Value("${abf.log.beginTime}")
    private String beginTime; //过滤调共享文件夹改日期前的而数据


    //pdf数据抽取的 业务
    @Value("${abf.log.pdfLogFile}")
    private String pdfLocalFilePath; //pdf抽取的 本地日志地址

    @Value("${abf.log.firstPdfLog}")
    private String firstPdfLogPath; //pdf抽取的 共享日志地址


    @Autowired
    private IOnlineAnalysis onlineAnalysis;

    /*
        2020-07-23 09:56:28==txt==LocalDir==T-Cross_AB1_79202018401264_Cycle Time.txt
     */
    private Map<String, List<String>> getLogByPosition(String position) { //
        File outFile = new File(localSupplementLog); //补数据的日志数据
        File onlineLocalLog = new File(logFilePath); //在线数据 监听文件的日志
        File onlineFirstLog = new File(firstReadLogPath); //首次启动的在线数据
        File pdfLocalLog = new File(pdfLocalFilePath); //pdf 监听文件的日志
        File pdfFirstLog = new File(firstPdfLogPath); //首次启动的pdfLog

        ArrayList<String> lineValues = new ArrayList<>(); //会出现最大容量 所以要定期清理日志 修改 工程的启动日期
        // 合并 本地 首次的记录 和 后补的记录
        Map<String, List<String>> localLogMap = new HashMap<>();
        try {
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            if (!onlineLocalLog.exists()) {
                onlineLocalLog.createNewFile();
            }
            if (!pdfLocalLog.exists()) {
                pdfLocalLog.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<HashMap<String, Object>> localResults = FileUtils.readDir(onlineLocalLog); //本地日志 本地文件夹监听输出日志
        List<HashMap<String, Object>> localSupplement = FileUtils.readDir(outFile); //本地日志 首次添加失败后补充的文件日志
        List<HashMap<String, Object>> localPdfResults = FileUtils.readDir(pdfLocalLog);//本地日志pdf 本地日志

        List<HashMap<String, Object>> firstOnlineLog = FileUtils.readDir(onlineFirstLog);//首次启动的日志 在线数据日志

        List<HashMap<String, Object>> firstPdfLog = FileUtils.readDir(pdfFirstLog);//首次启动的日志 pdf日志

        for (Map<String, Object> map : firstOnlineLog) {
            lineValues.addAll((ArrayList<String>) map.get("lineValues"));
        }

        for (Map<String, Object> map : firstPdfLog) {
            lineValues.addAll((ArrayList<String>) map.get("lineValues"));
        }

        for (Map<String, Object> map : localPdfResults) {
            lineValues.addAll((ArrayList<String>) map.get("lineValues"));
        }

        for (Map<String, Object> map : localSupplement) {
            lineValues.addAll((ArrayList<String>) map.get("lineValues"));
        }

        for (Map<String, Object> map : localResults) {
            lineValues.addAll((ArrayList<String>) map.get("lineValues"));
        }

        for (String line : lineValues) {
            String[] lineInfo = line.split("==");
            if (! position.equals(lineInfo[2])) { //对本地文件夹监控生成的日志处理
                continue;
            }
            if (localLogMap.containsKey(lineInfo[1])) {
                List<String> list = localLogMap.get(lineInfo[1]);
                list.add(lineInfo[3]);
                localLogMap.put(lineInfo[1], list);

            } else {
                List<String> list = new ArrayList<>();
                list.add(lineInfo[3]);
                localLogMap.put(lineInfo[1], list);
            }


        }
        return localLogMap;
    }
    //本地手动放到 本地文件夹补录的数据 补偿方法
    public void localFileReMakeDate() {
        Map<String, String> targetMap = onlineDataDirConfig.getTargetMap();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String,List<String>> localLogMap = getLogByPosition("LocalDir");


        for( String key :targetMap.keySet()) { //目标文件夹的 配置信息 获取 业务类型key 和 地址
        File[] files = null;
        try {
            files = new File(targetMap.get(key)).listFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> logFileNames = null;
        if (localLogMap.containsKey(key)) {
            logFileNames = localLogMap.get(key);
        }


        if (files.length > 0) {
            for (File f : files) {  //循环一个file  看在日志里存不存在
                Boolean contain = false;
                if (logFileNames != null) {  //如果日志里没有说明 本地解析 没成功
                    for (String name : logFileNames) {
                        if (name.equals(f.getName().toString())) {
                            contain = true;
                            break;
                        }
                    }
                }

                if (contain) {
                    continue;
                }
                String outLog = simpleDateFormat.format(new Date()) + "==" + key + "==" + "LocalDir==" + f.getName() + "\r\n";
                //补录
                if ("txt".equals(key)) {
                    onlineAnalysis.txtAnalysisRule(f); //补录txt
                } else if ("superAlmost".equals(key)) { // 超差点
                    onlineAnalysis.superAlmostExtractor(f);
                } else if ("stablePassRate".equals(key)) { //稳定性
                    onlineAnalysis.stablePassRateExtractor(f);
                } else {//demo数据
                    onlineAnalysis.demoAnalysisRule(f);//补录demo
                }

                try {
                    File outFile = new File(localSupplementLog);
                    FileOutputStream fileOutputStream = new FileOutputStream(outFile, true);
                    fileOutputStream.write(outLog.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

    //共享补数据 是 将共享文件夹的 数据 输出到 本地对应的文件夹内


    public void shareFileReMakeDate() {
        //共享文件夹的 文件 与  本地文件夹的日志 ShareDir  +  和首次启动的日志  + 补录文件日志    ShareDir的 比较 把gap文件补录
        //Map<String, String> targetMap = onlineDataDirConfig.getTargetMap();

        Map<String, List<String>> shareMap = onlineDataDirConfig.getShareMaplist();
        final  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String,List<String>> localLogMap = getLogByPosition("ShareDir");

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                Date date = null;
                Long time = null;

                try {
                    date = dateFormat.parse(beginTime);
                    time = file1.lastModified();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(time > date.getTime()){ //初次启动解析 6月开始的数据
                    return true;
                }
                return false;
            }
        };
        //循环配置文件
        for(String key : shareMap.keySet()){
            List<File> files = new ArrayList<>();

            try {
                List<String> shareDirPath = shareMap.get(key);
                for(String path : shareDirPath){
                    files.addAll(Arrays.asList(new File(path).listFiles(fileFilter)));  //过滤调配置时间前的数据
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<String> logFileNames = null;
            if (localLogMap.containsKey(key)) {
                logFileNames = localLogMap.get(key);
            }

            //循环配置文件  过滤到的file  对象
            if (files.size() > 0) {
                for (File f : files) {  //循环一个file  看在日志里存不存在
                    Boolean contain = false;
                    if (logFileNames != null) {  //如果日志里没有说明 本地解析 没成功
                        for (String name : logFileNames) {
                            if (name.equals(f.getName().toString())) {
                                contain = true;
                                break;
                            }
                        }
                    }

                    if (contain) {
                        continue;
                    }
                    String outLog = dateFormat.format(new Date()) + "==" + key + "==" + "ShareDir==" + f.getName() + "\r\n";
                    //补录
                   /* if ("txt".equals(key)) {
                        onlineAnalysis.txtAnalysisRule(f); //补录txt
                    } else if ("superAlmost".equals(key)) { // 超差点
                        onlineAnalysis.superAlmostExtractor(f);
                    } else if ("stablePassRate".equals(key)) { //稳定性
                        onlineAnalysis.stablePassRateExtractor(f);
                    } else {//demo数据
                        onlineAnalysis.demoAnalysisRule(f);//补录demo
                    }*/

                    try {
                        File outFile = new File(localSupplementLog);
                        FileOutputStream fileOutputStream = new FileOutputStream(outFile, true);
                        fileOutputStream.write(outLog.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
}
