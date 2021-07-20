package com.jni.test;

/**
 * 作者： jiangbin
 * 主页： Github: https://github.com/kusebingtang
 * 日期： 2021年04月28日 4:39 下午
 * 签名： 求之于势，不责于人！
 * _              _           _     _   ____  _             _ _
 * / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 * / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 * / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 * /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/
 * <p>
 * ----------------------------------------------------------------
 */
public class Demo1 {

    static {
        System.load("/Volumes/CodeApp/SourceWork/CPlus_workspace/JNI-Demo-Lib/cmake-build-debug/Demo1/libDemo1.dylib");
    }

    public static final int A = 100;

    public String name = "Demo1"; // 签名：Ljava/lang/String;

    public static int age = 29; // 签名：I

    // Java 本地方法  实现：native层
    public native String getStringPwd();

    public static native String getStringPwd2();

    // -------------  交互操作 JNI  -------------
    public native void changeName();

    public static native void changeAge();

    //告诉native调用java方法
    public native void callAddMethod();


    // 专门写一个函数，给native成调用!!!把值返回给jni层
    public int add(int number1, int number2) {
        return number1 + number2 + 8;
    }

    public static void main(String[] args) {
        System.out.println("test jni ");

        Demo1 demo1 = new Demo1();
        demo1.changeName();

        System.out.println(demo1.name);


        System.out.println("-------------------");
        Demo1.changeAge();
        System.out.println(Demo1.age);

        System.out.println("--------callAddMethod-----------");
        demo1.callAddMethod();

    }
}
