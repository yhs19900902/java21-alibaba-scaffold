package com.yhs.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.yhs.base.constant.CommonConstant;
import lombok.AllArgsConstructor;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.json.Path;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.search.*;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;

import java.util.List;
import java.util.Map;


/**
 * jedis 操作 redis
 *
 * @author 07664-linwei
 * @version Id: RedisJsonService.java, v 0.1 2022/8/30 11:30 lw Exp $
 */
@AllArgsConstructor
public class RedisJsonService {
    private UnifiedJedis unifiedJedis;

    /**
     * 保存json 数据
     *
     * @param key key
     * @param obj data
     * @return Boolean 是否成功
     */
    public boolean jsonSet(String key, Object obj) {
        return parseBoolean(unifiedJedis.jsonSet(key, Path2.ROOT_PATH, obj));
    }

    /**
     * 保存json 并设置过期时间
     *
     * @param key
     * @param obj
     * @param seconds
     * @return
     */
    public long jsonSetExpire(String key, Object obj, long seconds) {
        boolean b = parseBoolean(unifiedJedis.jsonSet(key, Path2.ROOT_PATH, obj));
        return unifiedJedis.expire(key, seconds);
    }

    /**
     * 按层级 保存json 数据
     *
     * @param key     key
     * @param obj     data
     * @param pathKey 层级,按顺序存入
     * @return
     */
    public boolean jsonSet(String key, Object obj, Object... pathKey) {
        return parseBoolean(unifiedJedis.jsonSet(key, new Path2(
                CharSequenceUtil.join(CommonConstant.DOT, pathKey)), obj));
    }

    /**
     * @param key
     * @param obj
     * @return
     */
    public boolean jsonSetWithEscape(String key, Object obj) {
        return parseBoolean(unifiedJedis.jsonSetWithEscape(key, obj));
    }

    public boolean jsonSetWithEscape(String key, Object obj, Object... pathKey) {
        return parseBoolean(unifiedJedis.jsonSetWithEscape(key, new Path2(
                CharSequenceUtil.join(CommonConstant.DOT, pathKey)), obj));
    }

    /**
     * 判断key 是否存在
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return unifiedJedis.exists(key);
    }

    /**
     * 判断多个key 是否存在
     *
     * @param key
     * @return
     */
    public long exists(String... key) {
        return unifiedJedis.exists(key);
    }

    /**
     * 续期
     *
     * @param key     key
     * @param seconds 秒
     * @return
     */
    public long persistExpire(String key, long seconds) {
        return unifiedJedis.pexpire(key, seconds * 1000);
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return
     */
    public Object jsonGet(String key) {
        return unifiedJedis.jsonGet(key);
    }

    /**
     * 获取数据并转换
     *
     * @param key   key
     * @param paths 转换对象类
     * @return 转换对象
     */
    public Object jsonGet(String key, Path2... paths) {
        return unifiedJedis.jsonGet(key, paths);
    }

    /**
     * 指定json 层级获取
     *
     * @param key     key
     * @param pathKey 层级名称
     * @return
     */
    public Object jsonGet(String key, Object... pathKey) {
        return unifiedJedis.jsonGet(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 指定层级获取并转换
     *
     * @param key     key
     * @param clazz   转换类型
     * @param pathKey 层级
     * @return
     */
    public <T> T jsonGet(String key, Class<T> clazz, Object... pathKey) {
        return unifiedJedis.jsonGet(key, clazz, new Path(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 删除json
     *
     * @param key key
     * @return
     */
    public long jsonDel(String key) {
        return unifiedJedis.jsonDel(key);
    }

    /**
     * 指定删除某个层级数据
     *
     * @param key     key
     * @param pathKey 层级
     * @return
     */
    public long jsonDel(String key, Object... pathKey) {
        return unifiedJedis.jsonDel(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 清空数据
     *
     * @param key
     * @return
     */
    public long jsonClear(String key) {
        return unifiedJedis.jsonClear(key);
    }

    /**
     * 清空指定层级数据
     *
     * @param key     key
     * @param pathKey 层级
     * @return
     */
    public long jsonClear(String key, Object... pathKey) {
        return unifiedJedis.jsonClear(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    public List<Boolean> jsonToggle(String key, Object... pathKey) {
        return unifiedJedis.jsonToggle(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 判断数据类型
     *
     * @param key
     * @return
     */
    public Class<?> jsonType(String key) {
        return unifiedJedis.jsonType(key);
    }

    /**
     * 指定判断数据类型
     *
     * @param key
     * @param pathKey
     * @return
     */
    public List<Class<?>> jsonType(String key, Object... pathKey) {
        return unifiedJedis.jsonType(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 追加数据
     *
     * @param key
     * @param obj
     * @return
     */
    public long jsonStrAppend(String key, Object obj) {
        return unifiedJedis.jsonStrAppend(key, obj);
    }

    /**
     * 指定追加数据
     *
     * @param key
     * @param obj
     * @param pathKey
     * @return
     */
    public List<Long> jsonStrAppend(String key, Object obj, Object... pathKey) {
        return unifiedJedis.jsonStrAppend(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)), obj);
    }

    /**
     * redis存储字符串长度
     *
     * @param key
     * @return
     */
    public Long jsonStrLen(String key) {
        return unifiedJedis.jsonStrLen(key);
    }

    /**
     * 指定 redis存储字符串长度
     *
     * @param key
     * @return
     */
    public List<Long> jsonStrLen(String key, Object... pathKey) {
        return unifiedJedis.jsonStrLen(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * json String数组追加数据
     *
     * @param key
     * @param Path
     * @param objects
     * @return
     */
    public List<Long> jsonArrAppend(String key, Path2 Path, Object... objects) {
        return unifiedJedis.jsonArrAppend(key, Path, objects);
    }

    public List<Long> jsonArrAppendWithEscape(String key, Path2 path, Object... objects) {
        return unifiedJedis.jsonArrAppendWithEscape(key, path, objects);
    }

    /**
     * 指定数组层级添加数据
     *
     * @param key
     * @param scalar
     * @param pathKey
     * @return
     */
    public List<Long> jsonArrIndex(String key, Object scalar, Object... pathKey) {
        return unifiedJedis.jsonArrIndex(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)), scalar);
    }


    public List<Long> jsonArrIndexWithEscape(String key, Object scalar, Object... pathKey) {
        return unifiedJedis.jsonArrIndexWithEscape(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)), scalar);
    }

    /**
     * 多层数组嵌套指定下标添加数据
     *
     * @param key     key
     * @param path    路径层级
     * @param index   下标
     * @param objects 数据
     * @return
     */
    public List<Long> jsonArrInsert(String key, Path2 path, int index, Object... objects) {
        return unifiedJedis.jsonArrInsert(key, path, index, objects);
    }


    public List<Long> jsonArrInsertWithEscape(String key, Path2 path, int index, Object... objects) {
        return unifiedJedis.jsonArrInsertWithEscape(key, path, index, objects);
    }

    /**
     * 弹出数组第一个数据
     *
     * @param key
     * @return
     */
    public Object jsonArrPop(String key) {
        return unifiedJedis.jsonArrPop(key);
    }

    /**
     * 弹出数组第一个数据并转换类型
     *
     * @param key   key
     * @param clazz 类型
     * @return
     */
    public <T> T jsonArrPop(String key, Class<T> clazz) {
        return unifiedJedis.jsonArrPop(key, clazz);
    }

    /**
     * 指定层级弹出数组第一个数据
     *
     * @param key
     * @param pathKey 路径层级
     * @return
     */
    public List<Object> jsonArrPop(String key, Object... pathKey) {
        return unifiedJedis.jsonArrPop(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 指定层级弹出数组第一个数据并转换
     *
     * @param key
     * @param clazz   类型
     * @param pathKey 路径层级
     * @return
     */
    public <T> T jsonArrPop(String key, Class<T> clazz, Object... pathKey) {
        return unifiedJedis.jsonArrPop(key, clazz, new Path(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }

    /**
     * 指定层级弹出数组下标数据
     *
     * @param key
     * @param index   类型
     * @param pathKey 路径层级
     * @return
     */
    public List<Object> jsonArrPop(String key, int index, Object... pathKey) {
        return unifiedJedis.jsonArrPop(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)), index);
    }

    /**
     * 指定层级弹出数组下标数据并转换
     *
     * @param key
     * @param index   类型
     * @param clazz   类型
     * @param pathKey 路径层级
     * @return
     */
    public <T> T jsonArrPop(String key, Class<T> clazz, int index, Object... pathKey) {
        return unifiedJedis.jsonArrPop(key, clazz, new Path(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)), index);
    }

    /**
     * 数组长度
     *
     * @param key
     * @return
     */
    public Long jsonArrLen(String key) {
        return unifiedJedis.jsonArrLen(key);
    }

    /**
     * 指定层级数组长度
     *
     * @param key
     * @param pathKey 路径层级
     * @return
     */
    public List<Long> jsonArrLen(String key, Object... pathKey) {
        return unifiedJedis.jsonArrLen(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)));
    }


    /**
     * 获取指定层级指定范围数组
     *
     * @param key
     * @param start   开始下标
     * @param stop    结束下标
     * @param pathKey 路径层级
     * @return
     */
    public List<Long> jsonArrTrim(String key, int start, int stop, Object... pathKey) {
        return unifiedJedis.jsonArrTrim(key, new Path2(CharSequenceUtil.join(CommonConstant.DOT
                , pathKey)), start, stop);
    }

    /**
     * 创建 索引
     *
     * @param indexName 索引名称
     * @param prefix    前缀
     * @param schema    索引字段配置
     */
    public void createIndex(String indexName, String prefix, Schema schema) {
        IndexDefinition rule = new IndexDefinition(IndexDefinition.Type.HASH)
                .setPrefixes(prefix)
                .setLanguage(CommonConstant.CHINESE);
        unifiedJedis.ftCreate(indexName, IndexOptions.defaultOptions().setDefinition(rule),
                schema);
    }

    /**
     * 创建 索引
     *
     * @param indexName 索引描述
     * @param rule      索引定义
     * @param schema    索引字段配置
     */
    public void createIndex(String indexName, IndexDefinition rule, Schema schema) {
        unifiedJedis.ftCreate(indexName, IndexOptions.defaultOptions().setDefinition(rule),
                schema);
    }

    /**
     * 索引添加数据
     *
     * @param prefix 索引前缀
     * @param obj    数据
     * @return
     */
    public boolean addIndexData(String prefix, Object obj) {
        Map<String, Object> fields = BeanUtil.beanToMap(obj);
        unifiedJedis.hset(prefix, RediSearchUtil.toStringMap(fields));
        return true;
    }

    /**
     * 搜索
     *
     * @param indexName 索引名称
     * @param query     搜索对象
     * @return
     */
    public SearchResult search(String indexName, Query query) {
        query.setLanguage(CommonConstant.CHINESE);
        return unifiedJedis.ftSearch(indexName, query);
    }

    /**
     * 聚合搜索
     *
     * @param indexName          索引名称
     * @param aggregationBuilder 聚合搜索对象
     * @return
     */
    public AggregationResult aggregationSearch(String indexName, AggregationBuilder aggregationBuilder) {
        return unifiedJedis.ftAggregate(indexName, aggregationBuilder);
    }

    private boolean parseBoolean(String resultFlag) {
        return CommonConstant.OK.equalsIgnoreCase(resultFlag);
    }
}