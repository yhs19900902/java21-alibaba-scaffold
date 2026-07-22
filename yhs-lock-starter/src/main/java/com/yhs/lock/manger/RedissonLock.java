package com.yhs.lock.manger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @author 07664-linwei
 * @version Id: RedissonLock.java, v 0.1 2022/4/24 17:47 lw Exp $
 */
@Data
@Slf4j
@AllArgsConstructor
public class RedissonLock {


    RedissonManger redissonManger;

    /**
     * 加锁
     *
     * @param lockName
     * @param expireTime
     * @return
     */
    public boolean lock(String lockName, long waitTime, TimeUnit timeUnit, long expireTime) {

        RLock lock = redissonManger.getRedisson().getLock(lockName);
        boolean lockFlag;
        try {
            lockFlag = lock.tryLock(waitTime, expireTime, timeUnit);
            if (lockFlag) {
                log.info("get Redisson distributed lock [success],lockName={}", lockName);
                return true;
            } else {
                log.warn("get Redisson distributed lock [fail],lockName={}", lockName);
                return false;
            }
        } catch (InterruptedException e) {
            log.error("get redisson distributed lock exception,lockName:{},{}", lockName, e);
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param lockName
     */
    public boolean unLock(String lockName) {
        RLock lock = redissonManger.getRedisson().getLock(lockName);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            return true;
        }
        log.warn("The current thread does not hold the lock, lockName:{}", lockName);
        return false;
    }
}
