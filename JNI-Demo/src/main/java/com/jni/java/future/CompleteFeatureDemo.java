package com.jni.java.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompleteFeatureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        simpleTask();
        serialTask();
//        andTask();
//        orTask();
//        complexTask();

        sleep(2000); // 等待子线程结束
        System.out.println("end.");

    }

    private static void simpleTask() throws ExecutionException, InterruptedException {
        // 1. runAsync 执行一个异步任务，没有返回值
        CompletableFuture.runAsync(()-> System.out.println("1. runAsync"));
        sleep(100);

        // 2. supplyAsync 执行一个异步任务，有返回值
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            System.out.println("2.1 supplyAsync task be called");
            sleep(100);
            return "2.2 supplyAsync return value";
        });
        System.out.println("2.3 after supplyAsync");
        System.out.println(future.get());
        sleep(200);
    }

    private static void serialTask() throws ExecutionException, InterruptedException {
        // 3. thenRun
        CompletableFuture.supplyAsync(()->{
            System.out.println("3.1 supplyAsync begin");
            sleep(100);  // 用于证明B等待A结束才会执行
            return "3.2 supplyAsync end";
        }).thenRun(()->{
            System.out.println("3.3 thenRun be called.");
        });
        sleep(200);

        // 4. thenApply
        CompletableFuture<String> future4 = CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "4.1 apple";
        }).thenApply(returnVal->{
            return "4.2 " + returnVal + "-苹果";
        });
        System.out.println("4.3 get: " + future4.get());
        sleep(100);

        // 5. thenAccept
        CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "5.1 orange";
        }).thenAccept(returnVal->{
            System.out.println("5.2 " + returnVal + "-桔子");
        });
        sleep(100);

        // 6. thenCompose
        CompletableFuture<String> future6 = CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "6.1 apple";
        }).thenCompose((returnVal)->{
            return CompletableFuture.supplyAsync(()->{
                return "6.2 " + returnVal;
            });
        });
        System.out.println("6.3 get: " + future6.get());
        sleep(100);

        // 7. whenComplete
        CompletableFuture.supplyAsync(()->{
            sleep(100);
            if (true) {  //修改boolean值观察不同结果
                return "7.1 return value for whenComplete";
            } else {
                throw new RuntimeException("7.2 throw exception for whenComplete");
            }
        }).whenComplete((returnVal, throwable)->{
            System.out.println("7.2 returnVal: " + returnVal);  // 可以直接拿到返回值，不需要通过future.get()得到
            System.out.println("7.3 throwable: " + throwable);  // 异步任务抛出异常，并不会因为异常终止，而是会走到这里，后面的代码还会继续执行
        });
        sleep(100);

        // 8. exceptionally
        CompletableFuture<String> future8 = CompletableFuture.supplyAsync(()->{
            sleep(100);
            if (false) {  //修改boolean值观察不同结果
                return "8.1 return value for exceptionally";
            } else {
                throw new RuntimeException("8.2 throw exception for exceptionally");
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return "8.3 return value after dealing exception.";
        });
        System.out.println("8.4 get: " + future8.get());
        sleep(100);

        // 9. handle
        CompletableFuture<String> future9 = CompletableFuture.supplyAsync(()->{
            sleep(100);
            if (false) {  //修改boolean值观察不同结果
                return "9.1 return value for handle";
            } else {
                throw new RuntimeException("9.2 throw exception for handle");
            }
        }).handle((retuanVal, throwable)->{
            System.out.println("9.3 retuanVal: " + retuanVal);
            System.out.println("9.4 throwable: " + throwable);
            return "9.5 new return value.";
        });
        System.out.println("9.6 get: " + future9.get());
        sleep(100);
    }

    private static void andTask() throws ExecutionException, InterruptedException {
        // 10. thenCombine 合并结果
        CompletableFuture<String> future10 = CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "10.1 TaskA return value";
        }).thenCombine(CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "10.2 TaskB return value";
        }), (taskAReturnVal, taskBReturnVal) -> taskAReturnVal + taskBReturnVal);
        System.out.println("10.3 get: " + future10.get());
        sleep(200);

        // 11. thenAcceptBoth
        CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "11.1 TaskA return value";
        }).thenAcceptBoth(CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "11.2 TaskB return value";
        }), (taskAReturnVal, taskBReturnVal) -> System.out.println(taskAReturnVal + taskBReturnVal));
        sleep(200);

        // 12. runAfterBoth A，B都执行完后才执行C，C不关心前面任务的返回值
        CompletableFuture.supplyAsync(()->{
            sleep(200);  // 虽然这个任务先执行，但是执行时间比下面的任务长，所以最后会使用下面的返回结果
            System.out.println("12.1 TaskA be called.");
            return "12.2 TaskA return value";
        }).runAfterBoth(CompletableFuture.supplyAsync(()->{
            sleep(100);
            System.out.println("12.3 TaskB be called.");
            return "12.4 TaskB return value";
        }), () -> System.out.println("12.5 TaskC be called."));
        sleep(300);
    }

    private static void orTask() throws ExecutionException, InterruptedException {
        // 13. applyToEither 使用A,B两个异步任务优先返回的结果
        CompletableFuture<String> future13 = CompletableFuture.supplyAsync(()->{
            sleep(200);  // 虽然这个任务先执行，但是执行时间比下面的任务长，所以最后会使用下面的返回结果
            System.out.println("13.1 TaskA be called"); // 用于证明拿到B的结果后，A还会继续执行，并不会终止
            return "13.2 TaskA return value";
        }).applyToEither(CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "13.3 TaskB return value";
        }), (returnVal) -> returnVal);
        System.out.println("13.4 get: " + future13.get());
        sleep(300);

        // 14. acceptEither 使用A,B两个异步任务优先返回的结果
        CompletableFuture.supplyAsync(()->{
            sleep(200);  // 虽然这个任务先执行，但是执行时间比下面的任务长，所以最后会使用下面的返回结果
            return "14.1 TaskA return value";
        }).acceptEither(CompletableFuture.supplyAsync(()->{
            sleep(100);
            return "14.2 TaskB return value";
        }), (returnVal) -> System.out.println(returnVal));
        sleep(300);

        // 15. runAfterEither A，B任意一个执行完后就执行C，C不关心前面任务的返回值
        CompletableFuture.supplyAsync(()->{
            sleep(200);  // 虽然这个任务先执行，但是执行时间比下面的任务长，所以最后会使用下面的返回结果
            System.out.println("15.1 TaskA be called.");
            return "15.2 TaskA return value";
        }).runAfterEither(CompletableFuture.supplyAsync(()->{
            sleep(100);
            System.out.println("15.3 TaskB be called.");
            return "15.4 TaskB return value";
        }), () -> System.out.println("15.5 TaskC be called."));
        sleep(300);
    }

    private static void complexTask() throws ExecutionException, InterruptedException {
        // 16. anyOf
        CompletableFuture future16 = CompletableFuture.anyOf(CompletableFuture.supplyAsync(()->
        {
            sleep(300);
            System.out.println("16.1 TaskA be called.");
            return "16.2 TaskA return value.";
        }), CompletableFuture.supplyAsync(()->{
            sleep(100);
            System.out.println("16.3 TaskB be called.");
            return "16.4 TaskB return value.";
        }));
        System.out.println("16.5 get: " + future16.get());
        sleep(400);

        // 17. allOf
        CompletableFuture<Void> future17 = CompletableFuture.allOf(CompletableFuture.supplyAsync(()->
        {
            sleep(300);
            System.out.println("17.1 TaskA be called.");
            return "17.2 TaskA return value.";
        }), CompletableFuture.supplyAsync(()->{
            sleep(100);
            System.out.println("17.3 TaskB be called.");
            return "17.4 TaskB return value.";
        }));
        System.out.println("17.5 get: " + future17.get()); // allOf没有返回值
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
