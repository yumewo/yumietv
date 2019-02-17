package cn.yumie.service;

import cn.yumietv.domain.SearchCondition;
import cn.yumietv.domain.ShaiXuanCondition;
import cn.yumietv.domain.ShaiXuanTiaoJian;

public interface SearchService {
    SearchCondition search(SearchCondition searchCondition) throws Exception;
    ShaiXuanCondition shaixuan(ShaiXuanCondition shaiXuanCondition) throws Exception;
    ShaiXuanTiaoJian getTiaoJian();
}
