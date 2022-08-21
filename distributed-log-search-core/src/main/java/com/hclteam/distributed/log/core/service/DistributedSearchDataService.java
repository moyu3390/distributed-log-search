package com.hclteam.distributed.log.core.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.hclteam.distributed.log.core.cache.builder.CacheBuilder;
import com.hclteam.distributed.log.core.cache.model.CacheData;
import com.hclteam.distributed.log.core.cache.model.ServerData;
import com.hclteam.distributed.log.core.cache.model.ServerInfoData;
import com.hclteam.distributed.log.core.cache.model.j.PageData;
import com.hclteam.distributed.log.core.cache.model.j.PageVo;
import com.hclteam.distributed.log.core.data.DataCollector;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DistributedSearchDataService {
    /**
     * 内存中每页条数,大于等于页面实际显示条数
     */
    private int cache_page_size = 50;

    private DataCollector dataCollector;

    public DistributedSearchDataService(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
    }

    public PageVo<Map<String, Object>> searchData(int pageNo, int pageSize, String queryParam, String clientIp, List<ServerInfoData> serverList) {
        String dateType = "test";
        if (CollectionUtils.isEmpty(serverList)) {
            return null;
        }
        serverList.sort(Comparator.comparing(ServerInfoData::getId));
        String ips = serverList.stream().map(s -> s.getIp()).collect(Collectors.joining("_"));
        String key = queryParam + dateType + clientIp + ips;

        PageData<ServerData, CacheData> cacheData = getDataFromCache(pageNo, pageSize, clientIp, key, serverList);
        PageVo<Map<String, Object>> pageData = null;
        if (Objects.isNull(cacheData)) {
            // 查询实际数据
            // 页面数据的检索条数范围
            long pageMin = (pageNo - 1) * pageSize;
            long pageMax = pageNo * pageSize - 1;
            // 把页面数据转换成内存页数据，再进行查找
            // 页面数据范围在缓存中的页数
            int cachePageSize = cache_page_size * serverList.size();
            long pageNoMinInCache = pageMin / cachePageSize + 1;
            long pageNoMaxInCache = pageMax / cachePageSize + 1;
            if (pageNoMaxInCache > pageNoMinInCache) {
                cache_page_size = pageSize;
                cachePageSize = cache_page_size * serverList.size();
                pageNoMinInCache = pageMin / cachePageSize + 1;
                pageNoMaxInCache = pageMax / cachePageSize + 1;
            }
            int cachePageNo = (int) pageNoMaxInCache;


            PageData<ServerData, CacheData> cacheDataTemp = serverList.stream().map(s ->
                            dataCollector.getServerData(cachePageNo, cache_page_size, queryParam, clientIp, s.getIp(), 1234, 0)
                    ).filter(p -> Objects.nonNull(p))
                    .reduce(this::collectCacheDataAll).get();

            long totalCount = cacheDataTemp.getPageVo().getTotalCount();
            if (pageMin >= totalCount) {
                throw new RuntimeException("页数太大了，没数据了");
            }

            int dataSize = cacheDataTemp.getPageVo().getDataList().size();
            int currentPageNo = cachePageNo;
            int currentCachePageSize = cache_page_size;
            // 判断查询结果是否足额
            if (dataSize < cachePageSize) {
                // 不足额，动态更换节点，去除不参与查询的节点，内存每页数据相应增大，同时计算如何查询能满足页面查询的数据
                // 去除不参与查询的节点

                // 计算要满足页面数据，现有节点需要如何分页进行检索，即调大内存每页条数
                // 重新查询。

                // 足额的节点
                List<ServerData> hasNextDataServer = cacheDataTemp.getServerDataList().stream().filter(s -> (s.getDataListSize() == cache_page_size && s.getPageNo() < s.getPageCount())).collect(Collectors.toList());
                // 从现有查询的结果中删除数据足额的节点数据，重新查询；
                List<CacheData> dataList = cacheDataTemp.getPageVo().getDataList();
                hasNextDataServer.stream().forEach(s -> {
                    dataList.removeIf(d -> d.getServerIp().equals(s.getServerInfo().getIp()));
                });
                // 计算相差数据
                int diffDatas = cachePageSize - dataList.size();
                // 计算相差页数
                int needPage = diffDatas % cache_page_size == 0 ? diffDatas / cache_page_size : diffDatas / cache_page_size + 1;
                // 如果剩余节点按现有每页条数查到的数据还不足分页数据，则改内存每页条数大小为相差的数据条数，向上取整。
                int cache_new_page_size_temp = cache_page_size;
                if (hasNextDataServer.size() > 0) {
                    if (needPage >= hasNextDataServer.size()) {
                        int mdi = needPage % hasNextDataServer.size() == 0 ? needPage / hasNextDataServer.size() : needPage / hasNextDataServer.size() + 1;
                        cache_new_page_size_temp = cache_new_page_size_temp * mdi;
                    }
                    int cache_new_page_size = cache_new_page_size_temp;
                    // 重新计算当前页码,服务器起始查询数据条数
                    int dataStart = (currentPageNo - 1) * currentCachePageSize;
                    int newPageNo = dataStart / (cache_new_page_size * hasNextDataServer.size()) + 1;
                    // 计算重新分页后，多查出来的数据

                    hasNextDataServer.stream().forEach(h -> {
                        // 重新分页后，数据起始位置
                        int dataMinNew = (newPageNo - 1) * cache_new_page_size;
                        int dataMin = dataStart;
                        h.setDiffIndex(dataMin - dataMinNew);
                        h.setPageNo(newPageNo);
                        h.setPageSize(cache_new_page_size);
                    });

                    PageData<ServerData, CacheData> dataTemp = hasNextDataServer.stream().map(h -> dataCollector.getServerData(newPageNo, cache_new_page_size, queryParam, clientIp, h.getServerInfo().getIp(), 1234, h.getDiffIndex()))
                            .filter(p -> Objects.nonNull(p))
                            .reduce(this::collectCacheDataAll).get();

                    dataTemp.getPageVo().getDataList().addAll(dataList);
                    dataTemp.getPageVo().getDataList().sort(new Comparator<CacheData>() {
                        @Override
                        public int compare(CacheData o1, CacheData o2) {
                            return o2.getCreateTime().compareTo(o1.getCreateTime());
                        }
                    });

                    // 计算由于多节点计算，查询出比当前内存每页数值多出的条数，直接删除最早的记录
                    int num = dataTemp.getPageVo().getDataList().size();
                    int diffNum = num - cachePageSize;
                    List<CacheData> dataList1 = dataTemp.getPageVo().getDataList();

                    if (totalCount - 1 <= pageMax) {
                        if (diffNum > 0) {
                            dataList1 = dataList1.subList(0, num - diffNum);
                        }
                    } else {
                        if (diffNum > 0) {
                            dataList1 = dataList1.subList(diffNum, num);
                        }
                    }
                    cacheDataTemp.getPageVo().setDataList(dataList1);
                }
            }

            Cache<String, PageData<ServerData, CacheData>> clientCache = CACHES.getIfPresent(clientIp);
            if (Objects.nonNull(cacheDataTemp)) {
                if (Objects.isNull(clientCache)) {
                    clientCache = getNewQueryDataCache();
                    clientCache.put(key, cacheDataTemp);
                    CACHES.put(clientIp, clientCache);
                } else {
                    clientCache.put(key, cacheDataTemp);
                }
                PageVo<CacheData> pageVo = cacheDataTemp.getPageVo();
                // 分页 按id列表去各个节点查询缓存当前页数据
                // 确定页面查询数据
                PageVo<CacheData> pagedata = getPageIdList(pageNo, pageSize, pageVo, cacheDataTemp.getServerDataList().size());
                return getDataByIds(pagedata);
            }

        }
        pageData = new PageVo<>();
        pageData.setPageNo(pageData.getPageNo());
        pageData.setPageSize(pageData.getPageSize());
        pageData.setTotalCount(0L);
        pageData.setDataList(new ArrayList<>());
        return pageData;

    }


    public PageVo<Map<String, Object>> getDataByIds(PageVo<CacheData> pageData) {
        List<CacheData> dataList = pageData.getDataList();

        Map<String, List<CacheData>> collect = dataList.stream().collect(Collectors.groupingBy(CacheData::getServerIp));

        List<Map<String, Object>> mapList = collect.keySet().stream().map(m -> dataCollector.getDataByIds(collect.get(m), m)).filter(l -> !CollectionUtils.isEmpty(l)).reduce(this::collectDatas).get();

        PageVo<Map<String, Object>> data = new PageVo<>();
        data.setPageNo(pageData.getPageNo());
        data.setPageSize(pageData.getPageSize());
        data.setTotalCount(pageData.getTotalCount());
        data.setDataList(mapList);


        return data;

    }

    public List<Map<String, Object>> collectDatas(List<Map<String, Object>> first, List<Map<String, Object>> second) {
        List<Map<String, Object>> list = new ArrayList<>();
        list.addAll(first);
        list.addAll(second);

        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Long l1 = Long.valueOf(o1.get("createTime").toString());
                Long l2 = Long.valueOf(o2.get("createTime").toString());
                return l2.compareTo(l1);// 降序
            }
        });
        return list;
    }


    public PageVo<CacheData> getPageIdList(int pageNo, int pageSize, PageVo<CacheData> pageVo, long serverCount) {
        // 页面数据的检索条数范围
        long pageMin = (pageNo - 1) * pageSize;
        long pageMax = pageNo * pageSize - 1;

        // 当前缓存的页数
        int cachePageNo = pageVo.getPageNo();
        int cachePageSizeTemp = pageVo.getPageSize();
        // 内存中存在的数据的范围
        long cacheMin = (cachePageNo - 1) * cachePageSizeTemp * serverCount;
        long cacheMax = (cachePageNo * cachePageSizeTemp * serverCount) - 1;

        int indexMin = 0;
        // 数据实际长度
        int indexMax = pageVo.getDataList().size() - 1;
        // 实际最大下标
//        long tt = cacheMin + indexMax;
//        if (cacheMax > tt) {
//            cacheMax = tt;
//        }
//        if (pageMax > cacheMax) {
//            pageMax = cacheMax;
//        }

        int start = (int) (pageMin - cacheMin);
        if (start < indexMin) {
            start = indexMin;
        }
        if (start > indexMax) {
            start = 0;
        }
        int end = (int) (pageMax - cacheMin);
        if (end > indexMax) {
            end = indexMax;
        }

        List<CacheData> dataList = pageVo.getDataList();
        List<CacheData> cacheData = dataList.subList(start, end + 1);
        PageVo<CacheData> pageVoData = new PageVo<>();
        pageVoData.setPageNo(pageNo);
        pageVoData.setPageSize(pageSize);
        pageVoData.setTotalCount(pageVo.getTotalCount());
        pageVoData.setDataList(cacheData);

        return pageVoData;
    }


    public PageData<ServerData, CacheData> collectCacheDataAll(PageData<ServerData, CacheData> first, PageData<ServerData, CacheData> second) {
        PageData<ServerData, CacheData> page = first;

        PageVo<CacheData> pageVo = page.getPageVo();
        PageVo<CacheData> pageVo1 = second.getPageVo();

        if (!CollectionUtils.isEmpty(pageVo1.getDataList())) {
            pageVo.getDataList().addAll(pageVo1.getDataList());

        }
        pageVo.setTotalCount(pageVo.getTotalCount() + pageVo1.getTotalCount());


        if (!CollectionUtils.isEmpty(second.getServerDataList())) {
            page.getServerDataList().addAll(second.getServerDataList());
        }
        pageVo.getDataList().sort(new Comparator<CacheData>() {
            @Override
            public int compare(CacheData o1, CacheData o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });

        page.setPageVo(pageVo);

        return page;

    }


    public PageData<ServerData, CacheData> getDataFromCache(int pageNo, int pageSize, String clientIp, String key, List<ServerInfoData> serverList) {
        Cache<String, PageData<ServerData, CacheData>> clientCache = CACHES.getIfPresent(clientIp);
        PageData<ServerData, CacheData> queryPageData = null;

        if (Objects.isNull(clientCache)) {
            return queryPageData;
        }

        PageData<ServerData, CacheData> queryCache = clientCache.getIfPresent(key);
        if (Objects.isNull(queryCache)) {
            return queryPageData;
        }

        PageVo<CacheData> pageVo = queryCache.getPageVo();

        // 计算页面分页数据在内存中的数据的范围
        // 页面数据的检索条数范围
        long pageMin = (pageNo - 1) * pageSize;
        long pageMax = pageNo * pageSize - 1;


        // 当前缓存的页数
        int cachePageNo = pageVo.getPageNo();
        int cachePageSizeTemp = pageVo.getPageSize();
        // 内存中存在的数据的范围
        long cacheMin = (cachePageNo - 1) * cachePageSizeTemp;
        long cacheMax = (cachePageNo * cachePageSizeTemp) - 1;
        // 命中缓存
        if (pageMin >= cacheMin && pageMax <= cacheMax) {
            queryPageData = queryCache;
        }
        if (pageSize > cache_page_size) {
            cache_page_size = pageSize;
        }
        return queryPageData;
    }


    public static Cache<String, PageData<ServerData, CacheData>> getNewQueryDataCache() {

        return CacheBuilder.builderCache(2L, TimeUnit.MINUTES, 100000L);
    }

    private static final Cache<String, Cache<String, PageData<ServerData, CacheData>>> CACHES = CacheBuilder.builderCache(3L, TimeUnit.MINUTES, 1000L);
}
