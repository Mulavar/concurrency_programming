package com.dong.p4;


/**
 * 4.4.3 线程池技术及其示例
 * 注：Job是个泛型符号，表示需要被执行的任务
 */
public interface ThreadPool<Job extends  Runnable> {

    /**
     * 执行一个Job
     */
    void execute(Job job);

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 增加工作线程
     */
    void addWorkers(int num);

    /**
     * 移除工作线程
     */
    void removeWorker(int num);

    /**
     * 获取Job数量
     */
    int getJobSize();
}
