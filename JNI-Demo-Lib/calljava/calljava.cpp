//
// Created by jiangbin on 2021/5/8.
//

#include "com_jni_calljava_TestCallJavaObject.h"
#include <string>
#include <iostream>

using namespace std;

// FindClass是通过传java中完整的类名来查找java的class，
// 而GetObjectClass是通过传入jni中的一个java的引用来获取该引用的类型。
// 前者要求你必须知道完整的类名，后者要求在Jni有一个类的引用。
JNIEXPORT void JNICALL Java_com_jni_calljava_TestCallJavaObject_putObject
        (JNIEnv *env, jobject thiz, jobject student) {

    // 1.寻找类 Student-->获取某个对象的"类"
    cout << "---------寻找类 Student---------------" << endl;
    jclass studentClass1 = env->FindClass("com/jni/calljava/Student"); // 第一种
    jclass studentClass2 = env->GetObjectClass(student); // 第二种

    cout << "studentClass1=" << studentClass1 << endl;
    cout << "studentClass1=" << studentClass2 << endl;


    cout << "-----------GetMethodID------------" << endl;
    // 2.Student类里面的函数规则  签名
    jmethodID setName = env->GetMethodID(studentClass1, "setName", "(Ljava/lang/String;)V");
    jmethodID getName = env->GetMethodID(studentClass1, "getName", "()Ljava/lang/String;");
    jmethodID showInfo = env->GetStaticMethodID(studentClass1, "showInfo", "(Ljava/lang/String;)V");

    // 3.调用 setName
    cout << "-----------CallVoidMethod------------" << endl;
    jstring value = env->NewStringUTF("AAAA");
    env->CallVoidMethod(student, setName, value);

    // 4.调用 getName
    jstring getNameResult = static_cast<jstring>(env->CallObjectMethod(student, getName));
    const char *getNameValue = env->GetStringUTFChars(getNameResult, NULL);
    cout << ("调用到getName方法，值是:") << getNameValue << endl;

    // 5 访问对象的变量
    cout << "------访问对象的变量---------" << endl;
    jfieldID fieldID_mAge = env->GetFieldID(studentClass1, "age", "I");
    // 设置Java对象obj的变量mAge的值
    env->SetIntField(student, fieldID_mAge, 18);
    // 从Java对象obj中获取变量mAge的值
    jint age = env->GetIntField(student, fieldID_mAge);
    cout << "访问对象的变量 Age: " << age << std::endl;


}

//对象操作
//直接创建一个Java对象AllocObject
//根据某个构造函数来创建Java对象NewObject/NewObjectA/NewObjectV
JNIEXPORT void JNICALL Java_com_jni_calljava_TestCallJavaObject_insertObject
        (JNIEnv *env, jobject thiz) {

    // 1.通过包名+类名的方式 拿到 Student class  凭空拿class
    jclass studentClass = env->FindClass("com/jni/calljava/Student"); // 第一种

    // 2.通过student的class  实例化此Student对象   C++ new Student *直接创建一个Java对象AllocObject*
    jobject studentObj = env->AllocObject(studentClass); // AllocObject 只实例化对象，不会调用对象的构造函数

    // 3.方法签名的规则
    jmethodID setName = env->GetMethodID(studentClass, "setName", "(Ljava/lang/String;)V");
    jmethodID setAge = env->GetMethodID(studentClass, "setAge", "(I)V");

    // 4.调用方法
    jstring strValue = env->NewStringUTF("HelloJni");
    env->CallVoidMethod(studentObj, setName, strValue);
    env->CallVoidMethod(studentObj, setAge, 18);

    jmethodID toStringMethod = env->GetMethodID(studentClass, "toString", "()Ljava/lang/String;");
    jstring toStringResult = static_cast<jstring>(env->CallObjectMethod(studentObj, toStringMethod));
    const char *value = env->GetStringUTFChars(toStringResult, nullptr);
    cout << value << endl;

    env->DeleteLocalRef(studentClass);
    env->DeleteLocalRef(studentObj);
}

//定义全局引用 必须手动去释放！！！！！
jclass dogClass;
JNIEXPORT void JNICALL Java_com_jni_calljava_TestCallJavaObject_testQuote
        (JNIEnv *env, jobject thiz) {
    if (nullptr == dogClass) {
        // 升级全局引用： JNI函数结束也不释放，反正就是不释放，必须手动释放   ----- 相当于： C++ 对象 new、手动delete
        const char *dogStr = "com/jni/calljava/Dog";
        jclass temp = env->FindClass(dogStr);
        // 提升全局引用
        dogClass = static_cast<jclass>(env->NewGlobalRef(temp)); 
        // 记住：用完了，如果不用了，马上释放，C C++ 工程师的赞美
        env->DeleteLocalRef(temp);
    }

    // <init> V  是不会变的

    // 构造函数一
    //1.先获取方法签名
    jmethodID init = env->GetMethodID(dogClass, "<init>", "()V");
    //2.执行java的构造函数
    jobject dog = env->NewObject(dogClass, init);

    // 构造函数2
    init = env->GetMethodID(dogClass, "<init>", "(I)V");
    dog = env->NewObject(dogClass, init, 100);


    // 构造函数3
    init = env->GetMethodID(dogClass, "<init>", "(II)V");
    dog = env->NewObject(dogClass, init, 200, 300);

    // 构造函数4
    init = env->GetMethodID(dogClass, "<init>", "(III)V");
    dog = env->NewObject(dogClass, init, 400, 500, 600);

    env->DeleteLocalRef(dog); // 释放
}

//给对象obj创建一个全局引用，obj可以是全局或局部引用。
//全局引用必须通过DeleteGlobalRef()显示处理。
//jobject NewGlobalRef(JNIEnv *env,object obj);全局引用jobject，如果内存溢出则返回NULL

 
//删除全局引用DeleteGlobalRef
//删除局部引用DeleteLocalRef
//创建一个局部引用NewLocalRef
//释放一个局部引用PopLocalFrame
//设定局部变量的容量EnsureLocalCapacity

//弱全局引用
//创建全局弱引用NewWeakGlobalRef
//删除全局弱引用DeleteWeakGlobalRef


JNIEXPORT void JNICALL Java_com_jni_calljava_TestCallJavaObject_delQuote
        (JNIEnv *env, jobject thiz) {
    if (dogClass != nullptr) {
        cout<<("全局引用释放完毕~~~~~~~~~🤣🤣🤣🤣");
        env->DeleteGlobalRef(dogClass);
        dogClass = nullptr; // 最好给一个NULL，指向NULL的地址，不要去成为悬空指针，为了好判断悬空指针的出现
    }
}