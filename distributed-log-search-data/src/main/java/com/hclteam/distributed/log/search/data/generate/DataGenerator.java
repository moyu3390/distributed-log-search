package com.hclteam.distributed.log.search.data.generate;

import com.alibaba.fastjson.JSONObject;
import com.hclteam.distributed.log.core.cache.model.CacheData;
import com.hclteam.distributed.log.core.cache.model.ServerInfoData;

import java.util.*;

public class DataGenerator {

    public static Map<String, List<Map<String, Object>>> genData() {
        DataNum dataNum = new DataNum("127.0.0.1", 100);
        DataNum dataNum2 = new DataNum("localhost", 150);
        DataNum dataNum3 = new DataNum("110.110.110.110", 200);
//        DataNum dataNum4 = new DataNum("220.220.220.220", 45);
        List<DataNum> dataNums = new ArrayList<>();
        dataNums.add(dataNum);
        dataNums.add(dataNum2);
//        dataNums.add(dataNum4);
        dataNums.add(dataNum3);

        Map<String, List<LogEntity>> data = getData(dataNums);
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        data.keySet().stream().forEach(m -> {
            List<LogEntity> list = data.get(m);
            List<Map<String, Object>> dataMapList = new ArrayList<>();
            list.stream().forEach(l -> {
                String json = JSONObject.toJSONString(l);
                dataMapList.add(JSONObject.parseObject(json).getInnerMap());
            });
            map.put(m, dataMapList);
        });
        return map;

    }

    private static Map<String, List<LogEntity>> getData(List<DataNum> dataNums) {
        int id = 0;
        Map<String, List<LogEntity>> data = new HashMap<>();
        for (DataNum d : dataNums) {
            List<LogEntity> l = new ArrayList<>();
            for (int i = 0; i < d.getCount(); i++) {
                LogEntity log = new LogEntity();
                log.setId(i);
                log.setLogContent(id + "============" + i);
                log.setServerIp(d.getIp());
//                try {
//                    Thread.sleep(10L);
//                } catch (InterruptedException e) {
//                }
                log.setCreateTime(System.currentTimeMillis());
                ++id;
                l.add(log);
            }
//            l.sort(new Comparator<LogEntity>() {
//                @Override
//                public int compare(LogEntity o1, LogEntity o2) {
//                    return compare(o2.getId(),o1.getId());
//                }
//
//                public int compare(long x, long y) {
//                    return (x < y) ? -1 : ((x == y) ? 0 : 1);
//                }
//            });
            data.put(d.getIp(), l);
        }
        return data;
    }


    public static Map<String,ServerInfoData> genServerInfoData() {
        ServerInfoData dataNum = new ServerInfoData(1,"127.0.0.1");
        ServerInfoData dataNum2 = new ServerInfoData(3,"localhost");
//        ServerInfoData dataNum4 = new ServerInfoData(4,"220.220.220.220");
        ServerInfoData dataNum3 = new ServerInfoData(2,"110.110.110.110");
        Map<String,ServerInfoData> map = new HashMap<>();
        map.put("127.0.0.1",dataNum);
        map.put("localhost",dataNum2);
//        map.put("220.220.220.220",dataNum4);
        map.put("110.110.110.110",dataNum3);

        return map;

    }

    public static List<ServerInfoData> genServerInfoDataList() {
        Map<String, ServerInfoData> serverInfoDataMap = DataGenerator.genServerInfoData();
        List<ServerInfoData> list = new ArrayList<>();
        serverInfoDataMap.keySet().stream().forEach(m->{
            list.add(serverInfoDataMap.get(m));
        });
        return list;

    }


}

class DataNum {
    private String ip;

    private int count;

    public DataNum(String ip, int count) {
        this.ip = ip;
        this.count = count;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
