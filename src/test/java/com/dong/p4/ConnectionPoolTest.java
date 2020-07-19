package com.dong.p4;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接池测试
 */
public class ConnectionPoolTest {
    /**
     * 初始化一个拥有10个连接的连接池
     */
    static ConnectionPool pool = new ConnectionPool(10);

    /**
     * 确保所有ConnectionRunner同时开始执行
     */
    static CountDownLatch start = new CountDownLatch(1);

    /**
     * 确保main线程在所有ConnectionRunner线程执行结束后结束
     */
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        int count = 10;
        int threadCount = 20;
        end = new CountDownLatch(threadCount);

        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            // 重用got和notGot，统计出的结果是所有线程加起来的结果
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }

        // 实际启动线程
        start.countDown();
        // 等待其他线程运行完
        end.await();

        System.out.println("total invoke: " + threadCount * count);
        System.out.println("got connection: " + got);
        System.out.println("not got connection: " + notGot);
    }

    static class ConnectionRunner implements Runnable {
        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {

            }

            while (count > 0) {
                try {
                    // 获取连接
                    Connection connection = pool.fetchConnection(100);
                    // 分别统计获取到连接和没获取到连接的数量
                    if (connection != null) {
                        try {
                            // 执行、提交
                            connection.createStatement();
                            connection.commit();
                        } finally {
                            // 释放连接
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {

                } finally {
                    count--;
                }
            }

            end.countDown();
        }
    }
}
