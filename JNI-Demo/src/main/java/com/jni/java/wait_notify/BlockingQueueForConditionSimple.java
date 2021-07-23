package com.jni.java.wait_notify;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition 模拟wait/notify
 */
public class BlockingQueueForConditionSimple {
    private Queue queue;
    private int max = 16;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public static void main(String[] args) {
        BlockingQueueForConditionSimple blockingQueueForCondition = new BlockingQueueForConditionSimple(10);
        new Thread(() -> {
            while (true) {
                Object o = new Object();
                System.out.println("生产了:" + o.toString());
                try {
                    blockingQueueForCondition.put(o);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        new Thread(() -> {
            while (true) {
                try {
                    Object o = blockingQueueForCondition.take();
                    System.out.println("消费了:" + o.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public BlockingQueueForConditionSimple(int size) {
        this.max = size;
        queue = new LinkedList();
    }

    public void put(Object o) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == max) {
                condition.await();
            }
            queue.add(o);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == 0) {
                condition.await();
            }
            Object item = queue.remove();
            condition.signalAll();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
