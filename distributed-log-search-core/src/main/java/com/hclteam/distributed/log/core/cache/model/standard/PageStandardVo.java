package com.hclteam.distributed.log.core.cache.model.standard;

import com.hclteam.distributed.log.core.cache.model.ServerData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 缓存中保存的页面数据对象1
 *
 * @param <T>
 */
public class PageStandardVo<K, T> implements Serializable {
    // 页面数据（保存主要信息即可，越少越好）
    private List<T> dataList;
    // 所有节点机器数据总条数
    private Long totalCount;
    // 查询条件
    private Map<String, Object> queryParamMap;
    // 当前内存分页的页码
    private Integer pageNo;
    // 当前内存每页条数
    private Integer pageSize;
    // 服务器信息
    private List<K> serverDataList;

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Map<String, Object> getQueryParamMap() {
        return queryParamMap;
    }

    public void setQueryParamMap(Map<String, Object> queryParamMap) {
        this.queryParamMap = queryParamMap;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<K> getServerDataList() {
        return serverDataList;
    }

    public void setServerDataList(List<K> serverDataList) {
        this.serverDataList = serverDataList;
    }
}
