package com.dong.p4;

import utils.SleepUtils;

/**
 * 4.3.5 Thread.join的使用
 */
public class Join {
    public static void main(String[] args) {
        Thread previousThread = Thread.currentThread();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Domino(previousThread), "thread " + i);
            thread.start();
            previousThread = thread;
        }

        SleepUtils.second(1);
        System.out.println(Thread.currentThread().getName() + " terminated!");
    }

    private static class Domino implements Runnable{
        private Thread thread;

        public Domino(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " terminated!");
        }
    }
}