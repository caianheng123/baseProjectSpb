package com.faw.modules.jobView.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.faw.modules.jobView.dao.ScheduleJobLogDao;
import com.faw.modules.jobView.entity.ScheduleJobLogEntity;
import com.faw.modules.jobView.service.ScheduleJobLogService;
import com.faw.utils.common.PageUtils;
import com.faw.utils.common.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
//定时任务日志服务
@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements ScheduleJobLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String jobId = (String)params.get("jobId");

		Page<ScheduleJobLogEntity> page = this.selectPage(
				new Query<ScheduleJobLogEntity>(params).getPage(),
				new EntityWrapper<ScheduleJobLogEntity>().like(StringUtils.isNotBlank(jobId),"job_id", jobId)
				.orderBy("create_time",false)
		);

		return new PageUtils(page);
	}

}
