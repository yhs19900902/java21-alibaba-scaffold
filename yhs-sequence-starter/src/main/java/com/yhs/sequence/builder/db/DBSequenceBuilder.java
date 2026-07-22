package com.yhs.sequence.builder.db;

import cn.hutool.core.text.StrPool;
import com.yhs.sequence.number.DbSequenceGenerate;
import com.yhs.sequence.range.db.DbSequenceRangeManagement;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于DB 构建序列号生成构建
 *
 * @author 07664-linwei
 * @version Id: DBSequenceBuilder.java, v 0.1 2022/4/29 15:36 lw Exp $
 */
public class DBSequenceBuilder {

    /**
     * 数据源
     */
    private DataSource dataSource;


    /**
     * 表名
     */
    private String tableName = "sequence";

    /**
     * 可选：：1000
     */
    private int step = 1000;

    private boolean localStorage;

    /**
     * 序列号分配起始值[可选：默认：0]
     */
    private long stepStart = 0;

    private RedisStringCommands<String, String> redisStringCommands;

    public static DBSequenceBuilder create() {
        return new DBSequenceBuilder();
    }


    public DbSequenceGenerate build() {
        DbSequenceRangeManagement sequenceRangeManagement
                = new DbSequenceRangeManagement();
        sequenceRangeManagement.setDataSource(this.dataSource);
        sequenceRangeManagement.setTableName(this.tableName);
        sequenceRangeManagement.setStep(this.step);
        sequenceRangeManagement.setStepStart(stepStart);
        sequenceRangeManagement.init();
        DbSequenceGenerate defaultRangeSequence = new DbSequenceGenerate();
        defaultRangeSequence.setLocalStorage(this.localStorage);
        defaultRangeSequence.setStep(this.step);
        defaultRangeSequence.setSeqRangeManagement(sequenceRangeManagement);
        defaultRangeSequence.setRedisStringCommands(this.redisStringCommands);
        return defaultRangeSequence;
    }

    private RedisStringCommands<String, String> initRedisClient(RedisProperties redisProperties) {

        if (null != redisProperties.getCluster()) {
            List<RedisURI> redisURIList = new ArrayList<>();
            String[] addrss = redisProperties.getCluster().getNodes().toArray(new String[0]);
            //设置cluster节点的服务IP和端口
            for (String s : addrss) {
                String[] split = s.split(StrPool.COLON);
                redisURIList.add(RedisURI.Builder.redis(split[0], Integer.parseInt(split[1]))
                        .withPassword(redisProperties.getPassword().toCharArray())
                        .withDatabase(redisProperties.getDatabase())
                        .build());
            }
            RedisClusterClient redisClusterClient = RedisClusterClient.create(redisURIList);
            return redisClusterClient.connect().sync();
        } else {
            String[] split = redisProperties.getHost().split(StrPool.COLON);
            RedisURI redisUri = RedisURI.Builder.redis(split[0], Integer.parseInt(split[1]))
                    .withPassword(redisProperties.getPassword().toCharArray())
                    .withDatabase(redisProperties.getDatabase())
                    .build();
            RedisClient client = RedisClient.create(redisUri);
            return client.connect().sync();
        }
    }

    public DBSequenceBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }


    public DBSequenceBuilder tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DBSequenceBuilder redisProperties(RedisProperties redisProperties) {
        this.redisStringCommands = initRedisClient(redisProperties);
        return this;
    }


    public DBSequenceBuilder step(int step) {
        this.step = step;
        return this;
    }


    public DBSequenceBuilder stepStart(long stepStart) {
        this.stepStart = stepStart;
        return this;
    }

    public DBSequenceBuilder setLocalStorage(boolean localStorage) {
        this.localStorage = localStorage;
        return this;
    }
}
