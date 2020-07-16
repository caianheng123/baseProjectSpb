package com.faw.modules.piWebJob.service;

import java.io.File;

/**
 * Created by JG on 2020/5/22.
 */
public interface IOnlineAnalysis {

    public void  txtAnalysisRule(File file);

    public void  demoAnalysisRule(File file);

    public void  superAlmostExtractor(File file);

    public void  stablePassRateExtractor(File file);
}
