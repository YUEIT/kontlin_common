//
// Created by admin on 2022/11/7.
//

#include <jni.h>
#include <string>
#ifndef KOTLIN_COMMON_XPOSED_NATIVE_H
#define KOTLIN_COMMON_XPOSED_NATIVE_H
#include <linux/limits.h>

//maximum line length in a procmaps file
#define PROCMAPS_LINE_MAX_LENGTH  (PATH_MAX + 100)

jboolean checkXposedResult(JNIEnv *env);
jboolean checkXposed1(JNIEnv *env);
jboolean checkXposed2(JNIEnv *env);
jboolean checkXposed3(JNIEnv *env);
jboolean checkXposed4(JNIEnv *env);
jboolean checkXposed5(JNIEnv *env);
jboolean checkXposed6(JNIEnv *env);
jboolean checkXposed7(JNIEnv *env);
jboolean checkXposed8(JNIEnv *env);
void exitApp(JNIEnv *env);
void checkPrint(JNIEnv *env, int i, jboolean result);
jobject getApplication(JNIEnv *env);
bool checkException(JNIEnv *env);

#ifdef __cplusplus
extern "C" { //#表示可以供外部调用
#endif

extern "C" JNIEXPORT jboolean
JNICALL
Java_cn_yue_test_checker_XposedNative_auth(JNIEnv *env, jobject type);

#ifdef __cplusplus
}
#endif

#endif //KOTLIN_COMMON_XPOSED_NATIVE_H
