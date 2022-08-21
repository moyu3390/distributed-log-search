package com.hclteam.distributed.log.search.data.generate;

import java.util.List;
import java.util.Map;

public class ServerData {


    public Map<String, List<Map<String, Object>>> dataList;

    public Map<String, List<Map<String, Object>>> getDataList() {
        return dataList;
    }

    public void setDataList(Map<String, List<Map<String, Object>>> dataList) {
        this.dataList = dataList;
    }
}
