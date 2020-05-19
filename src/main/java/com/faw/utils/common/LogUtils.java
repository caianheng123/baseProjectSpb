package com.faw.utils.common;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;


/**
 * @author liushengbin
 * @version 1.0
 * @description 日志输出处理
 * @email liushengbin7@gmail.com
 * @date 2019/2/20 9:53 AM
 */
public class LogUtils {

    /**
     * 把日志输出的内容格式化，防止攻击者可以利用这一行为来伪造日志条目或将恶意内容注入日志
     * @param log
     * @return
     */
    public static String convertValidLog(String log){
        List<String> list = new ArrayList<String>();
        list.add("%0d");
        list.add("\r");
        list.add("%0a");
        list.add("\n");

        // 将日志内容归一化
        String encode = Normalizer.normalize(log, Normalizer.Form.NFKC);
        for(String toReplaceStr : list){
            encode = encode.replace(toReplaceStr, "");
        }
        return encode;
    }

}
