package com.dong.p4;

import java.util.LinkedList;
import java.util.List;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

    private LinkedList<Job> jobs;

    private List<Worker> workers;

    public DefaultThreadPool(int num) {

    }

    @Override
    public void execute(Job job) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void addWorkers(int num) {

    }

    @Override
    public void removeWorker(int num) {

    }

    @Override
    public int getJobSize() {
        return 0;
    }

    /**
     * 工作者线程
     */
    class Worker implements Runnable {
        @Override
        public void run() {
            Job job = null;
            synchronized (jobs) {
                while (jobs.isEmpty()) {
                    try {
                        jobs.wait();
                    } catch (InterruptedException e) {
                        return ;
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
                }
                // 释放资源
                finally {

                }
                }
            }
        }
    }
