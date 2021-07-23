package com.jni.java;

/**
 * join的用法，哪个线程调用join哪个线程就插队先执行
 * 如在线程B中调用了线程A的Join()方法，直到线程A执行完毕后，才会继续执行线程B。
 * threadA.join(); //把当前线程执行时间让给threadA线程，直到threadA执行完毕才会继续执行当前线程
 * threadA.join(1000);  //把当前线程执行时间让给threadA线程，1000毫秒后，A、B两个线程再重新竞争
 */
public class Test1 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new Test1(), "线程一");
        Thread thread2 = new Thread(new Test1(), "线程二");
        Thread thread3 = new Thread(new Test1(), "线程三");
        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("------------------主线程到此-------------------");
//        thread2.join();//谁加join谁就先执行

        for (int i = 0; i < 5; i++) {
            System.out.println("主线程" + i);
        }
        blockByJoin();
    }

    // TODO: 通过join方法使调用thread的线程阻塞直到thread线程终止，就可以得到预期结果，
    private static void blockByJoin() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("the thread block by join");
            }
        });
        thread.start();

        try {
            // TODO: 意思是我（thread）先执行 ，先阻塞掉其他操作
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("join end");

    }

}