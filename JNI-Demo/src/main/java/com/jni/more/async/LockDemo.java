package com.jni.more.async;

import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

    public static void main(String[] args) {
        LockRunnable testLock = new LockRunnable();

        new Thread(testLock, "A").start();
        new Thread(testLock, "B").start();
        new Thread(testLock, "C").start();
    }

}

/**
 * 定义Lock锁实现同步
 */
class LockRunnable implements Runnable {
    int count = 200;

    //定义lock锁
    private final ReentrantLock lock = new ReentrantLock();
    //尝试拿到锁，如果有锁就拿到，没有拿到不会阻塞，返回false
//            tryLock();
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                //上锁（进入同步代码块）
                lock.lock();
                if (count > 0)
                    System.out.println(Thread.currentThread().getName() + "---" + count--);
                else
                    break;
            } finally {
                //解锁（出同步代码块）
                lock.unlock();
            }
        }
    }
}

