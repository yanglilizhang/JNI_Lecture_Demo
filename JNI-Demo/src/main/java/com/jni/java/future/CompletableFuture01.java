package com.jni.java.future;

import java.util.concurrent.CompletableFuture;

/**
 * 使用Future获得异步执行结果时，要么调用阻塞方法get()，要么轮询看isDone()是否为true，
 * 这两种方法都不是很好，因为主线程也会被迫等待。
 * CompletableFuture--->可以传入回调对象，当异步任务完成或者发生异常时，自动调用回调对象的回调方法。
 */
public class CompletableFuture01 {
//    可见CompletableFuture的优点是：
//
//    异步任务结束时，会自动回调某个对象的方法；
//    异步任务出错时，会自动回调某个对象的方法；
//    主线程设置好回调后，不再关心异步任务的执行。

    public static void main(String[] args) throws Exception {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFuture01::fetchPrice);
        // 如果执行成功:
        cf.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 如果执行异常:
        cf.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(200);
    }

    static Double fetchPrice() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
//        if (Math.random() < 0.3) {
//            throw new RuntimeException("fetch price failed!");
//        }
        return 5 + Math.random() * 20;
    }

    // TODO: 2021/7/22  CompletableFuture 异常处理

//    程序运行避免不了错误，那么CompletableFuture是如何处理异常的呢，先看以下代码，
//    如果在supplyAsync()任务中发生错误，那么就不会调用任何thenApply()的回调，
//    如果在第一个thenApply()回调中发生错误，则不会调用第二个及以后的，依此类推。

//    CompletableFuture.supplyAsync(() -> {
//        // Code which might throw an exception
//        return "Some result";
//    }).thenApply(result -> {
//        return "processed result";
//    }).thenApply(result -> {
//        return "result after further processing";
//    }).thenAccept(result -> {
//        // do something with the final result
//    });

    // TODO: 2021/7/22  使用exceptionly()回调处理异常

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture<Integer> maturityFuture = CompletableFuture.supplyAsync(() -> {
//            return  1/0;
//        }).exceptionally(ex -> {
//            ex.printStackTrace();
//            return 1;
//        });
//        System.out.println(maturityFuture.get());
//    }

}
