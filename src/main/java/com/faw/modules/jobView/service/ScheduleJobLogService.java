package com.faw.modules.jobView.service;

import com.baomidou.mybatisplus.service.IService;
import com.faw.modules.jobView.entity.ScheduleJobLogEntity;
import com.faw.utils.common.PageUtils;


import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
public interface ScheduleJobLogService extends IService<ScheduleJobLogEntity> {

	PageUtils queryPage(Map<String, Object> params);
	
}
