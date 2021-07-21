package com.jni.more;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 信号量
 * https://juejin.cn/post/6884534557123723271#heading-0
 * 它内部的计数器是递增的
 * 在很多场景下都需要限制流量操作，Semaphore就是限制并发量，保护一个重要的（代码）部分防止一次超过N个线程进入。
 */
public class SemaphoreDemo {
    // 创建一个Semaphore实例
    private static Semaphore semaphore = new Semaphore(0);

//    public static void main(String[] args) throws InterruptedException {
//        executorTest();
//    }

    private static void executorTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //将线程A加入线程池
        executorService.submit(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " over");
                semaphore.release();
            }
        });
        //将线程B加入线程池
        executorService.submit(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " over");
                semaphore.release();
            }
        });

        semaphore.acquire(2);
        System.out.println("all child thread over!");
        //关闭线程池
        executorService.shutdown();
    }

    private static Semaphore semaphore2 = new Semaphore(3);
    private static CyclicBarrier barrier = new CyclicBarrier(4);

    ///利用CyclicBarrier控制4个线程同时执行func函数,但是Semophore限制只能有3个线程同时访问。
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        barrier.await();
                        func();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            barrier.await();
            func();
        }
    }

    public static void func() throws InterruptedException {
        semaphore2.acquire();
        System.out.println(Thread.currentThread().getName() + " execute fun;T=" + System.currentTimeMillis() / 1000);
        Thread.sleep(5000);
        semaphore2.release();
    }

}
