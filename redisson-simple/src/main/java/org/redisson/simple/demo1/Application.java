package org.redisson.simple.demo1;

import org.redisson.Redisson;
import org.redisson.RedissonSemaphore;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * 第一个应用
 *
 * @author lv ning
 */
public class Application {

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://192.168.88.21:7001")
                .addNodeAddress("redis://192.168.88.21:7002")
                .addNodeAddress("redis://192.168.88.22:7003")
                .addNodeAddress("redis://192.168.88.22:7004")
                .addNodeAddress("redis://192.168.88.23:7005")
                .addNodeAddress("redis://192.168.88.23:7006");

        RedissonClient redisson = Redisson.create(config);

        // 非公平锁
        RLock lock = redisson.getLock("anyLock");
        lock.lock();
        lock.unlock();

        boolean tryLock = lock.tryLock(100, 10, TimeUnit.SECONDS);

        // 公平锁
        RLock fairLock = redisson.getFairLock("myFairLock");
        fairLock.lock();
        fairLock.unlock();

        // 多个锁 加锁
        RLock multiLock = redisson.getMultiLock(lock, fairLock);
        multiLock.lock();
        multiLock.unlock();

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("readWriteLock");

        // redis 信号量
        RSemaphore semaphore = redisson.getSemaphore("semaphore");
        semaphore.trySetPermits(3);// 设置只有三个客户端可以获取到锁

        int count = 3;
        // redis CountDownLatch
        RCountDownLatch countDownLatch = redisson.getCountDownLatch("countDownLatch");
        countDownLatch.trySetCount(count);// 三个任务

        for (int i = 0; i < count; i++) {
            // execute worker
            countDownLatch.countDown();
        }

        countDownLatch.await();


//        RMap<String, Object> map = redisson.getMap("anyMap");
//        map.put("foo", "bar");
//
//        map = redisson.getMap("anyMap");
//        System.out.println(map.get("foo"));
    }
}
