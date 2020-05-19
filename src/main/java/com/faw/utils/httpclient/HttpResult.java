/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: HttpResult
 * Author:   JG
 * Date:     2019/10/19 10:12
 * Description: http return result
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.utils.httpclient;

/**
 * 〈一句话功能简述〉<br> 
 * 〈http return result〉
 *
 * @author JG
 * @create 2019/10/19
 * @since 1.0.0
 */

public class HttpResult {

    private int code;
    // 响应的响应体
    private String body;

    public HttpResult(int statusCode, String s) {
        this.code = statusCode;
        this.body = s;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
