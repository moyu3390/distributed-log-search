package com.hclteam.distributed.log.core.cache.model;

import java.io.Serializable;

public class ServerInfoData implements Serializable {

    private long id;

    private String ip;

    public ServerInfoData(long id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
