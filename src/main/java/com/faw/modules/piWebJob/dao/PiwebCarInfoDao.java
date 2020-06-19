package com.faw.modules.piWebJob.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jiazh on 2020/6/19.
 */
@Mapper
public interface PiwebCarInfoDao {
    List<HashMap<String,Object>> queryCarInfo();
}
