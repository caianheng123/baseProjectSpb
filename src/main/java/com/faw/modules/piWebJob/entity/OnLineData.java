/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: OnLineData
 * Author:   JG
 * Date:     2020/5/25 16:15
 * Description: 在线数据解析实体
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.piWebJob.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.faw.base.annotation.DemoFild;
import com.faw.base.annotation.TxtFild;
import com.faw.base.entity.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈在线数据解析实体〉
 *
 * @author JG
 * @create 2020/5/25
 * @since 1.0.0
 */
@Data
public class OnLineData extends IdEntity{
    public  OnLineData(){
        super();
    }

    //这串数字的后八位 钢号
    @TxtFild(value = "Part ID")
    private String partId;

    //@TxtFild(value = "PartID 2")
    //private String partId2;

    @TxtFild(value = "MO")
    private String month;

    @TxtFild(value = "DAY")
    private String day;

    @TxtFild(value = "YR")
    private String year;

    @TxtFild(value = "HR")
    private String hour;

    @TxtFild(value = "MIN")
    private String minute;

    @TxtFild(value = "SEC")
    private String seconds;

    private String measureTime;

    private String model;

    private String factory;

    @TxtFild(value = "GAUGE ID")
    private String unitName;

    private String measurePoint;

    private  String measureCategory;//类别

    private  String categoryDirect;//方向

    private String measureX;

    private String  measureY;

    private String  measureZ;

    private String  measureP;

    private String justNo;  //钢号

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Date getUpdateTime() {
        if(updateTime == null){
            updateTime = new Date();
        }
        return updateTime;
    }

    private String reserve1;//预留字段1

    private String reserve2;//预留字段1

    private String reserve3;//预留字段1

    public void txtSetMeasureTime(){
        String monthStr="";
        int month = Integer.parseInt(this.month);
        if(month<10){
            monthStr = "0"+String.valueOf(month);
        }else{
            monthStr = String.valueOf(month);
        }
        String dayStr="";
        int day = Integer.parseInt(this.day);
        if(day<10){
            dayStr = "0"+String.valueOf(day);
        }else{
            dayStr = String.valueOf(day);
        }
        String hourStr="";
        int hour = Integer.parseInt(this.hour);
        if(hour<10){
            hourStr = "0"+String.valueOf(hour);
        }else{
            hourStr = String.valueOf(hour);
        }

        String minuteStr="";
        int minute = Integer.parseInt(this.minute);
        if(minute<10){
            minuteStr = "0"+String.valueOf(minute);
        }else{
            minuteStr = String.valueOf(minute);
        }

        String secondsStr="";
        int second = Integer.parseInt(this.seconds);
        if(second<10){
            secondsStr = "0"+String.valueOf(second);
        }else{
            secondsStr = String.valueOf(second);
        }
        this.measureTime = this.year+"-"+monthStr+"-"+dayStr+" "+hourStr+":"+minuteStr+":"+secondsStr;
    }
}
