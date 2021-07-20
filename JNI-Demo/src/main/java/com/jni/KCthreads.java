package com.jni;

/**
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
    private volatile static int v = 0;  //[解法4使用]
    private static int s = 0;
    public static void main(String[] args) throws InterruptedException {
        //【解法1：】join()方法：将线程并行变为串行
//        Thread t1 = new Thread(() -> test(), "线程A");
//        Thread t2 = new Thread(() -> test(), "线程B");
//        Thread t3 = new Thread(() -> test(), "线程C");
//        t1.start();t1.join();
//        t2.start();t2.join();
//        t3.start();
        //【解法2：】CountDownLatch计数器
//        CountDownLatch count = new CountDownLatch(2);
//        new Thread(() -> {
//            test();
//            count.countDown();
//        }, "线程A").start();
//        new Thread(()->{  //匿名内部类，也就是重写run()方法
//            while (count.getCount() == 2){
//
//            }
//            count.countDown();
//            test();
//        },"线程B").start();
//        new Thread(()->{  //匿名内部类，也就是重写run()方法
//            while (count.getCount() == 1){
//
//            }
//            test();
//        },"线程C").start();
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
        //【解法6：】使用锁Lock：ReentrantLock
//        Lock lock = new ReentrantLock();
//        new Thread(()->{
//            test();
//        },"线程A").start();
//        new Thread(()->{
//            lock.lock();
//            test();
//            lock.unlock();
//        },"线程B").start();
//        new Thread(()->{
//            lock.lock();
//            test();
//            lock.unlock();
//        },"线程C").start();
        //【解法7：】最暴力解法：sleep
        Thread t1 = new Thread(() -> test(), "线程A");
        Thread.sleep(1000);
        t1.start();
        Thread t2 = new Thread(() -> test(), "线程B");
        Thread.sleep(1000);
        t2.start();
        Thread t3 = new Thread(() -> test(), "线程C");
        Thread.sleep(1000);
        t3.start();
    }

    private static void test() {
        System.out.println(Thread.currentThread().getName());
    }

    //打印结果：顺序打印
    // 线程A
    // 线程B
    // 线程C
}