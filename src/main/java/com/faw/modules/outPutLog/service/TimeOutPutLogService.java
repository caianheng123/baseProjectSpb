/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TimeOutPutLogService
 * Author:   JG
 * Date:     2019/11/28 10:58
 * Description: 小时产量日志
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.outPutLog.service;

import com.alibaba.fastjson.JSON;
import com.faw.dataSource.DataSourceNames;
import com.faw.dataSource.annotation.DataSource;
import com.faw.modules.outPutLog.dao.TimeOutPutLogDao;
import com.faw.modules.outPutLog.entity.TimeOutLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈小时产量日志〉
 *
 * @author JG
 * @create 2019/11/28
 * @since 1.0.0
 */
@Service
public class TimeOutPutLogService {

   private Logger log = LoggerFactory.getLogger(TimeOutPutLogService.class);

    @Autowired
    TimeOutPutLogDao dao;

 @DataSource(name = DataSourceNames.SECOND)
 public void timeOutputLog(){
        List<TimeOutLog> m = dao.getAllOutPutByHours();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        log.info("*************************");
        log.info(simpleDateFormat.format(new Date())+" 产量列表" +JSON.toJSONString(m));
        log.info("*************************");
    }
}
