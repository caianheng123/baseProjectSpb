package com.faw.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;

import java.util.Date;

/**
 * 基础entity，其他entity继承
 *
 * @author liushengbin
 * @email liushengbin7@gmail.com
 * @date 2018-10-22 12:42:38
 */

public class SafeEntity extends IdEntity {


    /**
     * 状态：0：禁用；1：正常；2：删除；3：冻结；4：审核、待审核；5：审核驳回；9：草稿
     */
    public static final int STATUS_DISABLE = 0;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_DELETE = 2;
    public static final int STATUS_FREEZE = 3;
    public static final int STATUS_AUDIT = 4;
    public static final int STATUS_AUDIT_BACK = 5;
    public static final int STATUS_DRAFT = 9;


    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private Date updateTime;

    public Date getCreateTime() {
        if(createTime == null){
            createTime = new Date();
        }
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        if(updateTime == null){
            updateTime = new Date();
        }
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
