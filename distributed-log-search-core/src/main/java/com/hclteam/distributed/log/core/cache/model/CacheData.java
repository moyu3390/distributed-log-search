package com.hclteam.distributed.log.core.cache.model;

import java.io.Serializable;

/**
 * 内存中保存的数据条数信息
 */
public class CacheData implements Serializable {

    // 日志id
    private Long dataId;
    // 创建时间
    private Long createTime;
    // 服务器标识，表示该条数据在哪台服务器上
    private String serverIp;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
