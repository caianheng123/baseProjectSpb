package com.faw.modules.piWebJob.watcher;

import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import com.faw.utils.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.*;

//
@WebListener
@Order(value = 10)
public class ContextListener implements ServletContextListener {
    //txt数据文件夹
    @Value("${abf.onlineData.dirPathTxt}")
    private String txtDir;
    //demo数据文件夹  车型1
    @Value("${abf.onlineData.dirPathDemo.mode1Dir}")
    private String demo1Dir;

    //demo数据文件夹  车型2
    @Value("${abf.onlineData.dirPathDemo.mode2Dir}")
    private String demo2Dir;

    //数据抽取日志输出文件夹
    @Value("${abf.onlineData.logFile}")
    private String logFilePath;

    //共享文件夹
    @Value("${abf.onlineData.sharedTxtFile}")
    private String shareTxtDir;

    @Value("${abf.onlineData.sharedDemoFile.model1Dir}")
    private String shareDemo1Dir;

    @Value("${abf.onlineData.sharedDemoFile.model2Dir}")
    private String shareDemo2Dir;



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

        final File txtFile = new File(txtDir);
        final File demo1File = new File(demo1Dir);
        final File demo2File = new File(demo2Dir);
        final File shareTxtFile = new File(shareTxtDir);//共享文件夹 txt
        final File shareDemo1File = new File(shareDemo1Dir); //共享文件夹 demo 车型1
        final File shareDemo2File = new File(shareDemo2Dir); //共享文件夹 demo 车型2
        final File logFile = new File(logFilePath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();//创建新的文件夹
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new WriteLogThread(queue, logFile).start();//日志线程

       //检测txt文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(txtFile, true, new FileActionCallback() {
                        @Override
                        public void create(File file1) {
                            System.out.println("文件已创建\t" + file1.getAbsolutePath());
                            //onlineAnalysis.txtAnalysisRule(file);//数据抽取到 oracle
                            OnlineDataBusThread task1 =   new OnlineDataBusThread(file1,queue,"txt",onlineAnalysis);//监听线程1
                            Future<?> future = service.submit(task1);//开启该线程
                            try {
                                future.get();// 结果
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
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

        //检测demo1 车型1 文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(demo1File, true, new FileActionCallback() {
                        @Override
                        public void create(File file2) {
                            System.out.println("文件已创建\t" + file2.getAbsolutePath());
                            //onlineAnalysis.demoAnalysisRule(file2);//数据抽取到 oracle
                            //创建业务线程
                            OnlineDataBusThread task2 =  new OnlineDataBusThread(file2,queue,"demoCar1",onlineAnalysis);//监听线程1
                            Future<?> future = service.submit(task2);//开启该线程
                            try {
                                future.get();// 结果
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
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

        //检测demo2 车型2 文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(demo2File, true, new FileActionCallback() {
                        @Override
                        public void create(File file2) {
                            System.out.println("文件已创建\t" + file2.getAbsolutePath());
                            //onlineAnalysis.demoAnalysisRule(file2);//数据抽取到 oracle
                            //创建业务线程
                            OnlineDataBusThread task2 =  new OnlineDataBusThread(file2,queue,"demoCar2",onlineAnalysis);//监听线程1
                            Future<?> future = service.submit(task2);//开启该线程
                            try {
                                future.get();// 结果
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
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
        //检测demo1 车型1 文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(demo1File, true, new FileActionCallback() {
                        @Override
                        public void create(File file2) {
                            System.out.println("文件已创建\t" + file2.getAbsolutePath());
                            //onlineAnalysis.demoAnalysisRule(file2);//数据抽取到 oracle
                            //创建业务线程
                            OnlineDataBusThread task2 =  new OnlineDataBusThread(file2,queue,"demoCar1",onlineAnalysis);//监听线程1
                            Future<?> future = service.submit(task2);//开启该线程
                            try {
                                future.get();// 结果
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
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

        //检测共享文件 txt 文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(shareTxtFile, true, new FileActionCallback() {
                        @Override
                        public void create(File file) {
                            System.out.println("文件已创建\t" + file.getAbsolutePath());
                        }
                        @Override
                        public void delete(File file2) {
                            System.out.println("文件已删除\t" + file2.getAbsolutePath());
                            //删除操作的 业务
                        }
                        @Override
                        public void modify(File file) {
                            System.out.println("文件已修改\t" + file.getAbsolutePath());
                            //修改操作的 业务
                            //创建业务线程
                            ShareFileCopeThread task =  new ShareFileCopeThread(file,txtDir,queue,"Txt");//监听线程1
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

        //检测共享文件 demo 车型2 文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(shareDemo1File, true, new FileActionCallback() {
                        @Override
                        public void create(File file) {
                            System.out.println("文件已创建\t" + file.getAbsolutePath());
                        }
                        @Override
                        public void delete(File file2) {
                            System.out.println("文件已删除\t" + file2.getAbsolutePath());
                            //删除操作的 业务
                        }
                        @Override
                        public void modify(File file) {
                            System.out.println("文件已修改\t" + file.getAbsolutePath());
                            //修改操作的 业务
                            //创建业务线程
                            ShareFileCopeThread task =  new ShareFileCopeThread(file,demo1Dir,queue,"demoCar1");//监听线程1
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

        //检测共享文件 demo 车型1 文件夹的 线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new WatchDir(shareDemo2File, true, new FileActionCallback() {
                        @Override
                        public void create(File file) {
                            System.out.println("文件已创建\t" + file.getAbsolutePath());
                        }
                        @Override
                        public void delete(File file2) {
                            System.out.println("文件已删除\t" + file2.getAbsolutePath());
                            //删除操作的 业务
                        }
                        @Override
                        public void modify(File file) {
                            System.out.println("文件已修改\t" + file.getAbsolutePath());
                            //修改操作的 业务
                            //创建业务线程
                            ShareFileCopeThread task =  new ShareFileCopeThread(file,demo2Dir,queue,"demoCar2");//监听线程1
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
        System.out.println("正在监视文件夹:" + txtFile.getAbsolutePath());
        System.out.println("正在监视文件夹2:" + demo1File.getAbsolutePath());
        System.out.println("正在监视文件夹3:" + demo2File.getAbsolutePath());
        System.out.println("正在监视共享文件夹1:" + shareTxtFile.getAbsolutePath());
        System.out.println("正在监视共享文件夹2:" + shareDemo1File.getAbsolutePath());
        System.out.println("正在监视共享文件夹3:" + shareDemo2File.getAbsolutePath());
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
      String outLog = business+"ShareFile="+file.getName()+"|";
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
        String outLog = business+"="+file.getName()+"|";
        try {
            buffer.put(outLog.getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //业务
        if (business != null && "demoCar1".equals(business)) {
            onlineAnalysis.demoAnalysisRule(file,"car1");//数据抽取到 oracle

        } else  if (business != null && "demoCar2".equals(business)) {
            onlineAnalysis.demoAnalysisRule(file, "car2");//数据抽取到 oracle

        }
        else if (business != null && "txt".equals(business)) {
            onlineAnalysis.txtAnalysisRule(file);//数据抽取到 oracle
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