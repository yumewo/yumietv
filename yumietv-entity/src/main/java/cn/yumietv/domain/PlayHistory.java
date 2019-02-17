package cn.yumietv.domain;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: yumie
 * @Date: 2019/1/27 11:28
 * @Description:
 */
@Data
public class PlayHistory {
    //年月日
    private String nyr;
    //key是时分秒,value是链接
    private Map<String, String> sfmAndName = new HashMap<>();
}
