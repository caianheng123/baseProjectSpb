/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: prodJob
 * Author:   JG
 * Date:     2020/5/20 11:35
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.faw.modules.piWebJob;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author JG
 * @create 2020/5/20
 * @since 1.0.0
 */
@Component("prodJob")
public class prodJob {

    public void prodJob1(){
        System.out.println("------"+new Date().getTime());
    }

}
