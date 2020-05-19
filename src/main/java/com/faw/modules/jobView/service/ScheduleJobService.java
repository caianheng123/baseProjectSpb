package com.faw.modules.jobView.service;

import com.baomidou.mybatisplus.service.IService;
import com.faw.modules.jobView.entity.ScheduleJobEntity;
import com.faw.utils.common.PageUtils;
import java.util.Map;

/**
 * 定时任务
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
public interface ScheduleJobService extends IService<ScheduleJobEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 保存定时任务
	 */
	void save(ScheduleJobEntity scheduleJob);
	
	/**
	 * 更新定时任务
	 */
	void update(ScheduleJobEntity scheduleJob);
	
	/**
	 * 批量删除定时任务
	 */
	void deleteBatch(String[] jobIds);
	
	/**
	 * 批量更新定时任务状态
	 */
	int updateBatch(String[] jobIds, int status);
	
	/**
	 * 立即执行
	 */
	void run(String[] jobIds);
	
	/**
	 * 暂停运行
	 */
	void pause(String[] jobIds);
	
	/**
	 * 恢复运行
	 */
	void resume(String[] jobIds);
}
