

package com.faw.utils.common;

import com.baomidou.mybatisplus.plugins.Page;
import com.faw.utils.xss.SQLFilter;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询参数
 *
 * @author Mark liushengbin7@gmail.com
 * @since 2.0.0 2018-03-14
 */
public class Query<T> extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
    /**
     * mybatis-plus分页参数
     */
    private Page<T> page;
    /**
     * 当前页码
     */
    private int currPage = 1;
    /**
     * 每页条数
     */
    private int limit = 10;

    public Query(Map<String, Object> params){
        this.putAll(params);

        //分页参数
        if(params.get("page") != null){
            currPage = Integer.parseInt((String)params.get("page"));
        }
        if(params.get("limit") != null){
            limit = Integer.parseInt((String)params.get("limit"));
        }

        this.put("offset", (currPage - 1) * limit);
        this.put("page", currPage);
        this.put("limit", limit);

        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = SQLFilter.sqlInject((String)params.get("sidx"));
        String order = SQLFilter.sqlInject((String)params.get("order"));
        this.put("sidx", sidx);
        this.put("order", order);

        //mybatis-plus分页
        this.page = new Page<>(currPage, limit);

        //排序
        if(StringUtils.isNotBlank(sidx) && StringUtils.isNotBlank(order)){
            this.page.setOrderByField(sidx);
            this.page.setAsc("ASC".equalsIgnoreCase(order));
        }/* Map<String,Object> paramsMap=new HashMap<>();
        //得到typeIndex的值
        String index=params.get("index").toString();
        if("10".equals(index)){
            paramsMap.put("DATE_TIME",params.get("dateTime"));
        }else if("20".equals(index)){
            paramsMap.put("YEAR_MONTH_DAY",params.get("dateTime"));
        }else if("30".equals(index)){
            paramsMap.put("YEAR_MONTH_DAY",params.get("dateTime"));
        }else if("40".equals(index)){
            paramsMap.put("YEAR_MONTH_DAY",params.get("dateTime"));
        }else if("50".equals(index)){
            paramsMap.put("DATE_TIME",params.get("dateTime").toString().substring(0,7));
        }else if("60".equals(index)){//待定,因为该字段为null,说明在录入的时候就没有录入这个字段
            paramsMap.put("CREATE_DATE",params.get("dateTime"));
        }else if("70".equals(index)){
            paramsMap.put("YEAR_MONTH_DAY",params.get("dateTime"));
        }else if("80".equals(index)){//待定，没有该字段
            paramsMap.put("YEAR_MONTH_DAY",params.get("dateTime"));
        }else if("90".equals(index)){//待定，该字段的日期格式为xxxx/xx/xx/
            paramsMap.put("DATA_D",params.get("dateTime"));
        }else if("100".equals(index)){//待定，该字段的日期格式为xxxx/xx/xx/
            paramsMap.put("DATA_D",params.get("dateTime"));
        }else if("110".equals(index)){//没有这个字段
            paramsMap.put("DATA_D",params.get("dateTime"));
        }
        this.page.setCondition(paramsMap);*/



    }

    public Page<T> getPage() {
        return page;
    }

    public int getCurrPage() {
        return currPage;
    }

    public int getLimit() {
        return limit;
    }
}
