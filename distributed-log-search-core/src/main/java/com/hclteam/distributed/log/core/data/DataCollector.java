package com.hclteam.distributed.log.core.data;

import com.hclteam.distributed.log.core.cache.model.CacheData;
import com.hclteam.distributed.log.core.cache.model.ServerData;
import com.hclteam.distributed.log.core.cache.model.j.PageData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface DataCollector extends Serializable {

//    ServerData getServerDataTotalCount(int pageNo, int pageSize, String queryParam, String clientIp, String host, int port);

    PageData<ServerData, CacheData> getServerData(int pageNo, int pageSize, String queryParam, String clientIp, String host, int port,int diffIndex);

    List<Map<String, Object>> getDataByIds(List<CacheData> list,String serverIp);

}
