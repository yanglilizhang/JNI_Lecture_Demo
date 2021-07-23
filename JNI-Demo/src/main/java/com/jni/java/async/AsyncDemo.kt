package com.jni.java.async

import java.lang.Thread.sleep

fun main(args: Array<String>) {
   print("sssssssss")
}

/**
 * “ 现有 Task1、Task2 等多个并行任务，如何等待全部执行完成后，执行 Task3。”
 */
class AsyncDemo {

    val task1: () -> String = {
        sleep(2000)
        "Hello".also { println("task1 finished: $it") }
    }

    val task2: () -> String = {
        sleep(2000)
        "World".also { println("task2 finished: $it") }
    }

    val task3: (String, String) -> String = { p1, p2 ->
        sleep(2000)
        "$p1 $p2".also { println("task3 finished: $it") }
    }

    companion object {
        /** 我是main入口函数 **/
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }

}