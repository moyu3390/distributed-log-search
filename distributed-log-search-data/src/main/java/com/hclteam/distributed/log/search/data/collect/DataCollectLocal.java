package com.hclteam.distributed.log.search.data.collect;


import com.hclteam.distributed.log.core.cache.model.CacheData;
import com.hclteam.distributed.log.core.cache.model.ServerData;
import com.hclteam.distributed.log.core.cache.model.ServerInfoData;
import com.hclteam.distributed.log.core.cache.model.j.PageData;
import com.hclteam.distributed.log.core.cache.model.j.PageVo;
import com.hclteam.distributed.log.core.data.DataCollector;
import com.hclteam.distributed.log.search.data.generate.DataGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class DataCollectLocal implements DataCollector {
    private static Map<String, List<Map<String, Object>>> dataMap = DataGenerator.genData();

    public static Map<String, ServerInfoData> serverInfoDataMap = DataGenerator.genServerInfoData();

    @Override
    public PageData<ServerData, CacheData> getServerData(int pageNo, int pageSize, String queryParam, String clientIp, String host, int port, int diffIndex) {

        List<Map<String, Object>> maps = dataMap.get(host);
        long totalCount = maps.size();
        long totalPage = (totalCount + pageSize - 1) / pageSize;
        long surplus = totalCount - (pageNo*pageSize);
        surplus = surplus<0?0:surplus;
        if (totalPage < pageNo) {
            // 不返回数据，返回服务器信息
            PageVo<CacheData> pageVo = new PageVo<>();
            pageVo.setPageSize(pageSize);
            pageVo.setPageNo(pageNo);
            pageVo.setDataList(new ArrayList<>());
            pageVo.setTotalCount(totalCount);

            List<ServerData> serverDataList = new ArrayList<>();
            ServerData serverData = new ServerData();
            serverData.setPageSize(pageSize);
            serverData.setPageCount(totalPage);
            serverData.setTotalCount(totalCount);
            serverData.setDataListSize(0L);
            serverData.setPageNo(pageNo);
            serverData.setSurplus(surplus);
            serverData.setServerInfo(serverInfoDataMap.get(host));
            serverDataList.add(serverData);
            PageData<ServerData, CacheData> pageData = new PageData<>();
            pageData.setPageVo(pageVo);
            pageData.setServerDataList(serverDataList);

            return pageData;
        }
        int maxIndex = maps.size() - 1;

        int start = (pageNo - 1) * pageSize;


        int end = pageNo * pageSize - 1;
        // 如果差值大于0，则表示新分页后的开始元素和页面要查找的元素不匹配，需要校正，即把新分页的起始点和终止点 向右移动差值大小
        if (diffIndex > 0) {
            start = start + diffIndex;
            end = end + diffIndex;
        }
        if (end > maxIndex) {
            end = maxIndex;
        }
        List<Map<String, Object>> pageMap = maps.subList(start, end + 1);
        List<CacheData> dataList = new ArrayList<>();
        for (int i = 0; i < pageMap.size(); i++) {
            Map<String, Object> m = pageMap.get(i);
            CacheData data = new CacheData();
            data.setDataId(Long.valueOf(m.get("id").toString()));
            data.setServerIp(host);
            data.setCreateTime(Long.valueOf(m.get("createTime").toString()));
            dataList.add(data);

        }
        PageVo<CacheData> pageVo = new PageVo<>();
        pageVo.setPageSize(pageSize);
        pageVo.setPageNo(pageNo);
        pageVo.setDataList(dataList);
        pageVo.setTotalCount(totalCount);

        List<ServerData> serverDataList = new ArrayList<>();
        ServerData serverData = new ServerData();
        serverData.setPageSize(pageSize);
        serverData.setPageCount(totalPage);
        serverData.setTotalCount(totalCount);
        serverData.setPageNo(pageNo);
        serverData.setSurplus(surplus);
        serverData.setDataListSize(dataList.size());
        serverData.setServerInfo(serverInfoDataMap.get(host));
        serverDataList.add(serverData);
        PageData<ServerData, CacheData> pageData = new PageData<>();
        pageData.setPageVo(pageVo);
        pageData.setServerDataList(serverDataList);


        return pageData;
    }

    @Override
    public List<Map<String, Object>> getDataByIds(List<CacheData> list, String serverIp) {
        List<Map<String, Object>> maps = dataMap.get(serverIp);
        List<Long> collect = list.stream().map(l -> l.getDataId()).collect(Collectors.toList());
        List<Map<String, Object>> data = new ArrayList<>();
        maps.stream().forEach(m -> {
            long id = Long.valueOf(m.get("id").toString());
            if (collect.contains(id)) {
                data.add(m);
            }
        });
        data.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Long l1 = Long.valueOf(o1.get("createTime").toString());
                Long l2 = Long.valueOf(o2.get("createTime").toString());
                return l2.compareTo(l1);// 降序
            }
        });
        return data;
    }
}
