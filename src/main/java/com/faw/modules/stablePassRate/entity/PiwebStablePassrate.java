package com.faw.modules.stablePassRate.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.faw.base.entity.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by Jiazh on 2020/7/3.
 */
@Data
@TableName("PIWEB_STABLE_PASSRATE")
public class PiwebStablePassrate extends IdEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 基地/工厂
     */
    @TableField("factory")
    private String factory;
    /**
     * 车型名
     */
    @TableField("car_name")
    private String carName;
    /**
     * 零件名
     */
    @TableField("part_name")
    private String partName;
    /**
     * 数据类别
     */
    @TableField("category_data")
    private String categoryData;
    /**
     * 功能尺寸稳定性 绿色指标
     */
    @TableField("stable_func_green")
    private String stableFuncGreen;
    /**
     * 功能尺寸稳定性 黄色指标
     */
    @TableField("stable_func_yellow")
    private String stableFuncYellow;
    /**
     * 功能尺寸稳定性 红色指标
     */
    @TableField("stable_func_red")
    private String stableFuncRed;
    /**
     * 普通尺寸稳定性 绿色指标
     */
    @TableField("stable_com_green")
    private String stableComGreen;
    /**
     * 普通尺寸稳定性 黄色指标
     */
    @TableField("stable_com_yellow")
    private String stableComYellow;
    /**
     * 普通尺寸稳定性 红色指标
     */
    @TableField("stable_com_red")
    private String stableComRed;
    /**
     * 功能尺寸合格率 绿色指标
     */
    @TableField("passRate_func_green")
    private String passrateFuncGreen;
    /**
     * 功能尺寸合格率 黄色指标
     */
    @TableField("passRate_func_yellow")
    private String passrateFuncYellow;
    /**
     * 功能尺寸合格率 红色指标
     */
    @TableField("passRate_func_red")
    private String passrateFuncRed;
    /**
     * 普通尺寸合格率 绿色指标
     */
    @TableField("passRate_com_green")
    private String passrateComGreen;
    /**
     * 普通尺寸合格率 黄色指标
     */
    @TableField("passRate_com_yellow")
    private String passrateComYellow;
    /**
     * 普通尺寸合格率 红色指标
     */
    @TableField("passRate_com_red")
    private String passrateComRed;

    @TableField("CREATE_TIME")
    private Date createTime;

}
