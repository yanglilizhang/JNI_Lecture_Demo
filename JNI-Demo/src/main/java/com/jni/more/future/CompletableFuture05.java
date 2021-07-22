package com.jni.more.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 在两个CompletableFuture运行后再次计算
 * 用于用异步A、异步B结果请求处理C
 */
public class CompletableFuture05 {

    /**
     * 这种场景适用于当A和B通过异步取到值后，使用A、B计算出C，比如，获取两个用户的爱好交集，
     * 首先需要获取A用户的爱好，接着获取B用户的爱好，然后在求交集
     */


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //如果想运行一批任务中的其中一个，可以使用anyOf()方法，他会在其中挑一个进行执行
        CompletableFuture<List<String>> aUser = CompletableFuture.supplyAsync(() -> {
            return Stream.of("篮球", "羽毛球").collect(Collectors.toList());
        });
        CompletableFuture<List<String>> bUser = CompletableFuture.supplyAsync(() -> {
            return Stream.of("篮球", "排球").collect(Collectors.toList());
        });
        CompletableFuture<List<String>> combinedFuture = aUser
                .thenCombine(bUser, (aHobby, bHobby) -> {
                    aHobby.retainAll(bHobby);
                    return new ArrayList<>(aHobby);
                });
        System.out.println(combinedFuture.get());

//        还有一个方式是runAfterBoth()，他在两个任务完成后，分别以同步/异步的方式执行Runnable
//        public CompletableFuture<Void> runAfterBoth(CompletionStage<?> other,
//                Runnable action)
//
//        public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other,
//                Runnable action)

    }

}
