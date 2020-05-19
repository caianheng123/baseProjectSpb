package com.faw.base.constants;

/**
 * 常量
 * 
 * @author liushengbin
 * @email liushengbin7@gmail.com
 * @date 2016年11月15日 下午1:23:52
 */
public class Constant {
	/** 超级管理员ID */
	public static final String SUPER_ADMIN = "1";

    /**
     * 树根节点的父节点id值
     */
    public static final String ROOT_NODE_PARENT_ID = "0";

    /**
     * 树层级数据，ID值的分隔符
     */
    public static final String HIERARCHY_ID_SPLIT = ",";

    /**
     * 云存储配置KEY
     */
    public final static String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";

	/**
	 * 菜单类型
	 * 
	 * @author liushengbin
	 * @email liushengbin7@gmail.com
	 * @date 2016年11月15日 下午1:24:29
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     * 
     * @author liushengbin
     * @email liushengbin7@gmail.com
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(1),
        /**
         * 暂停
         */
    	PAUSE(0);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    /**
     * 云服务商
     */
    public enum BusinessType {
        /**
         * 昨天
         */
        LASTEDAY("lastDay"),
        /**
         * 当天
         */
        CURRENTDAY("currentDay"),
        /**
         * 当周
         */
        CURRENTWEEK("currentWeek"),
        /**
         * 当月
         */
        CURRENTMONTH("currentMonth"),
        /**
         * 当年
         */
        CURRENTYEAR("currentYear");


        private String value;

        BusinessType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
