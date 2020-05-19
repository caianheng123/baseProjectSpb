package com.faw.modules.jobView.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.faw.modules.jobView.entity.ScheduleJobLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
@Mapper
public interface ScheduleJobLogDao extends BaseMapper<ScheduleJobLogEntity> {
	
}
