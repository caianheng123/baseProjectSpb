package com.faw.modules.jobView.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.faw.base.annotation.SysLog;
import com.faw.modules.jobView.entity.ScheduleJobEntity;
import com.faw.modules.jobView.service.ScheduleJobService;
import com.faw.utils.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jiazh on 2020/8/10.
 */
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController {
    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 立即执行任务
     */
    @SysLog("立即执行任务")
    @PostMapping("/run")
    public R run(@RequestBody String[] jobIds){
        scheduleJobService.run(jobIds);

        return R.ok();
    }

    /**
     * 暂停定时任务
     */
    @SysLog("暂停定时任务")
    @PostMapping("/pause")
    public R pause(@RequestBody String[] jobIds){
        scheduleJobService.pause(jobIds);

        return R.ok();
    }

    /**
     * 恢复定时任务
     */
    @SysLog("恢复定时任务")
    @PostMapping("/resume")
    public R resume(@RequestBody String[] jobIds){
        scheduleJobService.resume(jobIds);

        return R.ok();
    }


    //立即执行一个任务
    @RequestMapping(value = "/run/oneJob/{jobId}",method = RequestMethod.GET)
    public R  runOne(@PathVariable("jobId") String jobId){
        String[] jobIds = new String[]{jobId};

        scheduleJobService.run(jobIds);
        return R.ok();
    }

    //立即暂停一个任务
    @RequestMapping(value = "/pause/oneJob/{jobId}",method = RequestMethod.GET)
    public R  pauseOne(@PathVariable("jobId") String jobId){
        String[] jobIds = new String[]{jobId};
        scheduleJobService.pause(jobIds);
        return R.ok();
    }

    //立即恢复一个任务
    @RequestMapping(value = "/resume/oneJob/{jobId}",method = RequestMethod.GET)
    public R  resumeOne(@PathVariable("jobId") String jobId){
        String[] jobIds = new String[]{jobId};
        scheduleJobService.resume(jobIds);
        return R.ok();
    }

    //立即重置某个一个任务
    @RequestMapping(value = "/resetOneJobById/{jobId}",method = RequestMethod.GET)
    public R  resetOneJobById(@PathVariable("jobId") String jobId) {

        scheduleJobService.resetOneJobById(jobId);
        return R.ok();
    }
}
