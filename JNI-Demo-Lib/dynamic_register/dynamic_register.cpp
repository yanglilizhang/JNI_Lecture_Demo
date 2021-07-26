//
// Created by jiangbin on 2021/5/6.
//
#include "com_jni_dynamic_register_TestDemo.h"
#include <string>
#include <iostream>

using namespace std;

//JNIEnv只在创建它的线程有效，不能跨线程传递，不能再线程之间共享 JNIEnv。

JNIEXPORT jstring JNICALL Java_com_jni_dynamic_register_TestDemo_stringFromJNI
        (JNIEnv *env, jobject thiz) {
    std::string hello = "默认就是静态注册哦";
    cout << hello << endl;
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL Java_com_jni_dynamic_register_TestDemo_staticRegister
        (JNIEnv *env, jobject thiz) {

    cout << "staticRegister" << endl;

}

// native 真正的函数
// void dynamicMethod01(JNIEnv *env, jobject thiz) { // OK的
void dynamicMethod01() { // 也OK  如果你用不到  JNIEnv jobject ，可以不用写
    cout << "我是动态注册的函数 dynamicMethod01..." << endl;
}

int dynamicMethod02(JNIEnv *env, jobject thiz, jstring valueStr) { // 也OK
    const char *text = env->GetStringUTFChars(valueStr, nullptr);
    cout << "我是动态注册的函数 dynamicMethod02... " << text << endl;
    env->ReleaseStringUTFChars(valueStr, text);
    return 200;
}

/*
 typedef struct {
    const char* name;       // 函数名
    const char* signature; // 函数的签名
    void*       fnPtr;     // 函数指针
 } JNINativeMethod;
 */
//java中的native方法名称,调用native参数和返回值,native方法返回值和方法名称
//{"dynamicJavaMethod02", "(Ljava/lang/String;)I", (int *) (dynamicMethod02)},
static const JNINativeMethod jniNativeMethod[] = {
        {"dynamicJavaMethod01", "()V",                   (void *) (dynamicMethod01)},
        {"dynamicJavaMethod02", "(Ljava/lang/String;)I", (int *) (dynamicMethod02)},
};

//这个路径就是java调用native方法的类的路径
const char *mainClassName = "com/jni/dynamic/register/TestDemo";
// JNI JNI_OnLoad函数，如果你不写JNI_OnLoad，默认就有JNI_OnLoad，如果你写JNI_OnLoad函数 覆写默认的JNI_OnLoad函数
extern "C"

// JavaVM *javaVM = NULL;
JNIEXPORT jint JNI_OnLoad(JavaVM *javaVm, void *) {
    //可以在该函数中保存JavaVM指针来供全局使用
    //javaVM = vm;

    JNIEnv *jniEnv = nullptr;
    int result = javaVm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_6);

    // result 等于0  就是成功    【C库 FFmpeg 成功就是0】
    if (result != JNI_OK) {
        return -1; // 会奔溃，故意奔溃
    }

    cout << "System.loadLibrary ---》 JNI Load init"<< endl;

    jclass mainClass = jniEnv->FindClass(mainClassName);

    // jint RegisterNatives(Class（java中含有native方法的类）, 我们的数组==jniNativeMethod， 注册的数量 = 2)
    jniEnv->RegisterNatives(mainClass,
                            jniNativeMethod,
                            sizeof(jniNativeMethod) / sizeof(JNINativeMethod));

    cout << ("动态 注册没有毛病") << endl;

    return JNI_VERSION_1_6; //  // AS的JDK在JNI默认最高1.6      存Java的JDKJNI 1.8
}