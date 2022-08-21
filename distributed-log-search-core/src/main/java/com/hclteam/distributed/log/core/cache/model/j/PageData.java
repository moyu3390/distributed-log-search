package com.hclteam.distributed.log.core.cache.model.j;

import com.hclteam.distributed.log.core.cache.model.CacheData;
import com.hclteam.distributed.log.core.cache.model.ServerData;

import java.io.Serializable;
import java.util.List;

public class PageData<K extends ServerData,T extends CacheData> implements Serializable {

    private PageVo<T> pageVo;

    private List<K> serverDataList;

    public PageVo<T> getPageVo() {
        return pageVo;
    }

    public void setPageVo(PageVo<T> pageVo) {
        this.pageVo = pageVo;
    }

    public List<K> getServerDataList() {
        return serverDataList;
    }

    public void setServerDataList(List<K> serverDataList) {
        this.serverDataList = serverDataList;
    }
}
