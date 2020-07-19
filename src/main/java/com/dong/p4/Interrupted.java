package com.dong.p4;


import utils.SleepUtils;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Interrupted {
    public static void main(String[] args) throws InterruptedException {
        Thread sleepThread = new Thread(new SleepRunner(), "sleep thread");
        Thread busyThread = new Thread(new BusyRunner(), "busy thread");

        // 设置为守护线程，这样所有非守护线程执行完毕后，虚拟机执行会自动退出
        sleepThread.setDaemon(true);
        busyThread.setDaemon(true);

        sleepThread.start();
        busyThread.start();

        TimeUnit.SECONDS.sleep(2);;

        // 设置中断标记
        sleepThread.interrupt();
        busyThread.interrupt();

        System.out.println("[1]sleep thread interrupted is " + sleepThread.isInterrupted());
        System.out.println("[1]busy thread interrupted is " + busyThread.isInterrupted());

        TimeUnit.SECONDS.sleep(2);;

        // 调用sleep会清除interrupt标记
        System.out.println("[2]sleep thread interrupted is " + sleepThread.isInterrupted());
        System.out.println("[2]busy thread interrupted is " + busyThread.isInterrupted());

        TimeUnit.SECONDS.sleep(2);;
    }

    static class SleepRunner implements Runnable {
        @Override
        public void run() {
            SleepUtils.second(10);
        }
    }

    static class BusyRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
            }
        }
    }
}