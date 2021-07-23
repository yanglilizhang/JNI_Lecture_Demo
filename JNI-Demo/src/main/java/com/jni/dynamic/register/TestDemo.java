package com.jni.dynamic.register;

/**
 * 作者： jiangbin https://github.com/kusebingtang/JNI_Lecture_Demo
 * 主页： Github: https://github.com/kusebingtang
 * 日期： 2021年05月06日 10:13 上午
 * 签名： 求之于势，不责于人！https://www.jianshu.com/p/58b79d9c80d2
 * https://www.jianshu.com/nb/48908385
 * 演示jni的静态注册和动态注册的用法！！！
 * 使用javah自动生成JNI C函数头文件(如果找不到类 可在src/main/java目录下执行)
 * c++代码 通过Clion新建C++工程，com_jni_dynamic_register_TestDemo.h放入工程中
 * _              _           _     _   ____  _             _ _
 * / \   _ __   __| |_ __ ___ (_) __| | / ___|| |_ _   _  __| (_) ___
 * / _ \ | '_ \ / _` | '__/ _ \| |/ _` | \___ \| __| | | |/ _` | |/ _ \
 * / ___ \| | | | (_| | | | (_) | | (_| |  ___) | |_| |_| | (_| | | (_) |
 * /_/   \_\_| |_|\__,_|_|  \___/|_|\__,_| |____/ \__|\__,_|\__,_|_|\___/
 * <p>
 * ----------------------------------------------------------------
 *
 */
public class TestDemo {
    //生成.h头文件 即使生成错误仍然可以生成.h文件
//I:\gitee_study\JNI_Lecture_Demo\JNI-Demo\src\main\java>javah com.jni.dynamic.register.TestDemo
    static {
        //调试的本地路径
        System.load("/Volumes/CodeApp/SourceWork/CPlus_workspace/JNI-Demo-Lib/cmake-build-debug/dynamic_register/libdynamicRegister.dylib");
    }

    public native String stringFromJNI(); // 静态注册

    public native void staticRegister(); // 静态注册

    public native void dynamicJavaMethod01(); // 动态注册1

    public native int dynamicJavaMethod02(String valueStr); // 动态注册2

    public static void main(String[] args) {

        System.out.println("-------------------start main-------------------");

        TestDemo demo = new TestDemo();

        demo.dynamicJavaMethod01();

        System.out.println("-------------------华丽的分割线-------------------");

        demo.dynamicJavaMethod02("hello 2222");

    }


}
