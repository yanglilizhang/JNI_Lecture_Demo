package com.jni.java.future;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 将两个 CompletableFuture组合在一起(链式执行) 串行执行
 */
public class CompletableFuture04 {
    /**
     * 当一个需求需要两个接口来完成时，我们可以将他们组合在一起，比如，要获取与用户A相同爱好的用户信息列表，
     * 首先需要从A服务器获取用户A的爱好，然后从B服务器获取相同爱好的用户，
     * 此时第二阶段需要第一阶段的值。
     */
// TODO: 2021/7/22 thenApply方法也就是以同步的方式继续处理上一个异步任务的结果，
//  当然还有thenApplyAsync()方法以异步方式处理上一个异步任务。
    public static void main(String[] args) {

        CompletableFuture<CompletableFuture<List<String>>> result = getUsersHobby("userId")
//                .thenApplyAsync(s -> getHobbyList(s));//以异步方式获取上个结果
                .thenApply(s -> getHobbyList(s));//以同步方式获取上个结果
        try {
            //使用thenApply() 方法导致了结果嵌套，我们不得不调用两次get来获取最终值，
            // 但如果希望最终结果是顶级 Future，那么可以改用thenCompose()方法
            System.out.println(result.get().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


//        CompletableFuture<List<String>> result = getUsersHobby("userId")
//                .thenCompose(s -> getHobbyList(s));
//        try {
//            System.out.println(result.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

    }

    static CompletableFuture<String> getUsersHobby(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            //发起网络请求，获取userId的爱好
            return "篮球";
        });
    }

    static CompletableFuture<List<String>> getHobbyList(String hobby) {
        //hobby是getUsersHobby()异步处理的结果
        return CompletableFuture.supplyAsync(() -> {
            //发起网络请求，获取具有爱好是hobby的所有用户
            return Arrays.asList("张三", "李四");
        });
    }

}
