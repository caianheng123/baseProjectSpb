package com.faw.modules.piWebJob.service.impl;

import com.faw.config.PdfClownKeyConfig;
import com.faw.modules.piWebJob.dao.OnlineDataDao;
import com.faw.modules.piWebJob.service.IPdfExtract;
import com.faw.modules.stablePassRate.entity.PiwebStablePassrate;
import com.faw.modules.superAlmost.entity.PiwebSuperAlmost;
import com.faw.utils.pdfBox.PDFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jiazh on 2020/7/3.
 */
@Service
public class PdfExtractorImpl implements IPdfExtract {

    @Autowired
    private PdfClownKeyConfig pdfClownKeyConfig;

    @Autowired
    private OnlineDataDao dao;

    @Override
    public void superAlmostExtractor(File file) {
         List<String> allRows = new ArrayList<>();
         List<List<String>> pageList = PDFUtils.redTable(file);
         List<PiwebSuperAlmost> results = new ArrayList<>();

         for(List<String> rows : pageList){
             for(int x=1;x<rows.size();x++){
                 allRows.add(rows.get(x));
             }
         }
         for(String line : allRows ){
             String[] clowns =  line.split("\\|");
             Map<String,Integer> superAlmostMaoKey =  pdfClownKeyConfig.getSuperAlmost();
             //没行一个实体
                 PiwebSuperAlmost superAlmost = new PiwebSuperAlmost();
                 Class clazz = superAlmost.getClass();
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                 //循环配置文件给 实体的 参数赋值
                 for(String key : superAlmostMaoKey.keySet()){
                     try {
                         Field entityFile =  clazz.getDeclaredField(key);
                         entityFile.setAccessible(true);
                         if("createTime".equals(key)){ //时间单独处理
                             entityFile.set(superAlmost,sdf.parse(clowns[superAlmostMaoKey.get(key)]));
                         }
                         else{
                             entityFile.set(superAlmost,clowns[superAlmostMaoKey.get(key)].toString());
                         }
                     } catch (NoSuchFieldException e) {
                         e.printStackTrace();
                     } catch (IllegalAccessException e) {
                         e.printStackTrace();
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }

                 }
               //针对  字符串的 dataTime
               superAlmost.setDateTime(sdf2.format(superAlmost.getCreateTime()));
             results.add(superAlmost);
         }

         if(results.size()>0){
             dao.insertListSuper(results);
         }

    }

    @Override
    public void stablePassRateExtractor(File file) {
        List<List<String>> pageList = PDFUtils.redTable(file);
        List<String> allRows = new ArrayList<>();
        List<PiwebStablePassrate> results = new ArrayList<>();

        for(List<String> rows : pageList){
            for(int x=0;x<rows.size();x++){
                allRows.add(rows.get(x));
            }
        }

        for(String line : allRows ){
            String[] clowns =  line.split("\\|");

            Map<String,Integer> stablePassRateKey =  pdfClownKeyConfig.getStablePassRate();
            if(clowns.length<stablePassRateKey.size()){
                continue;
            }
            //没行一个实体
            PiwebStablePassrate stablePassrate = new PiwebStablePassrate();
            Class clazz = stablePassrate.getClass();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            //循环配置文件给 实体的 参数赋值
            for(String key : stablePassRateKey.keySet()){
                try {
                    Field entityFile =  clazz.getDeclaredField(key);
                    entityFile.setAccessible(true);
                    if("createTime".equals(key)){ //时间单独处理
                        entityFile.set(stablePassrate,sdf.parse(clowns[stablePassRateKey.get(key)]));
                    }
                    else{
                        entityFile.set(stablePassrate,clowns[stablePassRateKey.get(key)]);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            results.add(stablePassrate);
        }
        if(results.size()>0){
           dao.insertListStable(results);
        }

    }
}
