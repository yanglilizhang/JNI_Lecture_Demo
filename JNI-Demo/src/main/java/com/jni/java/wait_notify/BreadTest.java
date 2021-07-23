package com.jni.java.wait_notify;

/**
 *
 * 生产者和消费者问题是线程模型中的经典问题：生产者和消费者在同一时间段内共用同一存储空间，
 * 生产者向空间里生产数据，而消费者从空间里取走数据。
 *
 * 同步代码块
 * 同步方法
 * lock锁机制， 通过创建Lock对象，采用lock()加锁，unlock()解锁，来保护指定的代码块
 *
 * 商店类（Shop）:定义一个成员变量，表示第几个面包，提供生产面包和消费面包的操作；
 */
class Shop {
    private int bread = 0;
    /**
     * 生产面包
     */
    public synchronized void produceBread() {
        if (bread < 10) {
            bread++;
            System.out.println(Thread.currentThread().getName() + ":开始生产第" + bread + "个面包");
            notify(); // 唤醒消费者线程
        } else {
            try {
                wait(); // 告诉生产者等待一下
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 消费面包
     */
    public synchronized void consumeBread() {
        if (bread > 0) {
            System.out.println(Thread.currentThread().getName() + ":开始消费第" + bread + "个面包");
            bread--;
            notify(); // 唤醒生产者线程
        } else {
            try {
                wait(); // 告诉消费者等待一下
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 生产者类（Producer）：实现Runnable接口，重写run()方法，调用生产面包的操作
 */
class Producer extends Thread {
    private Shop shop;

    public Producer(Shop shop) {
        this.shop = shop;
    }

    @Override
    public void run() {
        System.out.println(getName() + ":开始生产面包.....");
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shop.produceBread();
        }
    }
}

/**
 * 消费者类（Consumer）：实现Runnable接口，重写run()方法，调用消费面包的操作
 */
class Consumer extends Thread {
    private Shop shop;
    public Consumer(Shop shop) {
        this.shop = shop;
    }
    
    @Override
    public void run() {
        System.out.println(getName() + ":开始消费面包.....");
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            shop.consumeBread();
        }
    }
}

public class BreadTest {
    public static void main(String[] args) {
        // 创建商店对象
        Shop shop = new Shop();
        // 创建生产者对象，把商店对象作为构造方法参数传入生产者对象中
        Producer p1 = new Producer(shop);
        p1.setName("生产者");
        // 创建消费者对象，把商店对象作为构造方法参数传入消费者对象中
        Consumer c1 = new Consumer(shop);
        c1.setName("消费者");

        p1.start();
        c1.start();
        
    }
}
