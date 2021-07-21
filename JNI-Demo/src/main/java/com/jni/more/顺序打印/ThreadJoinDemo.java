package com.jni.more.顺序打印;

/**
 * @author wwj
 * 通过子程序join使线程按顺序执行
 * join():是Theard的方法，作用是调用线程需等待该join()线程执行完成后，才能继续用下运行。
 * 应用场景：当一个线程必须等待另一个线程执行完毕才能执行时可以使用join方法。
 */
public class ThreadJoinDemo {

    public static void main(String[] args) {
        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("产品经理规划新需求");
            }
        });

        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread1.join();
                    System.out.println("开发人员开发新需求功能");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread2.join();
                    System.out.println("测试人员测试新功能");
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
}