/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TimeOutLog
 * Author:   JG
 * Date:     2019/11/28 14:40
 * Description: 实体类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.outPutLog.entity;

/**
 * 〈一句话功能简述〉<br> 
 * 〈实体类〉
 *
 * @author JG
 * @create 2019/11/28
 * @since 1.0.0
 */

public class TimeOutLog {
    private String product_hour;

    public TimeOutLog(){

    }

    public TimeOutLog(String product_hour) {
        this.product_hour = product_hour;
    }

    public String getProduct_hour() {
        return product_hour;
    }

    public void setProduct_hour(String product_hour) {
        this.product_hour = product_hour;
    }
}
