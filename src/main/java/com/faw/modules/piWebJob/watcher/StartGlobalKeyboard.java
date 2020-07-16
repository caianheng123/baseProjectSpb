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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Order(value = 1)
public class StartGlobalKeyboard implements CommandLineRunner {

    @Autowired
    private OnlineDataDirConfig onlineDataDirConfig;

    //首次启动标识
    @Value("${abf.log.firstStart}")
    private  String firstStart;

    @Value("${abf.log.firstReadLog}")
    private String firstReadLogPath;

    @Value("${abf.log.firstPdfLog}")
    private String firstPdfLog;

    @Value("${abf.log.beginTime}")
    private String beginTime;

    //设置个阻塞队列
    public static BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(1024 * 1024);

    //设置个阻塞队列
    public static BlockingQueue<byte[]> pdfQueue = new ArrayBlockingQueue<>(1024 * 1024);
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

        final File logFile = new File(firstReadLogPath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();//创建新的文件夹
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final File pdfLogFile = new File(firstPdfLog);
        if (!pdfLogFile.exists()) {
            try {
                pdfLogFile.createNewFile();//创建新的文件夹
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写日志
        new WriteLogThread(queue, pdfQueue ,logFile,pdfLogFile).start();//日志线程

        //循环 分享目录通过key获取目标文件地址 key 通过 targetDirPathMap 获取地址     通过value获取共享文件地址
        for (String key : shareDirPathMap.keySet()) {
            final  String key2 = key;
            List<String> sharePaths = shareDirPathMap.get(key);
            //创建共享文件copy线程
            for(String sharePath : sharePaths){
                final File shareFile = new File(sharePath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            read(shareFile,key2,queue,pdfQueue);
                           /* if("txt".equals(key2)){
                                read(shareFile,"txt",queue,pdfQueue);
                            }else{
                                read(shareFile,"demo",queue,pdfQueue);
                            }*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        }

    }

    //共享文件夹全读
    public void  write (String toPath,File file) throws Exception {
        //目标源文件夹  ip 是各个线程存放在不同的文件夹的名称
        File f = new File(file.getAbsolutePath());
        File toFile = new File(toPath);
        FileUtils.copy(f,toFile);
    }
    //解析方法
    public void  read (File file,String bustype,BlockingQueue<byte[]> buffer,BlockingQueue<byte[]> pdfBuffer) throws Exception {
        if(file.isDirectory()){
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file1) {
                    Date date = null;
                    Long time = null;

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

            File[] fileList = file.listFiles(fileFilter);

            for(int x =0 ;x<fileList.length;x++){
                Date date2 = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String outLog = simpleDateFormat.format(date2)+"=="+bustype+"=="+"ShareFile=="+fileList[x].getName()+"==fileTime=="+simpleDateFormat.format(new Date(fileList[x].lastModified()))+"\r\n";
                try {
                    if("txt".equals(bustype)){
                        onlineAnalysis.txtAnalysisRule(fileList[x]);
                        buffer.put(outLog.getBytes());
                    }else if("superAlmost".equals(bustype)){ //超差点pdf 解析
                        onlineAnalysis.stablePassRateExtractor(file);//数据抽取到 oracle
                        pdfBuffer.put(outLog.getBytes());
                    }else if("stablePassRate".equals(bustype)){ //稳定性合格率 解析
                        onlineAnalysis.stablePassRateExtractor(file);//数据抽取到 oracle
                        pdfBuffer.put(outLog.getBytes());
                    }else{
                        onlineAnalysis.demoAnalysisRule(fileList[x]);
                        buffer.put(outLog.getBytes());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        }


    }
    //日志线程
    class WriteLogThread extends Thread {
        private BlockingQueue<byte[]> buffer;
        private BlockingQueue<byte[]> pdfBuffer;
        private File onlineDataOFile;
        private File pdfOFile;
        private FileOutputStream onlineDataOutputStream;
        private FileOutputStream pdfOutputStream;

        public WriteLogThread(BlockingQueue<byte[]> buffer, BlockingQueue<byte[]> pdfBuffer,File onlineDataOFile,File pdfOFile) {
            this.buffer = buffer;
            this.pdfBuffer = pdfBuffer;
            this.onlineDataOFile = onlineDataOFile;
            this.pdfOFile = pdfOFile;
            try {
                this.onlineDataOutputStream = new FileOutputStream(onlineDataOFile, true);
                this.pdfOutputStream = new FileOutputStream(pdfOFile, true);
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
                        pdfOutputStream.write(pdfBuffer.take());
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
}
