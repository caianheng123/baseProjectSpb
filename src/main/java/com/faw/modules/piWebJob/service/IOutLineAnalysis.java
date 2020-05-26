package com.faw.modules.piWebJob.service;

/**
 * Created by JG on 2020/5/22.
 */
public interface IOutLineAnalysis {
    //车型数据抽取
    public  void carModelData();
    //目录数据抽取
    public  void cataLogData();
    //零件数据抽取
    public  void unitData();
    //测量点数据抽取
    public  void measurePointData();
    //测量数据抽取
    public  void measureData();
}
