package com.jni.java.顺序打印;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用线程的Condition(条件变量)方法
 * 使用Condition(条件变量)实现线程按顺序运行
 */
public class ThreadConditionDemo {
//    应用场景：Condition是一个多线程间协调通信的工具类，使得某个，或者某些线程一起等待某个条件（Condition）,
//    只有当该条件具备( signal 或者 signalAll方法被调用)时 ，这些等待线程才会被唤醒，
//    从而重新争夺锁。
    private static Lock lock = new ReentrantLock();
    private static Condition condition1 = lock.newCondition();
    private static Condition condition2 = lock.newCondition();

    /**
     * 为什么要加这两个标识状态?
     * 如果没有状态标识，当t1已经运行完了t2才运行，t2在等待t1唤醒导致t2永远处于等待状态
     */
    private static Boolean t1Run = false;
    private static Boolean t2Run = false;

    public static void main(String[] args) {

        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                System.out.println("产品经理规划新需求");
                t1Run = true;
                condition1.signal();
                lock.unlock();
            }
        });

        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    if(!t1Run){
                        System.out.println("开发人员先休息会...");
                        condition1.await();
                    }
                    System.out.println("开发人员开发新需求功能");
                    t2Run = true;
                    condition2.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    if(!t2Run){
                        System.out.println("测试人员先休息会...");
                        condition2.await();
                    }
                    System.out.println("测试人员测试新功能");
                    lock.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("早上：");
        System.out.println("测试人员来上班了...");
        thread3.start();
        System.out.println("产品经理来上班了...");
        thread1.start();
        System.out.println("开发人员来上班了...");
        thread2.start();
    }
    //运行结果:这里输出会有很多种顺序，主要是因为线程进入的顺序，造成锁住线程的顺序不一致
}