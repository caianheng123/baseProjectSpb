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

public class BaseEntity extends IdEntity {


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
     * 创建人
     */
    @TableField("create_by")
    private String createBy;
    /**
     * 更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private Date updateTime;
    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 状态
     */
    @TableField("doc_status")
    protected Integer docStatus;

    /**
     * 租户代码
     */
    @TableField("tenant_code")
    private String tenantCode;

    public Date getCreateTime() {
        if(createTime == null){
            createTime = new Date();
        }
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreateBy() {
        return createBy;
    }

    public String getUpdateBy() {
        return updateBy;
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


    public Integer getDocStatus() {
        if(docStatus == null){
            docStatus = Integer.valueOf(STATUS_NORMAL);
        }
        return docStatus;
    }

    public void setDocStatus(Integer docStatus) {
        this.docStatus = docStatus;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }
}
