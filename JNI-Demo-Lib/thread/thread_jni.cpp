//
// Created by jiangbin on 2021/5/7.
//

#include "com_jni_thread_TestDemo.h"
#include <jni.h>
#include <string>
#include <pthread.h>
#include <iostream>

using namespace std;

static JavaVM *gVm = nullptr;

JNIEXPORT int JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    cout << ("JNI_OnLoad") << endl;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    gVm = vm;
    return JNI_VERSION_1_6;
}

void *printThreadHello(void *) {
    cout << ("hello thread");
    // 切记要有返回值
    return nullptr;
}

class MyContext {
public:
    JNIEnv *jniEnv = nullptr;  // 不能跨线程 ，会奔溃
    jobject instance = nullptr; // 不能跨线程 ，会奔溃
};


void *myThreadTaskAction(void *pVoid) { // 当前是异步线程
    // 这两个是必须要的
    // JNIEnv *env
    // jobject thiz   OK

    MyContext *myContext = static_cast<MyContext *>(pVoid);

    // jclass mainActivityClass = myContext->jniEnv->FindClass(mainActivityClassName); // 不能跨线程 ，会奔溃
    // mainActivityClass = myContext->jniEnv->GetObjectClass(myContext->instance); // 不能跨线程 ，会奔溃

    // TODO 解决方式 （安卓进程只有一个 JavaVM，是全局的，是可以跨越线程的）
    JNIEnv *jniEnv = nullptr; // 全新的JNIEnv  异步线程里面操作
    jint attachResult = ::gVm->AttachCurrentThread(reinterpret_cast<void **>(&jniEnv), nullptr); // 附加当前异步线程后，会得到一个全新的 env，此env相当于是子线程专用env
    if (attachResult != JNI_OK) {
        return 0; // 附加失败，返回了
    }

    // 1.拿到class
    jclass mainActivityClass = jniEnv->GetObjectClass(myContext->instance);

    // 2.拿到方法
    jmethodID updateActivityUI = jniEnv->GetMethodID(mainActivityClass, "updateActivityUI", "()V");

    // 3.调用
    jniEnv->CallVoidMethod(myContext->instance, updateActivityUI);

    ::gVm->DetachCurrentThread(); // 必须解除附加，否则报错

    cout << ("C++ 异步线程OK") << endl;

    return nullptr;
}


JNIEXPORT void JNICALL Java_com_jni_thread_TestDemo_naitveThread
        (JNIEnv *env, jobject job) {

    MyContext *myContext = new MyContext;
    myContext->jniEnv = env;
    // myContext->instance = job; // 默认是局部引用，会奔溃
    myContext->instance = env->NewGlobalRef(job); // 提升全局引用

    pthread_t pid;
    //对于本地库中创建的线程，需要使用AttachCurrentThread来附加到 JavaVM来获取一个可用的JNIEnv。
    //线程退出或不再需要使用JNIEnv时，必须通过调用DetachCurrentThread来解除连接。
    pthread_create(&pid, nullptr, myThreadTaskAction, myContext);
    pthread_join(pid, nullptr);

}




JNIEXPORT void JNICALL Java_com_jni_thread_TestDemo_nativeFun1
        (JNIEnv *env, jobject job)
{
    JavaVM * javaVm = nullptr;
    env->GetJavaVM(&javaVm);

    // 打印：当前函数env地址， 当前函数jvm地址， 当前函数job地址,  JNI_OnLoad的jvm地址
    printf("nativeFun1 当前函数env地址%p,  当前函数jvm地址:%p,  当前函数job地址:%p, JNI_OnLoad的jvm地址:%p\n", env, javaVm, job, ::gVm);
    cout<< endl;
}

JNIEXPORT void JNICALL Java_com_jni_thread_TestDemo_nativeFun2
        (JNIEnv *env, jobject job)
{
    JavaVM * javaVm = nullptr;
    env->GetJavaVM(&javaVm);

    // 打印：当前函数env地址， 当前函数jvm地址， 当前函数job地址,  JNI_OnLoad的jvm地址
    printf("nativeFun2 当前函数env地址%p,  当前函数jvm地址:%p,  当前函数job地址:%p, JNI_OnLoad的jvm地址:%p\n", env, javaVm, job, ::gVm);
    cout<< endl;
}

void * run(void *) { // native的子线程 env地址  和  Java的子线程env地址，一样吗  不一样的
    JNIEnv * newEnv = nullptr;
    ::gVm->AttachCurrentThread(reinterpret_cast<void **>(&newEnv), nullptr);
    // 打印：当前函数env地址， 当前函数jvm地址， 当前函数clazz地址,  JNI_OnLoad的jvm地址

    printf("run jvm地址:%p,  当前run函数的newEnv地址:%p \n", ::gVm, newEnv);
    cout<< endl;

    ::gVm->DetachCurrentThread();
    return nullptr;
}

JNIEXPORT void JNICALL Java_com_jni_thread_TestDemo_staticFun3
        (JNIEnv *env, jclass clazz)
{
    JavaVM * javaVm = nullptr;
    env->GetJavaVM(&javaVm);

    // 打印：当前函数env地址， 当前函数jvm地址， 当前函数job地址,  JNI_OnLoad的jvm地址
    printf("nativeFun3 当前函数env地址%p,  当前函数jvm地址:%p,  当前函数clazz地址:%p, JNI_OnLoad的jvm地址:%p\n", env, javaVm, clazz, ::gVm);
    cout<< endl;

    // 调用run
    pthread_t pid;
    pthread_create(&pid, nullptr, run, nullptr);
}

JNIEXPORT void JNICALL Java_com_jni_thread_TestDemo_staticFun4
        (JNIEnv *env, jclass clazz)
{
    JavaVM * javaVm = nullptr;
    env->GetJavaVM(&javaVm);

    // 打印：当前函数env地址， 当前函数jvm地址， 当前函数job地址,  JNI_OnLoad的jvm地址
    printf("nativeFun4 当前函数env地址%p,  当前函数jvm地址:%p,  当前函数clazz地址:%p, JNI_OnLoad的jvm地址:%p\n", env, javaVm, clazz, ::gVm);
    cout<< endl;
}