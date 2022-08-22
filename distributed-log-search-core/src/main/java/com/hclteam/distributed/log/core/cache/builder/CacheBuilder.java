package com.hclteam.distributed.log.core.cache.builder;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hclteam.distributed.log.core.cache.model.CacheLogListData;
import com.hclteam.distributed.log.core.cache.model.ServerLogData;
import com.hclteam.distributed.log.core.cache.model.j.PageData;

import java.util.concurrent.TimeUnit;

public class CacheBuilder {

    public static <K, V> Cache<K, V> builderCache(Long expireTime, TimeUnit timeUnit, Long maxSize) {
        Cache<K, V> cache = Caffeine.newBuilder()
                .weakValues()
                //5秒没有读写自动删除
                .expireAfterAccess(expireTime, timeUnit)
                //最大容量1024个，超过会自动清理空间
                .maximumSize(maxSize)
                .removalListener(((key, value, cause) -> {
                    //清理通知 key,value ==> 键值对   cause ==> 清理原因
                }))
                .build();
        return cache;
    }

    private CacheBuilder() {
    }

    public static void main(String[] args) {
        Cache<String, Cache<String, PageData<ServerLogData, CacheLogListData>>> objectObjectCache = CacheBuilder.builderCache(1L, TimeUnit.SECONDS, 1000L);
        System.out.println(objectObjectCache.stats());
    }
}
