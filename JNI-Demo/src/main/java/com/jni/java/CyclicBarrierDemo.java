package com.jni.java;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 屏障
 * 场景介绍：这里我们要实现的是，使用两个线程去执行一个被分解的任务A，
 * 当两个线程把自己的任务都执行完毕后再对它们的结果进行汇总处理。
 * 总结：分解执行任务，所有完成再汇总
 */
public class CyclicBarrierDemo {

    // 创建一个CyclicBarrier实例，添加一个所有子线程全部达到屏障后执行的任务
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2, new Runnable() {
        public void run() {
            //合并其他线程的结果
            System.out.println(Thread.currentThread().getName() + " task1 merge result");
        }
    });

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        executorTest();
        threadTest();
    }

    private static void threadTest() throws InterruptedException, BrokenBarrierException {
        final CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {
            public void run() {
                // TODO: 2021/7/21 是并发之前优先执行的
                System.out.println("barrier execute");
            }
        });
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "countdown");
                }
            }).start();
        }
        Thread.sleep(1000);
        barrier.await();
        System.out.println("main thread execute");
    }

    private static void executorTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //将线程A添加到线程池
        executorService.submit(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " task1-1");
                System.out.println(Thread.currentThread().getName() + " enter in barrier");
                try {
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " enter out barrier");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        //将线程B添加到线程池
        executorService.submit(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " task1-2");
                System.out.println(Thread.currentThread().getName() + " enter in barrier");
                try {
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " enter out barrier");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        //关闭线程池
        executorService.shutdown();
    }

}
