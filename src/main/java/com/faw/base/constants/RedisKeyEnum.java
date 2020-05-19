package com.faw.base.constants;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liushengbin
 * @version 1.0
 * @description RedisKey前缀
 * @email liushengbin7@gmail.com
 * @date 2018/10/31 11:44 AM
 */
public enum RedisKeyEnum {

    SYS_USER_AUTHENTICATION_INFO("sys:user:authenticationInfo:","用户认证信息"),
    SYS_USER_AUTHORIZATION_INFO("sys:user:authorizationInfo:","用户权限信息"),
    SYS_USER("sys:user:","用户"),
    SYS_DICT("sys:dict:", "业务字典"),
    SYS_MENU("sys:menu:", "菜单信息"),
    SYS_CONFIG("sys:config:","系统参数配置");
    /**
     * key前缀
     */
    @Getter
    @Setter
    private String key;
    /**
     * 模块描述
     */
    @Getter
    @Setter
    private String module;

    RedisKeyEnum(String key, String module) {
        this.key = key;
        this.module = module;
    }


}
