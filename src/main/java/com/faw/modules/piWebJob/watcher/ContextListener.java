package com.faw.modules.piWebJob.watcher;

import com.faw.config.OnlineDataDirConfig;
import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import com.faw.utils.io.FileUtils;
import org.apache.tools.ant.util.ReaderInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.crypto.Data;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
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

    //pdf数据抽取的 业务
    @Value("${abf.log.pdfLogFile}")
    private String pdfLocalFilePath;

    @Autowired
    private IOnlineAnalysis onlineAnalysis;

    //设置个线程池
    private ThreadPoolTaskExecutor threadPoolTaskExecutor = buildThreadPoolTaskExecutor();// 线程池

    private BlockingQueue<Runnable> bq = new LinkedBlockingQueue<>();// 任务放入队列

    //设置个阻塞队列  onlineData 的输入队列
    public static BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(1024 * 1024);

    //设置个阻塞队列  pdfData 的输入队列
    public static BlockingQueue<byte[]> pdfqueue = new ArrayBlockingQueue<>(1024 * 1024);

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("===========================MyServletContextListener初始化");

        Map<String,String> targetDirPathMap = onlineDataDirConfig.getTargetMap();
        Map<String,List<String>> shareDirPathMap = onlineDataDirConfig.getShareMaplist();

        //循环 分享目录通过key获取目标文件地址 key 通过 targetDirPathMap 获取地址     通过value获取共享文件地址
        //online 的日志输出文件
        final File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();//创建新的文件夹
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //pdf 的日志输出文件
        final File pdfLocalFile = new File(pdfLocalFilePath);
        if (!pdfLocalFile.exists()) {
            try {
                pdfLocalFile.createNewFile();//创建新的文件夹
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //创建个 线程处理阻塞队列里的 文件
        new WriteLogThread(queue,pdfqueue,logFile,pdfLocalFile).start();//日志线程

        for (String key : shareDirPathMap.keySet()) {
            List<String> sharePaths = shareDirPathMap.get(key);
            final String targetPath = targetDirPathMap.get(key);
            //监听共享文件夹文件变化线程
            final String key2 = key; //给为 那个路径地址的key
            final File targetFile = new File(targetPath);
            for(String sharePath : sharePaths) { //遍历当前可以
                final File shareFile = new File(sharePath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new WatchDir(shareFile, true, new FileActionCallback() {
                                @Override
                                public void modify(File file) {
                                }
                                @Override
                                public void delete(File file2) {
                                    //删除操作的 业务
                                }
                                @Override
                                public void create(File file) {
                                    //修改操作的 业务
                                    //创建业务线程
                                    OnlineDataBusThread task = new OnlineDataBusThread(file,queue,pdfqueue,key2,"ShareDir",onlineAnalysis);
                                  /*
                                    Future<?> future = threadPoolTaskExecutor.submit(task);//开启该线程
                                    try {
                                        future.get();// 结果
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }*/
                                    threadPoolTaskExecutor.execute(task);//开启该线程
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
                            public void modify(File file) {}
                            @Override
                            public void delete(File file) {
                                //删除操作的 业务
                            }
                            @Override
                            public void create(File file) {
                                OnlineDataBusThread task1 = new OnlineDataBusThread(file,queue,pdfqueue,key2,"LocalDir",onlineAnalysis);
                                /*
                                Future<?> future = threadPoolTaskExecutor.submit(task1);//开启该线程
                                try {
                                    future.get();// 结果
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }*/
                                threadPoolTaskExecutor.execute(task1);//开启该线程

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

    private static ThreadPoolTaskExecutor buildThreadPoolTaskExecutor() {
        //一个线程池中的线程异常了，那么线程池会怎么处理这个线程? 问题验证
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //ThreadPoolExecutor tpe = new ThreadPoolExecutor(3, 6, 50, TimeUnit.MILLISECONDS, bq);
        //线程池数量
        threadPoolTaskExecutor.setCorePoolSize(10);
        //任务执行最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(10);
        //线程池缓存任务队列大小
        threadPoolTaskExecutor.setQueueCapacity(1000);
        //线程允许空闲时长 (单位秒 SECONDS)
        threadPoolTaskExecutor.setKeepAliveSeconds(20);
        //线程超过空闲时间限制，均会退出直到线程数量为0
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        //对Runnable任务装饰一下， 在任务执行时完成异常日志打印、ThreadLocal清理等功能
        threadPoolTaskExecutor.setTaskDecorator(null);
        //线程异常处理策略

        /**
         * 四种线程的拒绝策略
         *
         *　new ThreadPoolExecutor.AbortPolicy();//默认，队列满了丢任务抛出异常
         *　new ThreadPoolExecutor.DiscardPolicy();//队列满了丢任务不异常
         *　new ThreadPoolExecutor.DiscardOldestPolicy();//将最早进入队列的任务删，之后再尝试加入队列
         *　new ThreadPoolExecutor.CallerRunsPolicy();//如果添加到线程池失败，那么主线程会自己去执行该任务
         */
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        //初始化线程池
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
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
        String outLog = simpleDateFormat.format(date)+"=="+business+"ShareFile=="+file.getName()+"\r\n";
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
    private BlockingQueue<byte[]> pdfQueue;
    private File file;
    private String business;
    private String dirType;
    private IOnlineAnalysis onlineAnalysis;

    public OnlineDataBusThread(File file, BlockingQueue<byte[]> buffer,BlockingQueue<byte[]>  pdfQueue, String business,String dirType, IOnlineAnalysis onlineAnalysis) {
        this.file = file;
        this.buffer = buffer;
        this.pdfQueue = pdfQueue;
        this.business = business;
        this.dirType = dirType;
        this.onlineAnalysis = onlineAnalysis;
    }

    public OnlineDataBusThread(File file) {
        this.file = file;
    }

    public void run() {
        //创建业务操作的 业务
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String outLog = simpleDateFormat.format(date)+"=="+business+"=="+dirType+"=="+file.getName()+"\r\n";
        try {
        //业务   在业务 有异常时不输出日志
            if (business != null && "txt".equals(business)) {
                onlineAnalysis.txtAnalysisRule(file);//数据抽取到 oracle
                buffer.put(outLog.getBytes());
            }else if(business != null && "superAlmost".equals(business)){ //超差点  pdf
                onlineAnalysis.superAlmostExtractor(file);//数据抽取到 oracle
                pdfQueue.put(outLog.getBytes());
            }else if(business != null && "stablePassRate".equals(business)){ //稳定性合格率  pdf
                onlineAnalysis.stablePassRateExtractor(file);//数据抽取到 oracle
                pdfQueue.put(outLog.getBytes());
            }
            else{
                onlineAnalysis.demoAnalysisRule(file);//数据抽取到 oracle
                buffer.put(outLog.getBytes());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class WriteLogThread extends Thread {
    private BlockingQueue<byte[]> buffer;
    private BlockingQueue<byte[]> pdfBuffer;
    private File onlineDataOFile;
    private File pdfDataOFile;
    private FileOutputStream onlineDataOutputStream;
    private FileOutputStream pdfDataOutputStream;

    public WriteLogThread(BlockingQueue<byte[]> buffer,BlockingQueue<byte[]> pdfBuffer ,File onlineDataOFile,File pdfDataOFile) {
        this.buffer = buffer;
        this.pdfBuffer = pdfBuffer;
        this.onlineDataOFile = onlineDataOFile;
        this.pdfDataOFile = pdfDataOFile;
        try {
            this.onlineDataOutputStream = new FileOutputStream(onlineDataOFile, true);
            this.pdfDataOutputStream = new FileOutputStream(pdfDataOFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                if (!buffer.isEmpty()) {
                    onlineDataOutputStream.write(buffer.take());
                }
                if (!pdfBuffer.isEmpty()) {
                    pdfDataOutputStream.write(pdfBuffer.take());
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