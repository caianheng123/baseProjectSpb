package com.faw.modules.piWebJob.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.faw.modules.piWebJob.entity.OnLineData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by JG on 2020/5/26.
 */
@Mapper
public interface OnlineDataDao extends BaseMapper<OnLineData> {
    void insertMyBatch(List<OnLineData> list);

}
