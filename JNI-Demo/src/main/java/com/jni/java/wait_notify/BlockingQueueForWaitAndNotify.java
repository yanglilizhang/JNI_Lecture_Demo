package com.jni.java.wait_notify;

import java.util.LinkedList;

/**
 * wait/notify 实现生产者消费者模式
 */
public class BlockingQueueForWaitAndNotify {
    private int maxSize;
    private LinkedList<Object> storage;

    public static void main(String[] args) {
        BlockingQueueForWaitAndNotify blockingQueueForWaitAndNotify =
                new BlockingQueueForWaitAndNotify(16);

        new Thread(() -> {
            while (true) {
                try {
                    blockingQueueForWaitAndNotify.put();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();


        new Thread(() -> {
            while (true) {
                try {
                    blockingQueueForWaitAndNotify.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public BlockingQueueForWaitAndNotify(int size) {
        this.maxSize = size;
        storage = new LinkedList<>();
    }

    //put 方法被 synchronized 保护，while 检查队列是否为满，
    // 如果不满就往里放入数据并通过 notifyAll() 唤醒其他线程。
    public synchronized void put() throws InterruptedException {
        while (storage.size() == maxSize) {
            wait();
        }
        Object o = new Object();
        storage.addLast(o);
        System.out.println("生产了:" + o);
        notifyAll();
    }

    //take 方法也被 synchronized 修饰，while 检查队列是否为空，如果不为空就获取数据并唤醒其他线程。
    public synchronized void take() throws InterruptedException {
        while (storage.size() == 0) {
            wait();
        }
        System.out.println("消费了:" + storage.removeFirst());
        notifyAll();
    }
}
