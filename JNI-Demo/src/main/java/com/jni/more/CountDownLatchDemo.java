package com.jni.more;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 倒计时门栓
 * 场景介绍：日常开发中经常会遇到需要在主线程中开启多个线程去并行地执行任务，
 * 并且主线程需要等待所有子线程执行完毕后再进行汇总的情景。
 * 总结：多线程执行任务，所有完成再汇总
 */
public class CountDownLatchDemo {
    //    在CountDownLatch出现之前一般都是使用线程的join()方法来实现这一点，
//    但是join方法不够灵活（因为项目实践中一般都避免直接操作线程，
//    而是使用ExecutorService线程池来管理，而ExecutorService传递的参数
//    是Runable或者Callable对象，这个时候没有办法调用线程的join方法），
//    不能够满足不同场景的需要

    public static void main(String[] args) throws InterruptedException {
        threadTest();
        executorTest();
    }

    private static void threadTest() throws InterruptedException {
        //因为下面模拟三个线程，因此创建一个计数值为3的CountDownLatch对象。
        final CountDownLatch latch = new CountDownLatch(3);
        //循环执行并打印3个线程，依次将计数值减1
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "countdown");
                    latch.countDown();
                }
            }).start();
        }
        //计数值不为0时将阻塞，为0时才执行
        latch.await();
        System.out.println("main function execute");
    }

    //创建一个CountDownLatch实例，计数值2
    private static CountDownLatch latch = new CountDownLatch(2);

    private static void executorTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //将线程A添加到线程池
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " over!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });
        //将线程B添加到线程池
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " over!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        });
        System.out.println("wait all thread over!");
        //等待线程A和线程B执行完毕
        latch.await();
        System.out.println("main thread over!");
        executorService.shutdown();
    }

}
