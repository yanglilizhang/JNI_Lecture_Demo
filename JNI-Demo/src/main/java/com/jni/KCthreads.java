package com.jni;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 在java并发中，控制共享变量的访问非常重要，有时候我们也想控制并发线程的执行顺序，
 * 多线程实现按序打印
 * 1、使用Thread.join()方法；
 * 2、使用计数器CountDownLatch
 * 3、使用原子类AtomicInteger
 * 4、使用volatile关键字
 * 5、使用synchronized关键字
 * 6、使用Lock
 * 7、调用Thread的静态方法sleep()-暴力解法
 */
public class KCthreads {
    //怎么保证线程执行顺序？
    //- 保证多线程之间变量的内存可见性 - 禁止指令重排序
    private volatile static int v = 0;  //[解法4使用]
    private static int s = 0;

    public static void main(String[] args) throws InterruptedException {
        //TODO【解法1：】join()方法：将线程并行变为串行
        //TODO  join在线程里面意味着“插队”，哪个线程调用join代表哪个线程插队先执行——
        // 但是插谁的队是有讲究了，不是说你可以插到队头去做第一个吃螃蟹的人，而是插到在当前运行线程的前面，比如系统目前运行线程A，在线程A里面调用了线程B.join方法，则接下来线程B会抢先在线程A面前执行，等到线程B全部执行完后才继续执行线程A。
        //https://www.cnblogs.com/chiweiming/p/11095682.html
//        Thread t1 = new Thread(() -> test(), "线程A");
//        Thread t2 = new Thread(() -> test(), "线程B");
//        Thread t3 = new Thread(() -> test(), "线程C");
//        //默认是并行
////        t1.start();
////        t2.start();
////        t3.start();
//        t1.start();t1.join();
//        t2.start();t2.join();
//        t3.start();
        //*********************************************************************
        //TODO【解法2：】CountDownLatch计数器-原理是内部有一个计数器
        //TODO 参数就是用来控制CountDownLatch需要递减多少次才释放锁（打开门闩)

        //TODO CountDownLatch的使用不一定非得一个线程进行等待，多个线程等待也是可以的，
        // 他的Count数和线程数也是没有关系的，Count的数和执行countDown的次数是有关系的，
        // 一个线程可以多次执行countDown方法的
//        CountDownLatch count = new CountDownLatch(2);
//        new Thread(() -> {
//            test();
//            count.countDown();//2->1
//        }, "线程A").start();
//        new Thread(() -> {  //匿名内部类，也就是重写run()方法
//            while (count.getCount() == 2) {
//                System.out.println("线程B中 count == 2");
//            }
//            count.countDown();//1->0
//            test();
//        }, "线程B").start();
//        new Thread(() -> {  //匿名内部类，也就是重写run()方法
//            while (count.getCount() == 1) {
//                System.out.println("线程C中 count == 1");
//            }
//            test();
//        }, "线程C").start();
        //*********************************************************************
        // TODO: AtomicInteger原子类
        //AtomicInteger的主要用途是当我们处于多线程上下文中时，我们需要对int值执行原子操作，而无需使用synced关键字
        //【解法3：】AtomicInteger原子类
//        AtomicInteger atomic = new AtomicInteger(0);
//        new Thread(() -> {
//            test();
//            atomic.incrementAndGet();  //自增i++
//        }, "线程A").start();
//        new Thread(()->{  //匿名内部类，也就是重写run()方法
//            while (atomic.get() == 0){
//
//            }
//            atomic.incrementAndGet();
//            test();
//        },"线程B").start();
//        new Thread(()->{  //匿名内部类，也就是重写run()方法
//            while (atomic.get() == 1){
//
//            }
//            atomic.incrementAndGet();
//            test();
//        },"线程C").start();
        //*********************************************************************

        //【解法4：】使用volatile关键字
//        new Thread(()->{
//            test();
//            v = 1;
//        },"线程A").start();
//        new Thread(()->{
//            while(v == 0){
//
//            }
//            test();
//            v = 2;
//        },"线程B").start();
//        new Thread(()->{
//            while(v == 1){
//
//            }
//            test();
//        },"线程C").start();
        //*********************************************************************

        //【解法5：】使用synchronized关键字
//        new Thread(()->{
//            test();
//            s++;
//        },"线程A").start();
//        new Thread(()->{
//            synchronized (KCthreads.class){
//                while (s == 0){
//
//                }
//                test();
//                s++;
//            }
//        },"线程B").start();
//        new Thread(()->{
//            synchronized (KCthreads.class){
//                while (s == 1){
//
//                }
//                test();
//            }
//        },"线程C").start();
        //*********************************************************************

        //【解法6：】使用锁Lock：ReentrantLock
        Lock lock = new ReentrantLock();
        new Thread(()->{
            test();
        },"线程A").start();
        new Thread(()->{
            lock.lock();
            test();
            lock.unlock();
        },"线程B").start();
        new Thread(()->{
            lock.lock();
            test();
            lock.unlock();
        },"线程C").start();
        //【解法7：】最暴力解法：sleep
//        Thread t1 = new Thread(() -> test(), "线程A");
//        Thread.sleep(1000);
//        t1.start();
//        Thread t2 = new Thread(() -> test(), "线程B");
//        Thread.sleep(5000);
//        t2.start();
//        Thread t3 = new Thread(() -> test(), "线程C");
//        Thread.sleep(1000);
//        t3.start();
    }

    private static void test() {
        System.out.println("---->" + Thread.currentThread().getName());
    }

    //打印结果：顺序打印
    // 线程A
    // 线程B
    // 线程C

    /**
     * [1] 使用线程的join方法
     * [2] 使用主线程的join方法
     * [3] 使用线程的wait方法
     * [4] 使用线程的线程池方法
     * [5] 使用线程的Condition(条件变量)方法
     * [6] 使用线程的CountDownLatch(倒计数)方法
     * [7] 使用线程的CyclicBarrier(回环栅栏)方法
     * [8] 使用线程的Semaphore(信号量)方法
     * https://zhuanlan.zhihu.com/p/80787379
     */


}