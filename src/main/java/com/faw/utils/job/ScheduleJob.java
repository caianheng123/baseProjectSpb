package com.faw.utils.job;

import com.alibaba.fastjson.JSONObject;

import com.faw.modules.jobView.service.ScheduleJobLogService;
import com.faw.utils.common.SpringContextUtils;
import com.faw.utils.job.ScheduleRunnable;
import com.faw.modules.jobView.entity.ScheduleJobEntity;
import com.faw.modules.jobView.entity.ScheduleJobLogEntity;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 定时任务
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
public class ScheduleJob extends QuartzJobBean {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private ExecutorService service = Executors.newSingleThreadExecutor(); //线程池
	
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Object o = context.getMergedJobDataMap().get(ScheduleJobEntity.JOB_PARAM_KEY);
		String str = JSONObject.toJSONString(o);
		ScheduleJobEntity scheduleJob = JSONObject.parseObject(str,ScheduleJobEntity.class);
//        ScheduleJobEntity scheduleJob = (ScheduleJobEntity) context.getMergedJobDataMap()
//        		.get(ScheduleJobEntity.JOB_PARAM_KEY);

		ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) SpringContextUtils.getBean("scheduleJobLogService");
        //数据库保存执行记录
        ScheduleJobLogEntity log = new ScheduleJobLogEntity();
        log.setJobId(scheduleJob.getId());
        log.setBeanName(scheduleJob.getBeanName());
        log.setMethodName(scheduleJob.getMethodName());
        log.setParams(scheduleJob.getParams());
        log.setCreateTime(new Date());
        
        //任务开始时间
        long startTime = System.currentTimeMillis();
        
        try {
            //执行任务
        	logger.info("任务准备执行，任务ID：" + scheduleJob.getId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(),
            		scheduleJob.getMethodName(), scheduleJob.getParams()); //创建个线程
            Future<?> future = service.submit(task);//开启该线程
            
			future.get();// 结果
			
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			log.setTimes((int)times);
			//任务状态    1：成功    0：失败
			log.setJobStatus(1);
			
			logger.info("任务执行完毕，任务ID：" + scheduleJob.getId() + "  总共耗时：" + times + "毫秒");
		} catch (Exception e) {
			logger.error("任务执行失败，任务ID：" + scheduleJob.getId(), e);
			
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			log.setTimes((int)times);
			
			//任务状态    1：成功    0：失败
			log.setJobStatus(0);
			log.setError(StringUtils.substring(e.toString(), 0, 2000));
		}finally {
			//获取spring bean
			scheduleJobLogService.insert(log);
		}
    }
}
