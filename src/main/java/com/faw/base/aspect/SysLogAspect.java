package com.faw.base.aspect;

import com.faw.base.annotation.SysLog;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by Jiazh on 2020/8/10.
 */
@Aspect
@Component
public class SysLogAspect {
    //日志服務 保存日志信息

    @Pointcut("@annotation(com.faw.base.annotation.SysLog)")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog syslog = method.getAnnotation(SysLog.class);

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try{
            String params = new Gson().toJson(args);
        }catch (Exception e){

        }

        //获取request

        //设置IP地址

        //用户名

        //保存系统日志

    }
}

