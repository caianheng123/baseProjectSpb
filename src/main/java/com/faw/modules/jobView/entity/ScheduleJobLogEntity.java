package com.faw.modules.jobView.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.faw.base.entity.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务日志
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
@TableName("schedule_job_log")
@Data
public class ScheduleJobLogEntity extends IdEntity {
	private static final long serialVersionUID = 1L;
	

	/**
	 * 任务id
	 */
	private String jobId;
	
	/**
	 * spring bean名称
	 */
	private String beanName;
	
	/**
	 * 方法名
	 */
	private String methodName;
	
	/**
	 * 参数
	 */
	private String params;

	/**
	 * 任务状态    1：成功    0：失败
	 */
	@TableField("job_status")
	private Integer jobStatus;
	
	/**
	 * 失败信息
	 */
	@TableField("error")
	private String error;
	
	/**
	 * 耗时(单位：毫秒)
	 */
	@TableField("times")
	private Integer times;

	/**
	 * 创建时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@TableField("create_time")
	private Date createTime;

	/**
	 * 租户代码
	 */
	@TableField("tenant_code")
	private String tenantCode;
	


	
}
