package com.jni.more.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 将多个 CompletableFuture 组合在一起
 * 用于多个异步任务后干其他事情！！！！！！
 */
public class CompletableFuture06 {

    /**
     * ompletableFuture.allOf 适用于在执行完众多异步任务后做某事时使用，
     * 问题allOf()在于它返回CompletableFuture<Void>，表示没有结果集， 
     * 但是我们可以通过编写一些额外的代码行来获得所有包装的 CompletableFutures的结果
     */


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //如果想运行一批任务中的其中一个，可以使用anyOf()方法，他会在其中挑一个进行执行
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(getUserName("1"),
                getUserName("2"),
                getUserName("3"))
                .thenAccept(new Consumer<Void>() {
                    @Override
                    public void accept(Void unused) {
                        System.out.println("完成");
                    }
                });
        completableFuture.join();
    }

    static CompletableFuture<String> getUserName(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "用户-" + userId;
        });
    }
}
