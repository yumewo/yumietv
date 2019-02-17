package cn.yumietv.domain;

import lombok.Data;

import java.util.List;

/**
 * @Auther: yumie
 * @Date: 2019/1/20 09:37
 * @Description:
 */
@Data
public class ShaiXuanTiaoJian {
    private List<String> categories;
    private List<String> zimu24;
    private List<String> nianfen;
    private List<String> country;
}
