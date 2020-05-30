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

import com.faw.base.annotation.DemoFild;
import com.faw.base.annotation.TxtFild;
import com.faw.base.entity.IdEntity;
import lombok.Data;

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

    @TxtFild(value = "Part ID")
    private String partId;

    @TxtFild(value = "PartID 2")
    private String partId2;

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

    @TxtFild(value = "MODEL")
    @DemoFild(value = "PV(AUTO)")
    private String model;

    @TxtFild(value = "FIXTURE")
    private String fixTure;

    private String factory;

    @TxtFild(value = "GAUGE ID")
    @DemoFild(value = "PR(PARTNM)")
    private String unitName;

    private String measurePoint;

    private  String measureCategory;//类别

    private  String categoryDirect;//方向

    private String measureX;

    private String  measureY;

    private String  measureZ;

    private String  measureP;

    private String  theoryX;

    private String  theoryY;

    private String  theoryZ;

    private String justNo;  //钢号


    private String reserve1;//预留字段1

    private String reserve2;//预留字段1

    private String reserve3;//预留字段1

    public void txtSetMeasureTime(){
        String hourStr="";
        int hour = Integer.parseInt(this.hour);
        if(hour<10){
            hourStr = "0"+String.valueOf(hour);
        }else{
            hourStr = String.valueOf(hour);
        }
        this.measureTime = this.year+"-"+this.month+"-"+this.day+" "+hourStr+":"+this.minute+":"+this.seconds;
    }
}
