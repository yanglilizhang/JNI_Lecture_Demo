//
// Created by jiangbin on 2021/4/28.
//
#include "jni.h"
#include "com_jni_test_Demo1.h"

JNIEXPORT jstring JNICALL Java_com_jni_test_Demo1_getStringPwd
        (JNIEnv * , jobject) {

}

JNIEXPORT jstring JNICALL Java_com_jni_test_Demo1_getStringPwd2
        (JNIEnv * , jclass) {}

//jobject thiz的话就需要寻找类
//jclass jClass一般就是操作java中的静态相关的属性和方法        
JNIEXPORT void JNICALL Java_com_jni_test_Demo1_changeName
        (JNIEnv * env, jobject thiz) {
    // 获取class
    jclass j_cls = env->GetObjectClass(thiz);

    // 获取属性  L对象类型 都需要L
    // jfieldID GetFieldID(MainActivity.class, 属性名, 属性的签名)
    jfieldID j_fid = env->GetFieldID(j_cls, "name", "Ljava/lang/String;");

    // 转换工作
    jstring j_str = static_cast<jstring>(env->GetObjectField(thiz, j_fid));

    // 打印字符串  目标
    char *c_str = const_cast<char *>(env->GetStringUTFChars(j_str, nullptr));
    printf("native : %s\n", c_str);
    printf("native : %s\n", c_str);
    printf("native : %s\n", c_str);

    // 修改成 Beyond
    jstring jName = env->NewStringUTF("Beyond");
    //改变java一般属性的值
    env-> SetObjectField(thiz, j_fid, jName);

// printf()  C
// cout << << endl; // C++
}

extern "C"
JNIEXPORT void JNICALL
        Java_com_jni_test_Demo1_changeAge(JNIEnv * env, jclass jClass) {

    //因为java中的age为静态 可直接操作age
    jfieldID j_fide = env->GetStaticFieldID(jClass,"age","I");
    jint age = env->GetStaticIntField(jClass,j_fide);
    age+=10;
    //改变java静态属性的值
    env->SetStaticIntField(jClass,j_fide,age);

}


JNIEXPORT void JNICALL Java_com_jni_test_Demo1_callAddMethod (JNIEnv *env,
                                                              jobject job) {
        //找到java的相关class
        jclass  mainClass = env->GetObjectClass(job);

        //找到相关方法
        // GetMethodID(MainActivity.class, java中的方法名, 方法的签名)
        jmethodID j_mid = env->GetMethodID(mainClass, "add", "(II)I");

        // 调用 Java的方法 并接收了java的返回值
        jint sum = env->CallIntMethod(job, j_mid, 3, 3);
        printf("sum result:%d", sum);

}