package com.jni.java;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UseCountDownLatch {

    static CountDownLatch latch = new CountDownLatch(6);

    /*初始化线程*/
    private static class InitThread implements Runnable {

        public void run() {
            System.out.println("Thread_" + Thread.currentThread().getId() + " init thread run......");
            latch.countDown();
            for (int i = 0; i < 2; i++) {
                System.out.println("Thread_" + Thread.currentThread().getId()
                        + " ........初始化线程代码---在执行");
            }
        }
    }

    /*业务线程等待latch的计数器为0完成*/
    private static class BusiThread implements Runnable {

        public void run() {
            try {
                // TODO: 2021/7/21 执行等待直到计数器为0
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 3; i++) {
                System.out.println("业务线程_" + Thread.currentThread().getId() + " do business-----");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            public void run() {
//                SleepTools.ms(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread_" + Thread.currentThread().getId()
                        + " ready init work step 1st......");
                latch.countDown();
                System.out.println("begin step 2nd.......");
//                SleepTools.ms(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread_" + Thread.currentThread().getId()
                        + " ready init work step 2nd......");
                latch.countDown();
            }
        }).start();
        new Thread(new BusiThread()).start();
        for (int i = 0; i <= 3; i++) {
            Thread thread = new Thread(new InitThread());
            thread.start();
        }

        latch.await();
        System.out.println("主线程代码工作了........");
        blockByCountDown();
        oneWaitMore();
        moreWaitOne();
    }

    // TODO: 2021/7/21  CountDownLatch通过内置一个线程数量计数器，需要同步多少个线程，
    //  构造方法则传入对应的数量，接着在需要阻塞线程的地方调用await方法，在释放线程的地方调用countDown方法既可。
    //  相比较于join方法，CountDownLatch的优势在于不需要等待线程死亡才释放，可以在满足条件的地方调用countDown方法就可以让计数减一，相比较于join更灵活可控一些。
    private static void blockByCountDown() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("the thread block by countdownLatch");
                countDownLatch.countDown();
            }
        }).start();

        try {
            // TODO: 调用该方法的线程会被挂起，直到count为0时才会被唤醒；
            // TODO: 执行等待直到计数器为0
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("countdownLatch end");
    }

    //////////////////////////////////////////////////////////
    // TODO: 2021/7/21 一等多场景
    private static void oneWaitMore() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);
        ExecutorService service = Executors.newFixedThreadPool(5);
        //创建5个线程，分别执行不通模块测试
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            Runnable runnable = () -> {
                try {
                    //模拟测试耗时
                    Thread.sleep((long) (Math.random() * 5000));
                    System.out.println("模块：" + num + "测试完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //测试完毕后执行减一操作
                    latch.countDown();
                }
            };
            service.submit(runnable);
        }
        System.out.println("等待模块检测...");
        //主线程等待，直到count为0
        latch.await();
        System.out.println("所有模块测试完毕,开始上线");
    }

    //////////////////////////////////////////////////////////
    // TODO: 2021/7/21 一等多场景
    private static void moreWaitOne() throws InterruptedException {
        //重点：这里初始化为1
        CountDownLatch begin = new CountDownLatch(1);

        ExecutorService service = Executors.newFixedThreadPool(5);
        //创建5个线程
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            Runnable runnable = () -> {
                try {
                    System.out.println("运动员：" + num + "等待起跑指令");
                    //所有运动员等待指令
                    begin.await();
                    System.out.println("运动员：" + num + "开始跑步");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            service.submit(runnable);
        }
        Thread.sleep(1000);
        //裁判员发出起步指令
        begin.countDown();
        System.out.println("发令完毕");
    }

    //////////////////////////////////////////////////////////

}
