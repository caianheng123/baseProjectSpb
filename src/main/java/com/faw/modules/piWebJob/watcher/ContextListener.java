package com.faw.modules.piWebJob.watcher;

import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//
@WebListener
@Order(value = 10)
public class ContextListener implements ServletContextListener {
    //txt数据文件夹
    @Value("${abf.onlineData.dirPathTxt}")
    private  String txtDir;

    //demo数据文件夹
    @Value("${abf.onlineData.dirPathDemo}")
    private  String demoDir;

    @Autowired
    private IOnlineAnalysis onlineAnalysis;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("===========================MyServletContextListener初始化");

        final File txtFile  = new File(txtDir);
        final File demoFile = new File(demoDir);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(txtFile, true, new FileActionCallback() {
                        @Override
                        public void create(File file) {
                            System.out.println("文件已创建\t" + file.getAbsolutePath());

                            onlineAnalysis.txtAnalysisRule(file);//数据抽取到 oracle

                        }
                        @Override
                        public void delete(File file) {
                            System.out.println("文件已删除\t" + file.getAbsolutePath());
                            //删除操作的 业务
                        }
                        @Override
                        public void modify(File file) {
                         System.out.println("文件已修改\t" + file.getAbsolutePath());
                            //修改操作的 业务
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(demoFile, true, new FileActionCallback() {
                        @Override
                        public void create(File file2) {
                            System.out.println("文件已创建\t" + file2.getAbsolutePath());
                            //删除操作的 业务

                            onlineAnalysis.demoAnalysisRule(file2);//数据抽取到 oracle
                        }
                        @Override
                        public void delete(File file2) {
                            System.out.println("文件已删除\t" + file2.getAbsolutePath());
                            //删除操作的 业务
                        }
                        @Override
                        public void modify(File file2) {
                            System.out.println("文件已修改\t" + file2.getAbsolutePath());
                            //修改操作的 业务
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("正在监视文件夹:" +  txtFile.getAbsolutePath());
        System.out.println("正在监视文件夹2:" + demoFile.getAbsolutePath());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("===========================MyServletContextListener销毁");
    }

    public void  write (String targetDir,File file) throws Exception {
        //目标源文件夹  ip 是各个线程存放在不同的文件夹的名称
        System.out.println(file.getAbsolutePath());
        File f = new File(file.getAbsolutePath());
        String path = "D:\\" + targetDir;
        File f1 = new File(path);
        copy(f,f1); //将 f -> f1
        System.out.println("复制成功");
    }

    public void copy(File f,File f1) throws IOException{ //复制文件的方法！
        if(!f1.exists()){
            f1.mkdir();
        }
        if(!f1.exists()){//路径e79fa5e98193e59b9ee7ad9431333332616430判断，是路径还是单个的文件
            File[] cf = f.listFiles();
            for(File fn : cf){
                if(fn.isFile()){
                    FileInputStream fis = new FileInputStream(fn);
                    FileOutputStream fos = new FileOutputStream(f1 + "\\" +fn.getName());
                    byte[] b = new byte[1024];
                    int i = fis.read(b);
                    while(i != -1){
                        fos.write(b, 0, i);
                        i = fis.read(b);
                    }
                    fis.close();
                    fos.close();
                }else{
                    File fb = new File(f1 + "\\" + fn.getName());
                    fb.mkdir();
                    if(fn.listFiles() != null){//如果有子目录递归复制子目录！
                        copy(fn,fb);
                    }
                }
            }
        }else{
            FileInputStream fis = new FileInputStream(f);
            FileOutputStream fos = new FileOutputStream(f1 + "\\" +f.getName());
            byte[] b = new byte[1024];
            int i = fis.read(b);
            while(i != -1){
                fos.write(b, 0, i);
                i = fis.read(b);
            }
            fis.close();
            fos.close();
        }
    }
}