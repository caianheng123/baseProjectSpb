package com.faw.modules.piWebJob.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.faw.modules.piWebJob.entity.OnLineData;
import com.faw.modules.stablePassRate.entity.PiwebStablePassrate;
import com.faw.modules.superAlmost.entity.PiwebSuperAlmost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by JG on 2020/5/26.
 */
@Mapper
public interface OnlineDataDao {
    void insertMyBatch(List<OnLineData> list);
    void insertListStable(List<PiwebStablePassrate> list);
    void insertListSuper(List<PiwebSuperAlmost> list);
}
