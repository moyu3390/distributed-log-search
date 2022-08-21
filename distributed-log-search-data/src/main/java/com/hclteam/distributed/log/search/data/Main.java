package com.hclteam.distributed.log.search.data;

import com.alibaba.fastjson.JSONObject;
import com.hclteam.distributed.log.core.cache.model.ServerInfoData;
import com.hclteam.distributed.log.core.cache.model.j.PageVo;
import com.hclteam.distributed.log.core.service.DistributedSearchDataService;
import com.hclteam.distributed.log.search.data.collect.DataCollectLocal;
import com.hclteam.distributed.log.search.data.generate.DataGenerator;

import java.util.List;
import java.util.Map;

public class Main {
    static int pageNo =     33;

    static int pageSize = 25;

    static List<ServerInfoData> serverInfoDataList = DataGenerator.genServerInfoDataList();

    public static void main(String[] args) {
        DistributedSearchDataService service = new DistributedSearchDataService(new DataCollectLocal());
        PageVo<Map<String, Object>> mapPageVo = service.searchData(pageNo, pageSize, "", "111.111.111.111", serverInfoDataList);

        System.out.println(JSONObject.toJSONString(mapPageVo));

    }
}
