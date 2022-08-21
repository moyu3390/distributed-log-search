package com.hclteam.distributed.log.core.cache.model.j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PageVo<T> implements Serializable {
    // 页面数据（保存主要信息即可，越少越好）
    private List<T> dataList;
    // 所有节点机器数据总条数
    private long totalCount;
    // 当前内存分页的页码
    private int pageNo;
    // 当前内存每页条数
    private int pageSize;

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
