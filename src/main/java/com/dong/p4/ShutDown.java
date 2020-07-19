package com.dong.p4;

import utils.SleepUtils;

/**
 * 4.2.5 安全地中止线程
 */
public class ShutDown {
    public static void main(String[] args) {
        Thread runner = new Thread(new Runner(), "runner");
        runner.start();

        SleepUtils.second(2);
        runner.interrupt();

        SleepUtils.second(2);
    }

    private static class Runner implements Runnable {
        private long i;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println("Count i = " + i);
        }
    }
}