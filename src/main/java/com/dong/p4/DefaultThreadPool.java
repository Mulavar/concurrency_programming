package com.dong.p4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    /**
     * 线程池最大线程数
     */
    private static final int MAX_WORK_NUMBERS = 10;

    /**
     * 线程池默认线程数
     */
    private static final int DEFAULT_WORK_NUMBERS = 5;

    /**
     * 线程池最小线程数
     */
    private static final int MIN_WORKER_NUMBER = 1;

    /**
     * 当前线程数
     */
    private int workerNum = DEFAULT_WORK_NUMBERS;

    /**
     * 线程编号
     */
    private AtomicLong threadNum = new AtomicLong();

    /**
     * 任务队列
     */
    private LinkedList<Job> jobs = new LinkedList<>();

    /**
     * 工作者队列，是个并发集合，每个工作者对应一个工作线程
     */
    private List<Worker> workers = Collections.synchronizedList(new ArrayList<>());

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORK_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        num = num > MAX_WORK_NUMBERS ? MAX_WORK_NUMBERS : num < MIN_WORKER_NUMBER ? MIN_WORKER_NUMBER : num;
        workerNum = num;
        initializeWorkers(num);
    }

    @Override
    public void execute(Job job) {
        // 生产-消费者模式
        // 生产者
        if (job != null) {
            synchronized (jobs) {
                // 将任务添加到任务队列
                jobs.add(job);
                // 通知工作线程
                jobs.notify();
                // 使用notifyAll会有更大开销
                // jobs.notifyAll();
            }
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            // 线程数量不能超过限定的最大值
            if (num + this.workerNum > MAX_WORK_NUMBERS) {
                num = MAX_WORK_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num > this.workerNum) {
                throw new IllegalArgumentException("beyond workNum");
            }

            int count = num;
            while (count > 0) {
                Worker worker = workers.get(count--);
                if (workers.remove(worker)) {
                    worker.shutdown();
                }
            }

            this.workerNum -= num;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    /**
     * 初始化工作线程
     */
    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    /**
     * 工作者线程
     */
    class Worker implements Runnable {
        // 标识是否工作
        private volatile boolean running = true;

        /**
         * 不断检查任务队列，如果有未被处理的任务则取来处理
         */
        @Override
        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    while (jobs.isEmpty()) {
                        try {
                            // 没任务时等待
                            jobs.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }

                if (job != null) {
                    try {
                        job.run();
                    }
                    // 捕获job运行过程中的异常
                    catch (Exception e) {
                        return;
                    }
                    // 释放资源
                    finally {

                    }
                }
            }
        }

        /**
         * 关闭该线程
         */
        public void shutdown() {
            this.running = false;
        }
    }
}
