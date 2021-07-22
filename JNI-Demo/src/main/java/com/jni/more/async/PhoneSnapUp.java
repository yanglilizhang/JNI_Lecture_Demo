package com.jni.more.async;

/**
 * 多线程-同步方法与同步代码块
 * synchronized：
 *
 * 默认锁的是他自身的对象。
 * 要是跨对象，通常使用同步块。
 * 即锁共享资源所在的对象。
 *
 * 总结：同步方法可以做到保证结果不会出错。
 * 但是因为是方法，一加上synchronized 整个方法内的所有内容就相当于被加上了锁，可能会引起效率问题，导致程序阻塞~
 * 所以一般我们都是使用同步代码块来实现加锁的操作。
 */
public class PhoneSnapUp implements Runnable {

    private Integer inventory = 10;//手机库存
    private boolean flag = true;

    public static void main(String[] args) {
        PhoneSnapUp phoneSnapUp = new PhoneSnapUp();
        //模拟5人同时抢购，即同时开启5个线程
        new Thread(phoneSnapUp, "丁大大1号").start();
        new Thread(phoneSnapUp, "丁大大2号").start();
        new Thread(phoneSnapUp, "丁大大3号").start();
        new Thread(phoneSnapUp, "丁大大4号").start();
        new Thread(phoneSnapUp, "丁大大5号").start();
    }

    @Override //同步方法块
    public synchronized void run() {
        while (flag){
            try {
                //同步代码块
//                synchronized (this) {
                buy();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //购票方法提出来
    private void buy() throws Exception {
        //当库存为0时，抢购结束
        if (inventory <= 0) {
            flag = false;
            return;
        }
        //模拟延迟，否则结果不容易看出来
        Thread.sleep(500);
        //每次抢购完，库存减1
        System.out.println("恭喜！！" + Thread.currentThread().getName() + "--抢到了一台小米12！库存还剩：" + --inventory + "台");
    }
}

  