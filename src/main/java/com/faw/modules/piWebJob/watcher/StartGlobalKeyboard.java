package com.faw.modules.piWebJob.watcher;

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

@Component
@Order(value = 1)
public class StartGlobalKeyboard implements CommandLineRunner {
    //txt数据文件夹
    @Value("${abf.onlineData.dirPathTxt}")
    private  String txtDir;

    //demo数据文件夹
    @Value("${abf.onlineData.dirPathDemo.mode1Dir}")
    private  String demo1Dir;

    //demo数据文件夹
    @Value("${abf.onlineData.dirPathDemo.mode2Dir}")
    private  String demo2Dir;

    //首次启动标识
    @Value("${abf.onlineData.firstStart}")
    private  String firstStart;

    //共享文件夹
    @Value("${abf.onlineData.sharedTxtFile}")
    private String shareTxtDir;

    @Value("${abf.onlineData.sharedDemoFile.model1Dir}")
    private String shareDemo1Dir;

    @Value("${abf.onlineData.sharedDemoFile.model2Dir}")
    private String shareDemo2Dir;

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


        //你的要执行的代码
        final File txtFile = new File(txtDir);

        final File demo1File = new File(demo1Dir);

        final File demo2File = new File(demo2Dir);

        final File shareTxtFile = new File(shareTxtDir);

        final File shareDemo1File = new File(shareDemo1Dir);

        final File shareDemo2File = new File(shareDemo2Dir);

        //共享文件数据备份到D
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    write(txtDir,shareTxtFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    write(demo1Dir,shareDemo1File);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //暂时废弃文件夹2
     /*   new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    write(demo2Dir,shareDemo2File);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

        //文件解析业务
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    read(txtFile,"txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    read(demo1File,"demoCar1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //暂时关闭
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    read(demo2File,"demoCar2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/


    }

    //共享文件夹全读
    public void  write (String toPath,File file) throws Exception {
        //目标源文件夹  ip 是各个线程存放在不同的文件夹的名称
        System.out.println(file.getAbsolutePath());
        File f = new File(file.getAbsolutePath());
        File toFile = new File(toPath);
        FileUtils.copy(f,toFile);
    }
    public void  read (File file,String bustype) throws Exception {
        File f = new File(file.getAbsolutePath());
        if("txt".equals(bustype)){
            onlineAnalysis.txtAnalysisRule(f);
        }else if("demoCar1".equals(bustype)){
            onlineAnalysis.demoAnalysisRule(f,"b8I");
        }else if("demoCar2".equals(bustype)){
            onlineAnalysis.demoAnalysisRule(f,"car2");
        }
    }

}