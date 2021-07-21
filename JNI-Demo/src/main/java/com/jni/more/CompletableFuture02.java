package com.jni.more;

import java.util.concurrent.CompletableFuture;

/**
 * 使用Future获得异步执行结果时，要么调用阻塞方法get()，要么轮询看isDone()是否为true，
 * 这两种方法都不是很好，因为主线程也会被迫等待。
 * CompletableFuture--->可以传入回调对象，当异步任务完成或者发生异常时，自动调用回调对象的回调方法。
 */
public class CompletableFuture02 {
//    可见CompletableFuture的优点是：
//
//    异步任务结束时，会自动回调某个对象的方法；
//    异步任务出错时，会自动回调某个对象的方法；
//    主线程设置好回调后，不再关心异步任务的执行。

    // TODO: 2021/7/21 多个CompletableFuture可以串行执行！！！！！！！！！！
    public static void main(String[] args) throws Exception {
        // 第一个任务:
        CompletableFuture<String> cfQuery = CompletableFuture.supplyAsync(() -> {
            return queryCode("中国石油");
        });
        // cfQuery成功后继续执行下一个任务:
        CompletableFuture<Double> cfFetch = cfQuery.thenApplyAsync((code) -> {
            return fetchPrice(code);
        });
        // cfFetch成功后打印结果:
        cfFetch.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(2000);
    }

    static String queryCode(String name) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return "601857";
    }

    static Double fetchPrice(String code) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        return 5 + Math.random() * 20;
    }
}
