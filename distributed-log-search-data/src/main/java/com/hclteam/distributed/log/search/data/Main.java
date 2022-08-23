package com.hclteam.distributed.log.search.data;

import com.alibaba.fastjson.JSONObject;
import com.hclteam.distributed.log.core.cache.model.ServerInfoData;
import com.hclteam.distributed.log.core.cache.model.j.PageVo;
import com.hclteam.distributed.log.core.service.DistributedSearchDataService;
import com.hclteam.distributed.log.search.data.collect.DataCollectLocal;
import com.hclteam.distributed.log.search.data.generate.DataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static int pageNo = 37;

    static int pageSize = 10;

    static List<ServerInfoData> serverInfoDataList = DataGenerator.genServerInfoDataList();

    public static void main(String[] args) {
        DistributedSearchDataService service = new DistributedSearchDataService(new DataCollectLocal());
        PageVo<Map<String, Object>> mapPageVo = service.searchData(pageNo, pageSize, "", "111.111.111.111", serverInfoDataList);
        System.out.println(JSONObject.toJSONString(mapPageVo));






//        List<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 45; i++) {
//            PageVo<Map<String, Object>> mapPageVo = service.searchData((i + 1), pageSize, "", "111.111.111.111", serverInfoDataList);
//            System.out.println("第" + (i + 1) + "页 :");
//            mapPageVo.getDataList().forEach(m -> {
//                String id = m.get("id").toString();
//                System.out.print("\t" + id);
//                list.add(Integer.valueOf(id));
//
//            });
//            System.out.println();
//            if(i==36) break;
//        }
//
//        List<Integer> integers = checkRepeat(list);
//        System.out.println("重复元素：");
//        integers.stream().forEach(i -> {
//            System.out.print("\t" + i);
//        });




    }


    public static <T> List<T> checkRepeat(List<T> list) {
        return list.stream()
                .collect(Collectors.toMap(e -> e, e -> 1, (a, b) -> a + b)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
                .entrySet().stream() // Set<Entry>转换为Stream<Entry>
                .filter(entry -> entry.getValue() > 1) // 过滤出元素出现次数大于 1 的 entry
                .map(entry -> entry.getKey()) // 获得 entry 的键（重复元素）对应的 Stream
                .collect(Collectors.toList()); // 转化为 List
    }
}
