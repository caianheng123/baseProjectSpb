package com.faw.modules.piWebJob.watcher;

import com.faw.config.OnlineDataDirConfig;
import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import com.faw.utils.io.FileUtils;
import com.faw.utils.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;

@Component
@Order(value = 1)
public class StartGlobalKeyboard implements CommandLineRunner {

    @Autowired
    private OnlineDataDirConfig onlineDataDirConfig;

    //首次启动标识
    @Value("${abf.log.firstStart}")
    private  String firstStart;

    @Autowired
    private IOnlineAnalysis onlineAnalysis;

    @Override
    public void run(String... args) throws Exception {

        //是否首次启动
        File fistStart = new File(firstStart);
        if(fistStart.exists()){
            List<String> lines = FileUtils.resolveFile(fistStart);
            for(String s : lines){
                if("first=1".equals(s)){
                    //已经首次启动过了
                    return;
                }
            }
        }else{
            if(fistStart.createNewFile()){
                FileOutputStream fileOutputStream= new FileOutputStream(fistStart);
                IOUtils.write("first=1".getBytes(),fileOutputStream);
                fileOutputStream.close();
            }
        }

        Map<String,String> targetDirPathMap = onlineDataDirConfig.getTargetMap();
        Map<String,List<String>> shareDirPathMap = onlineDataDirConfig.getShareMaplist();

        //循环 分享目录通过key获取目标文件地址 key 通过 targetDirPathMap 获取地址     通过value获取共享文件地址

        for (String key : shareDirPathMap.keySet()) {
            List<String> sharePaths = shareDirPathMap.get(key);

            final String targetPath = targetDirPathMap.get(key);
            //创建共享文件copy线程
            for(String sharePath : sharePaths){
                final File shareFile = new File(sharePath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            write(targetPath,shareFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            final  File targetFile = new File(targetPath);
            final  String key2 = key;
            //创建文件解析线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if("txt".equals(key2)){
                            read(targetFile,"txt");
                        }else{
                            read(targetFile,"demo");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }



    }

    //共享文件夹全读
    public void  write (String toPath,File file) throws Exception {
        //目标源文件夹  ip 是各个线程存放在不同的文件夹的名称
        File f = new File(file.getAbsolutePath());
        File toFile = new File(toPath);
        FileUtils.copy(f,toFile);
    }
    public void  read (File file,String bustype) throws Exception {
        File f = new File(file.getAbsolutePath());
        if("txt".equals(bustype)){
            onlineAnalysis.txtAnalysisRule(f);
        }else {
            onlineAnalysis.demoAnalysisRule(f);
        }
    }

}