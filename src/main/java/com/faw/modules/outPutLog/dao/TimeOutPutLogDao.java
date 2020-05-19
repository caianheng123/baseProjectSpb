/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: TimeOutPutLogDao
 * Author:   JG
 * Date:     2019/11/28 10:59
 * Description: 小时产量日志dao
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.outPutLog.dao;

import com.faw.dataSource.DataSourceNames;
import com.faw.dataSource.annotation.DataSource;
import com.faw.modules.outPutLog.entity.TimeOutLog;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈小时产量日志dao〉
 *
 * @author JG
 * @create 2019/11/28
 * @since 1.0.0
 */
@Mapper
public interface TimeOutPutLogDao {

    public List<TimeOutLog> getAllOutPutByHours();
}
