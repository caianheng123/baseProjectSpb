package com.faw.modules.superAlmost.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.faw.base.entity.BaseEntity;
import com.faw.base.entity.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by Jiazh on 2020/7/3.
 */

@Data
@TableName("PIWEB_SUPER_ALMOST")
public class PiwebSuperAlmost  extends IdEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 基地/工厂
     */
    @TableField("FACTORY")
    private String factory;
    /**
     * 车型名
     */
    @TableField("CAR_NAME")
    private String carName;
    /**
     * 零件名
     */
    @TableField("PART_NAME")
    private String partName;
    /**
     * 日期
     */
    @TableField("DATE_TIME")
    private String dateTime;
    /**
     * 上公差
     */
    @TableField("O_TOL")
    private String oTol;
    /**
     * 下公差
     */
    @TableField("U_TOL")
    private String uTol;
    /**
     * 偏差
     */
    @TableField("DEVIATION")
    private String deviation;
    /**
     * 创建者
     */
    @TableField("CREATE_BY")
    private String createBy;
    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private Date createTime;
    /**
     * 修改者
     */
    @TableField("UPDATE_BY")
    private String updateBy;
    /**
     * 修改时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

    /**
     * 修改时间
     */
    @TableField("MEASURE")
    private String measure;
    /**
     * 测量点
     */
    @TableField("MERSURE_POINT")
    private String measurePoint;

    @TableField("CATEGORY_DATA")
    private String categoryData;

    public Date getUpdateTime() {
        if(updateTime == null){
            updateTime = new Date();
        }
        return updateTime;
    }
}
