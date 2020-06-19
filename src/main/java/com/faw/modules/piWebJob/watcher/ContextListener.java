package com.faw.modules.piWebJob.watcher;

import com.faw.config.OnlineDataDirConfig;
import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import com.faw.utils.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.crypto.Data;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

//
@WebListener
@Order(value = 10)
public class ContextListener implements ServletContextListener {

    @Autowired
    private OnlineDataDirConfig onlineDataDirConfig;
    //数据抽取日志输出文件夹
    @Value("${abf.log.logFile}")
    private String logFilePath;

    @Autowired
    private IOnlineAnalysis onlineAnalysis;

    //设置个线程池
    private ExecutorService service = Executors.newSingleThreadExecutor(); // 线程池

    //设置个阻塞队列
    public static BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(1024 * 1024);

    //创建个 线程处理阻塞队列里的 文件

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("===========================MyServletContextListener初始化");

        Map<String,String> targetDirPathMap = onlineDataDirConfig.getTargetMap();
        Map<String,List<String>> shareDirPathMap = onlineDataDirConfig.getShareMaplist();

        //循环 分享目录通过key获取目标文件地址 key 通过 targetDirPathMap 获取地址     通过value获取共享文件地址

        final File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();//创建新的文件夹
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new WriteLogThread(queue, logFile).start();//日志线程

        for (String key : shareDirPathMap.keySet()) {
            List<String> sharePaths = shareDirPathMap.get(key);
            final String targetPath = targetDirPathMap.get(key);
            //监听共享文件夹文件变化线程
            final String key2 = key; //
            final File targetFile = new File(targetPath);
            for(String sharePath : sharePaths) { //遍历当前可以
                final File shareFile = new File(sharePath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new WatchDir(shareFile, true, new FileActionCallback() {
                                @Override
                                public void create(File file) {
                                }
                                @Override
                                public void delete(File file2) {
                                    System.out.println("文件已删除\t" + file2.getAbsolutePath());
                                    //删除操作的 业务
                                }
                                @Override
                                public void modify(File file) {
                                    //修改操作的 业务
                                    //创建业务线程
                                    ShareFileCopeThread task;
                                    if("txt".equals(key2)){
                                        task =  new ShareFileCopeThread(file,targetPath,queue,key2);//监听线程1
                                    }else{
                                        task =  new ShareFileCopeThread(file,targetPath,queue,key2);//监听线程1
                                    }

                                    Future<?> future = service.submit(task);//开启该线程
                                    try {
                                        future.get();// 结果
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                System.out.println("正在监视的共享文件夹:" + shareFile.getAbsolutePath());

            }
            //创建文件 本地文件夹 变化的监听线程 解析线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new WatchDir(targetFile, true, new FileActionCallback() {
                            @Override
                            public void create(File file1) {

                            }
                            @Override
                            public void delete(File file)
                            {
                                //删除操作的 业务
                            }

                            @Override
                            public void modify(File file) {
                                OnlineDataBusThread task1;
                                if("txt".equals(key2)){
                                    task1 =   new OnlineDataBusThread(file,queue,key2,onlineAnalysis);//监听线程1
                                }else{
                                    task1 =   new OnlineDataBusThread(file,queue,key2,onlineAnalysis);//监听线程1
                                }
                                Future<?> future = service.submit(task1);//开启该线程
                                try {
                                    future.get();// 结果
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            System.out.println("正在监视文件夹:" + targetFile.getAbsolutePath());
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("===========================MyServletContextListener销毁");
    }


}

class ShareFileCopeThread implements Runnable {
    private File file;//被监听测到的文件
    private String toPath;//输出到那个文件夹
    private String business; //检测的什么共享数据
    private BlockingQueue<byte[]> buffer;

    public ShareFileCopeThread(File file,String toPath,BlockingQueue<byte[]> buffer,String business ){
        this.file = file;
        this.toPath = toPath;
        this.buffer = buffer;
        this.business = business;
    }
    public void run() {
        //创建业务操作的 业务
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String outLog = simpleDateFormat.format(date)+"=="+business+"ShareFile="+file.getName()+"\r\n";
        try {
            buffer.put(outLog.getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
         FileUtils.copy(file,new File(toPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class OnlineDataBusThread implements Runnable {
    private BlockingQueue<byte[]> buffer;
    private File file;
    private String business;
    private IOnlineAnalysis onlineAnalysis;

    public OnlineDataBusThread(File file, BlockingQueue<byte[]> buffer, String business, IOnlineAnalysis onlineAnalysis) {
        this.file = file;
        this.buffer = buffer;
        this.business = business;
        this.onlineAnalysis = onlineAnalysis;
    }

    public OnlineDataBusThread(File file) {
        this.file = file;
    }

    public void run() {
        //创建业务操作的 业务
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String outLog = simpleDateFormat.format(date)+"=="+business+"ShareFile="+file.getName()+"\r\n";
        try {
            buffer.put(outLog.getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //业务
        if (business != null && "txt".equals(business)) {
            onlineAnalysis.txtAnalysisRule(file);//数据抽取到 oracle
        }else{
            onlineAnalysis.demoAnalysisRule(file);//数据抽取到 oracle
        }

    }
}

class WriteLogThread extends Thread {
    private BlockingQueue<byte[]> buffer;
    private File targetFile;
    private FileOutputStream fileOutputStream;

    public WriteLogThread(BlockingQueue<byte[]> buffer, File targetFile) {
        this.buffer = buffer;
        this.targetFile = targetFile;
        try {
            this.fileOutputStream = new FileOutputStream(targetFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                if (!buffer.isEmpty()) {
                    fileOutputStream.write(buffer.take());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          /*  if(buffer.isEmpty()) {
                //当缓冲区没有元素，并且 count为0，则说明读写完成
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        }

    }
}