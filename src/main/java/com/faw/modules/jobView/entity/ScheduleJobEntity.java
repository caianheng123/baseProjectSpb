package com.faw.modules.jobView.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.faw.base.entity.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 定时任务
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
@TableName("SCHEDULE_JOB")
@Data
public class ScheduleJobEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务调度参数key
	 */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";
	
	/**
	 * spring bean名称
	 */
	@NotBlank(message="bean名称不能为空")
	private String beanName;
	
	/**
	 * 方法名
	 */
	@NotBlank(message="方法名称不能为空")
	private String methodName;
	
	/**
	 * 参数
	 */
	private String params;
	
	/**
	 * cron表达式
	 */
	@NotBlank(message="cron表达式不能为空")
	private String cronExpression;


	/**
	 * 备注
	 */
	private String remarks;


}
