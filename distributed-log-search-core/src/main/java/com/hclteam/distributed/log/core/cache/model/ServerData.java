package com.hclteam.distributed.log.core.cache.model;

import java.io.Serializable;

public class ServerData implements Serializable {
    // 总条数
    private long totalCount;
    // 总页数
    private long pageCount;
    // 每页条数
    private int pageSize;

    // 当前内存分页的页码
    private int pageNo;
    // 本次从服务器查询的数据个数，为0则没有参与查询
    private long dataListSize;
    // 服务器信息
    private ServerInfoData serverInfo;
    // 由于其他节点数据不足，本次查询在本节点上重新计算每页条数和分页，多查出来的数据， 比如：之前查第8页，每页20条，从140条数据开始，重新分页后，查第3页，每页60条，从120条开始查， 多查的20条就是差值。在查询时需要把起始点补足差值，才是接本次正常查询真正开始的数据
    private int diffIndex;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getDataListSize() {
        return dataListSize;
    }

    public void setDataListSize(long dataListSize) {
        this.dataListSize = dataListSize;
    }

    public ServerInfoData getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfoData serverInfo) {
        this.serverInfo = serverInfo;
    }

    public int getDiffIndex() {
        return diffIndex;
    }

    public void setDiffIndex(int diffIndex) {
        this.diffIndex = diffIndex;
    }
}
