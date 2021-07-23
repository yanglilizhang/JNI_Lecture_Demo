package com.jni.java.wait_notify;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * BlockingQueue 实现生产者消费者
 *
 * Disruptor生产者消费者的终极解决方案
 * Disruptor是一个高性能的异步处理框架，或者可以认为是最快的消息框架（轻量的JMS），
 * 也可以认为是一个观察者模式的实现，或者事件监听模式的实现。
 */
public class ProducerConsumerPatternSimple {
    public static void main(String[] args) {
        BlockingQueue<Object> queue = new ArrayBlockingQueue<>(10);
        Runnable producer = () -> {
            while (true) {
                try {
                    Object object = new Object();
                    queue.put(object);
                    System.out.println("生产了:" + object.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(producer).start();
        new Thread(producer).start();

        Runnable consumer = () -> {
            while (true) {
                try {
                    Object object = queue.take();
                    System.out.println("消费了:" + object.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(consumer).start();
        new Thread(consumer).start();
    }

}
