package com.cjh.blog.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存
 * @author CJH
 * on 2019/3/14
 */

public class MapCache {

    /**
     * 默认最大缓存数 1024
     */
    private static final int DEFAULT_CACHES = 1024;

    private static final MapCache CACHE = new MapCache();

    public static MapCache single() {
        return CACHE;
    }

    private Map<String,CacheObject> cacheObjectMap;

    public MapCache() {
        this(DEFAULT_CACHES);
    }

    public MapCache(int cacheCount) {
        cacheObjectMap = new ConcurrentHashMap<>(cacheCount);
    }

    /**
     * 读取一个缓存
     *
     * @param key 缓存key
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        CacheObject cacheObject = cacheObjectMap.get(key);
        if (null != cacheObject) {
            long cur = System.currentTimeMillis() / 1000;
            //未过期直接返回
            if (cacheObject.getExpired() <= 0 || cacheObject.getExpired() > cur) {
                Object result = cacheObject.getValue();
                return (T) result;
            }
            //已过期直接删除
            if (cur > cacheObject.getExpired()) {
                cacheObjectMap.remove(key);
            }
        }
        return null;
    }

    /**
     * 读取一个hash类型缓存
     *
     * @param key   缓存key
     * @param field 缓存field
     * @param <T>
     * @return
     */
    public <T> T hget(String key, String field) {
        key = key + ":" + field;
        return this.get(key);
    }

    /**
     * 设置一个缓存 永不过期
     *
     * @param key   缓存key
     * @param value 缓存value
     */
    public void set(String key, Object value) {
        this.set(key, value, -1);
    }


    /**
     * 设置一个缓存并带过期时间
     *
     * @param key     缓存key
     * @param value   缓存value
     * @param expired 过期时间，单位为秒
     */
    public void set(String key, Object value, long expired) {
        // 计算定时
        expired = expired > 0 ? System.currentTimeMillis() / 1000 + expired : expired;
        //cachePool大于800时，强制清空缓存池，这个操作有些粗暴会导致误删问题，后期考虑用redis替代MapCache优化
        if (cacheObjectMap.size() > 800) {
            cacheObjectMap.clear();
        }
        CacheObject cacheObject = new CacheObject(key, value, expired);
        cacheObjectMap.put(key, cacheObject);
    }

    /**
     * 设置一个hash缓存 永不过期
     *
     * @param key   缓存key
     * @param field 缓存field
     * @param value 缓存value
     */
    public void hset(String key, String field, Object value) {
        this.hset(key, field, value, -1);
    }

    /**
     * 设置一个hash缓存并带过期时间
     *
     * @param key     缓存key
     * @param field   缓存field
     * @param value   缓存value
     * @param expired 过期时间，单位为秒
     */
    public void hset(String key, String field, Object value, long expired) {
        key = key + ":" + field;
        expired = expired > 0 ? System.currentTimeMillis() / 1000 + expired : expired;
        CacheObject cacheObject = new CacheObject(key, value, expired);
        cacheObjectMap.put(key, cacheObject);
    }

    /**
     * 根据key删除缓存
     *
     * @param key 缓存key
     */
    public void del(String key) {
        cacheObjectMap.remove(key);
    }

    /**
     * 根据key和field删除缓存
     *
     * @param key   缓存key
     * @param field 缓存field
     */
    public void hdel(String key, String field) {
        key = key + ":" + field;
        this.del(key);
    }

    /**
     * 清空缓存
     */
    public void clean() {
        cacheObjectMap.clear();
    }

    private static class CacheObject {

        private String key;

        private Object value;
        /**
         * 有效时间 单位 s
          */
        private long expired;

        public CacheObject(String key, Object value, long expired) {
            this.key = key;
            this.value = value;
            this.expired = expired;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public long getExpired() {
            return expired;
        }

    }
}
