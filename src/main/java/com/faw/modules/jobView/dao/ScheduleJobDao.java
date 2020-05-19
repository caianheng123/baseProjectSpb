package com.faw.modules.jobView.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.faw.modules.jobView.entity.ScheduleJobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 定时任务
 *
 * @author Mark liushengbin7@gmail.com
 * @since 1.2.0 2016-11-28
 */
@Mapper
public interface ScheduleJobDao extends BaseMapper<ScheduleJobEntity> {

    /**
     * 批量更新状态
     */
    int updateBatch(Map<String, Object> map);
}
