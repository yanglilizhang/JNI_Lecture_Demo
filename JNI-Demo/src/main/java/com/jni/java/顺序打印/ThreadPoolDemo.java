package com.jni.java.顺序打印;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 通过SingleThreadExecutor让线程按顺序执行
 * 1.单线程化线程池(newSingleThreadExecutor);
 * 2.可控最大并发数线程池(newFixedThreadPool);
 * 3.可回收缓存线程池(newCachedThreadPool);
 * 4.支持定时与周期性任务的线程池(newScheduledThreadPool)。
 */
public class ThreadPoolDemo {

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws Exception {

        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("产品经理规划新需求");
            }
        });

        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("开发人员开发新需求功能");
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("测试人员测试新功能");
            }
        });

        System.out.println("早上：");
        System.out.println("产品经理来上班了");
        System.out.println("测试人员来上班了");
        System.out.println("开发人员来上班了");
        System.out.println("领导吩咐:");
        System.out.println("首先，产品经理规划新需求...");
        executorService.submit(thread1);
        System.out.println("然后，开发人员开发新需求功能...");
        executorService.submit(thread2);
        System.out.println("最后，测试人员测试新功能...");
        executorService.submit(thread3);
        executorService.shutdown();
    }
}