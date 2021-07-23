package com.jni.java.async

import java.lang.Thread.sleep
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

//TODO ***************************************************
//TODO 实现线程同步的N种方法
//https://juejin.cn/post/6981952428786597902#heading-9
//https://juejin.cn/post/6979012171564843044#heading-0
//TODO ***************************************************
val task1: () -> String = {
    sleep(1000)
    "Hello".also { println("task1 finished: $it") }
}

val task2: () -> String = {
    sleep(1000)
    "World".also { println("task2 finished: $it") }
}

val task3: (String, String) -> String = { p1, p2 ->
    sleep(1000)
    "$p1 $p2".also { println("task3 finished: $it") }
}

/**
 * “ 现有 Task1、Task2 等多个并行任务，如何等待全部执行完成后，执行 Task3。”
 */
fun main(args: Array<String>) {
//    testJoin()

//    testReentrantLock()
//    testCyclicBarrier()
    testNoAsync()
}

fun testNoAsync() {
    var s1: String = ""
    var s2: String = ""


    val t1 = Thread { s1 = task1() }
    val t2 = Thread { s2 = task2() }
    t1.start()
    t2.start()

    task3(s1, s2)
}

// TODO: Thread.join()
fun testJoin() {
    lateinit var s1: String
    lateinit var s2: String

    val t1 = Thread { s1 = task1() }
    val t2 = Thread { s2 = task2() }
    val t3 = Thread(Runnable {
        print("thread3---run---")
    })
    t1.start()
    t2.start()

    t1.join()
    t2.join()

    task3(s1, s2)
    t3.start()
}

// TODO: 使用 synchronized 锁进行同步
fun testSyncFun() {
    lateinit var s1: String
    lateinit var s2: String

    Thread {
        synchronized(Unit) {
            s1 = task1()
        }
    }.start()
    s2 = task2()

    synchronized(Unit) {
        task3(s1, s2)
    }

}

// TODO: ReentrantLock 是 JUC 提供的线程锁，可以替换 synchronized 的使用
//ReentrantLock底层是Lock，Lock底层是AQS，所以可以使用AQS自定义各种锁
fun testReentrantLock() {

    lateinit var s1: String
    lateinit var s2: String

    val lock = ReentrantLock()
    Thread {
        lock.lock()
        s1 = task1()
        lock.unlock()
    }.start()
    s2 = task2()

    lock.lock()
    task3(s1, s2)
    lock.unlock()

}

// TODO: BlockingQueue
//阻塞队列内部也是通过 Lock 实现的，所以也可以达到同步锁的效果
fun testBlockingQueue() {

    lateinit var s1: String
    lateinit var s2: String

    val queue = SynchronousQueue<Unit>()

    Thread {
        s1 = task1()
        queue.put(Unit)
    }.start()

    s2 = task2()

    queue.take()
    task3(s1, s2)
}

// TODO:  CountDownLatch
//JUC 中的锁大都基于 AQS 实现的，可以分为独享锁和共享锁。ReentrantLock 就是一种独享锁。
// 相比之下，共享锁更适合本场景。 例如 CountDownLatch，它可以让一个线程一直处于阻塞状态，
// 直到其他线程的执行全部完成
fun test_countdownlatch() {
//共享锁的好处是不必为了每个任务都创建单独的锁，即使再多并行任务写起来也很轻松
    lateinit var s1: String
    lateinit var s2: String
    val cd = CountDownLatch(2)
    Thread() {
        s1 = task1()
        cd.countDown()
    }.start()

    Thread() {
        s2 = task2()
        cd.countDown()
    }.start()

    cd.await()
    task3(s1, s2)
}

// TODO: CyclicBarrier
//CyclicBarrier 是 JUC 提供的另一种共享锁机制，
// 它可以让一组线程到达一个同步点后再一起继续运行，其中任意一个线程未达到同步点，
// 其他已到达的线程均会被阻塞。
fun testCyclicBarrier() {

    lateinit var s1: String
    lateinit var s2: String
    val cb = CyclicBarrier(3)

    Thread {
        s1 = task1()
        cb.await()
    }.start()

    Thread() {
        s2 = task2()
        cb.await()
    }.start()

    cb.await()
    task3(s1, s2)

}

// TODO: CAS
//AQS 内部通过自旋锁实现同步，自旋锁的本质是利用
// CompareAndSwap 避免线程阻塞的开销。 因此，我们可以使用基于 CAS 的原子类计数，
// 达到实现无锁操作的目的。

fun testCas() {

    lateinit var s1: String
    lateinit var s2: String

    val cas = AtomicInteger(2)

    Thread {
        s1 = task1()
        cas.getAndDecrement()
    }.start()

    Thread {
        s2 = task2()
        cas.getAndDecrement()
    }.start()

    while (cas.get() != 0) {
        //While 循环空转看起来有些浪费资源，但是自旋锁的本质就是这样，
        // 所以 CAS 仅仅适用于一些cpu密集型的短任务同步
    }

    task3(s1, s2)

}

// TODO: volatile
//看到 CAS 的无锁实现，也许很多人会想到 volatile， 是否也能实现无锁的线程安全？
fun test_Volatile() {
    //注意，这种写法是错误的 volatile 能保证可见性，但是不能保证原子性
    // ，cnt-- 并非线程安全，需要加锁操作

    lateinit var s1: String
    lateinit var s2: String

    Thread {
        s1 = task1()
//        cnt--
    }.start()

    Thread {
        s2 = task2()
//        cnt--
    }.start()

//    while (cnt != 0) {
//    }

    task3(s1, s2)

}

// TODO: Future
//上面无论有锁操作还是无锁操作，都需要定义两个变量s1、s2记录结果非常不方便。
// Java 1.5 开始，提供了 Callable 和 Future ，可以在任务执行结束时返回结果
fun test_future() {

    val future1 = FutureTask(Callable(task1))
    val future2 = FutureTask(Callable(task2))

    Executors.newCachedThreadPool().execute(future1)
    Executors.newCachedThreadPool().execute(future2)
//通过 future.get()，可以同步等待结果返回
    task3(future1.get(), future2.get())

}

// TODO: CompletableFuture
//future.get() 虽然方便，但是会阻塞线程。 Java 8 中引入了 CompletableFuture  ，
// 他实现了 Future 接口的同时实现了 CompletionStage 接口。
// CompletableFuture 可以针对多个 CompletionStage 进行逻辑组合、实现复杂的异步编程。
// 这些逻辑组合的方法以回调的形式避免了线程阻塞：
fun testCompletableFuture() {
    CompletableFuture.supplyAsync(task1)
        .thenCombine(CompletableFuture.supplyAsync(task2)) { p1, p2 ->
            task3(p1, p2)
        }.join()
}

// TODO: RxJava
//RxJava 提供的各种操作符以及线程切换能力同样可以帮助我们实现需求：
// zip 操作符可以组合两个 Observable 的结果；subscribeOn 用来启动异步任务
fun test_Rxjava() {

//    Observable.zip(
//        Observable.fromCallable(Callable(task1))
//            .subscribeOn(Schedulers.newThread()),
//        Observable.fromCallable(Callable(task2))
//            .subscribeOn(Schedulers.newThread()),
//        BiFunction(task3)
//    ).test().awaitTerminalEvent()

}

// TODO: Coroutine
//前面讲了那么多，其实都是 Java 的工具。 Coroutine 终于算得上是 Kotlin 特有的工具了
fun testCoroutine() {

//    runBlocking {
//        val c1 = async(Dispatchers.IO) {
//            task1()
//        }
//
//        val c2 = async(Dispatchers.IO) {
//            task2()
//        }
//
//        task3(c1.await(), c2.await())
//    }
}

// TODO: Flow
//Flow 就是 Coroutine 版的 RxJava，具备很多 RxJava 的操作符，例如 zip:

fun test_flow() {

//    val flow1 = flow<String> { emit(task1()) }
//    val flow2 = flow<String> { emit(task2()) }
//
//    runBlocking {
//        flow1.zip(flow2) { t1, t2 ->
//            task3(t1, t2)
//        }.flowOn(Dispatchers.IO)//FlowOn 使得 Task 在异步计算并发射结果。
//            .collect()
//    }

}
