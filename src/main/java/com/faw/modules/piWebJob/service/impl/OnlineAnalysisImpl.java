/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: OnlineAnalysis
 * Author:   JG
 * Date:     2020/5/22 10:53
 * Description: 在线数据分析
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.piWebJob.service.impl;

import com.faw.base.annotation.TxtFild;
import com.faw.modules.piWebJob.dao.OnlineDataDao;
import com.faw.modules.piWebJob.entity.OnLineData;
import com.faw.modules.piWebJob.service.IOnlineAnalysis;
import com.faw.utils.io.FileUtils;
import org.apache.hadoop.util.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br> 
 * 〈在线数据分析〉
 *
 * @author JG
 * @create 2020/5/22
 * @since 1.0.0
 */
@Component("OnlineAnalysis")
public class OnlineAnalysisImpl implements IOnlineAnalysis {
    //txt数据文件夹
    @Value("${abf.onlineData.dirPathTxt}")
    private  String txtDir;

    //demo数据文件夹
    @Value("${abf.onlineData.dirPathDemo}")
    private  String demoDir;

    @Autowired
    private OnlineDataDao onlineDataDao;

    //txt 格式文件分析
    public  void  txtAnalysisRule(){
        List<HashMap<String,Object>> results = 	FileUtils.readDir(txtDir);
        for(HashMap m : results){ // 一个文件一个文件解析
          ArrayList<String> lineVue = (ArrayList<String>) m.get("lineValues");
            List<String []> clowns = new ArrayList<>();// 全部row信息
            List<String []> values = new ArrayList<>();// 全部value信息
            List<List<String>> baseInfoRows = new ArrayList<>();// 具体到某个对象的 公共信息
            List<Set<String>> measurePoints = new ArrayList<>(); // 筛选测量点名

          for(int x = 0 ; x < lineVue.size();x++){
              if( x%2 == 0){ //clown
                  String[]  rowArray =  lineVue.get(x).split("\t");
                  clowns.add(rowArray);
              }else{//value
                  String[]  valueArray =  lineVue.get(x).split("\t");
                  values.add(valueArray);
              }
          }

          //匹配某个对象的正则  筛选出那些对象
            String patterObj = "^(D|B|A|LS|US|UR|LR)+\\s+\\S*\\s+(X|Y|Z)+$";
            Pattern p= Pattern.compile(patterObj);


            for(int x = 0;x< clowns.size();x++){
                String[] recodeArray = clowns.get(x);
                //循环所有的row信息 按空格分隔  不含空格的为 公共信息  含空格且长度为3第二项为车型
                List<String> baseInfoList = new ArrayList();
                Set<String>  pointInfoSet = new HashSet<>();

               for(int y = 0; y< recodeArray.length; y++){
                   Matcher matcher =p.matcher(recodeArray[y]);
                   if(matcher.find()){//匹配到具体obj
                       String[] infoArr = recodeArray[y].split("\\s+");
                       if(infoArr.length == 3){
                           pointInfoSet.add(infoArr[1]);//车型
                       }
                   }else{
                       baseInfoList.add(recodeArray[y]);
                   }
               }
                baseInfoRows.add( baseInfoList );
                measurePoints.add(pointInfoSet);
            }

            //组合title和value map list
            List<Map<String,String>> dataMapList = new ArrayList<>();
            Map<String,String> dataMap = null;
            for(int x=0; x< clowns.size() ; x++){
                String[] valueArr = values.get(x);
                String[] clownArr = clowns.get(x);

                dataMap = new HashMap();
                for(int y=0; y < clownArr.length;y++){
                    dataMap.put(String.valueOf(clownArr[y]),String.valueOf(valueArr[y]));
                }
                dataMapList.add(dataMap);
            }

            //创建实例
            List<OnLineData> onLineDatas = new ArrayList<>();

            //1 根据测量点  循环clowns 获取指定的 clowns信息  在根据转向 分出要创建几个实例
            List<List<String>> objClowns = new ArrayList<>();

            for( Set<String>  pointInfoSet :measurePoints){//获取测量点set
                for(String clownStr : pointInfoSet){
                    List objClownList = new ArrayList();
                    for(String [] clownArr : clowns){//循环所有clown
                        for(String clown : clownArr){
                            //匹配筛选出的测量点
                            if(clown.indexOf(clownStr)!=-1){
                                objClownList.add(clown);
                            }

                        }

                    }
                    objClowns.add(objClownList);
                }

            }



            //2 根据 objClowns 内的 数据 根据方向创建对象
            for(List<String> clownObjs : objClowns){
                Set<String> directionSet = new HashSet<>();
                for(String clown : clownObjs){//一个测量点
                    //根据空格拆分出方向
                    String[] objInfo = clown.split(" "); //测量点拆分出 方向 和
                    if(objInfo.length > 0){
                        String direction = objInfo[0];
                        directionSet.add(direction);
                    }
                }

                try {

                        //循环方向
                        for(String direction : directionSet){//一个方向创建一个对象
                            OnLineData onLineData = new OnLineData();
                            Field[] fields = onLineData.getClass().getDeclaredFields();
                            for(Field field:fields){
                                field.setAccessible(true);
                                if(field.isAnnotationPresent(TxtFild.class)){ //将指定的key的值设置到对象上
                                    TxtFild txtFild =   field.getAnnotation(TxtFild.class);
                                    String key = txtFild.value();
                                    for(Map<String,String> valMap:dataMapList){//存值的maplist
                                        field.set(onLineData,valMap.get(key));

                                    }
                                }

                            }
                            for(String clown : clownObjs) {//所有方向的key
                                //设置 测量 x y z 值
                                if(clown.split(" ")[0].indexOf(direction)!=-1){

                                    //设置测量点
                                    onLineData.setMeasurePoint(clown);

                                    //设置零件编号 取测量点 _ 分隔 第一个信息
                                    String[] clownSpiltInfo = clown.split("_");
                                    if(clownSpiltInfo.length>0){
                                        String unitStr = clownSpiltInfo[0];
                                        String unitNumber = unitStr.split(" ")[1];
                                        onLineData.setUnitNumber(unitNumber);
                                    }

                                    if(clown.indexOf("X") !=-1){ //取X轴信息
                                        for(Map<String,String> valMap:dataMapList){//存值的maplist
                                            onLineData.setMeasureX(valMap.get(clown));
                                        }
                                    }
                                    if(clown.indexOf("Y") !=-1){ //取Y轴信息
                                        for(Map<String,String> valMap:dataMapList){//存值的maplist
                                            onLineData.setMeasureY(valMap.get(clown));
                                        }
                                    }
                                    if(clown.indexOf("Z") !=-1){ //取Z轴信息
                                        for(Map<String,String> valMap:dataMapList){//存值的maplist
                                            onLineData.setMeasureZ(valMap.get(clown));
                                        }
                                    }
                                }

                            }
                            //包含该方向
                            onLineData.setDirect(direction);
                            //设置测量时间
                            onLineData.txtSetMeasureTime();

                            onLineDatas.add(onLineData);
                        }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

            //持久化到数据库
            if(onLineDatas.size()>0){
                onlineDataDao.insertMyBatch(onLineDatas);
            }

        }
    }
    //demo 格式文件分析
    public  void  demoAnalysisRule(){
        List<HashMap<String,Object>> results = 	FileUtils.readDir(demoDir);
        //多个文件场景
        HashMap<String,Object> file = new HashMap<>();
        for(HashMap fileMap: results){
                //获取指定的文件
            file = fileMap;
        }
        ArrayList<String> lineValue = (ArrayList<String>) file.get("lineValues");
        Map<String,List<String>>  objectMap = new HashMap<>(); //存放对象

        String key = "";
        for(String str: lineValue){
            // 每个独立的信息 都是 OUTPUT/ 开头的

            if(str.indexOf("OUTPUT/")!=-1){
                //判断当前字符串是否包含, 包含拆分 取 第一个  在用空格拆分取第二个  再取()里信息 做key
                String newStr = str;
                if(str.indexOf(",")!=-1){
                    String[] leveOneArr = str.split(",");
                    newStr = leveOneArr[0];
                }
                if(newStr.indexOf(" ")!= -1){
                    String[] levetwoArr = str.split(" ");
                    newStr = levetwoArr[1];
                }

                Matcher matcher=Pattern.compile("\\(").matcher(newStr);
                int start = 0;
                if(matcher.find()){
                    start =  matcher.start();
                }

                key = newStr.substring(start+1,newStr.lastIndexOf(")"));
                if(!objectMap.containsKey(key)){
                      objectMap.put(key,new ArrayList<String>());
                }
                continue;
            }else if("".equals(key)){
                continue;
            }
            ArrayList<String> objInfoList = (ArrayList<String>) objectMap.get(key);

            objInfoList.add(str);

            objectMap.put(key,objInfoList);

        }

        List<String> baseInfos = new ArrayList<>();


        if(objectMap.size()>0){
            //把基本信息拆出来  key为  R1 , DATE_TIME  R99
            if(objectMap.containsKey("R1")){
                baseInfos.addAll(objectMap.get("R1"));
                objectMap.remove("R1");
            }
            if(objectMap.containsKey("DATE_TIME")){
                baseInfos.addAll(objectMap.get("DATE_TIME"));
                objectMap.remove("DATE_TIME");
            }

        }

        //实例化数据
        for(String mapKey : objectMap.keySet()){
            List<String> mapList = objectMap.get(mapKey);
            for(String info : mapList){

            }
        }
    }
}
