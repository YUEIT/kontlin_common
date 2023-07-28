#pragma clang diagnostic push
#pragma ide diagnostic ignored "hicpp-deprecated-headers"
//
// Created by admin on 2022/11/7.
//
#include <iostream>
#include <string>
#include "xposed_native.h"
#include "xposed_app_list.h"
#include <android/log.h>

using namespace std;

extern "C" jboolean Java_cn_yue_test_checker_XposedNative_auth(JNIEnv *env, jobject type) {
    return checkXposedResult(env);
}

static const char *findClassName = "cn/yue/test/checker/XposedNative";

jboolean checkXposedResult(JNIEnv *env) {
    jboolean check1 = checkXposed1(env);
    jboolean check2 = checkXposed2(env);
    jboolean check3 = checkXposed3(env);
//    jboolean check4 = checkXposed4(env);
    jboolean check5 = checkXposed5(env);
    jboolean check6 = checkXposed6(env);
    jboolean check7 = checkXposed7(env);
    jboolean check8 = checkXposed8(env);
    jboolean result = check1 || check2 || check3
//            || check4
            || check5 || check6 || check7 || check8;
    if (result == JNI_TRUE) {
        exitApp(env);
    }
//    checkPrint(env, 1, check1);
//    checkPrint(env, 2, check2);
//    checkPrint(env, 3, check3);
//    checkPrint(env, 4, check4);
//    checkPrint(env, 5, check5);
//    checkPrint(env, 6, check6);
//    checkPrint(env, 7, check7);
//    checkPrint(env, 8, check8);
    return result;
}

void checkPrint(JNIEnv *env, int i, jboolean result) {
    char str[20];
    string resultStr;
    sprintf(str, "check %d ", i);
    resultStr = str;
    if (result == JNI_TRUE) {
        resultStr += "true";
    } else {
        resultStr += "false";
    }
    __android_log_print(ANDROID_LOG_INFO, "xposed check", " %s ", resultStr.data());
}

/**
 * 加载类de.robv.android.xposed.XposedHelpers
 */
jboolean checkXposed1(JNIEnv *env) {
    jclass myClass = env->FindClass(findClassName);
    if (myClass == nullptr) {
        return JNI_FALSE;
    }
    jmethodID method = env->GetStaticMethodID(myClass, "testClassLoader",
                                              "(Ljava/lang/String;)Z");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    jstring param = env->NewStringUTF("de.robv.android.xposed.XposedHelpers");
    jboolean result = env->CallStaticBooleanMethod(myClass, method, param);
    env->DeleteLocalRef(myClass);
    env->DeleteLocalRef(param);
    return result;
}

/**
 * 打开文件/proc/uid/maps查找XposedBridge
 */
jboolean checkXposed2(JNIEnv *env) {
    jclass processClass = env->FindClass("android/system/Os");
    jmethodID myPidMethodID = env->GetStaticMethodID(processClass, "getpid", "()I");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    // Os.getpid()
    jint myPid = env->CallStaticIntMethod(processClass, myPidMethodID);

    char maps_path[500];
    if (myPid >= 0) {
        sprintf(maps_path, "/proc/%d/maps", myPid);
    } else {
        sprintf(maps_path, "/proc/self/maps");
    }
    //读取文件
    FILE *file = fopen(maps_path, "r");
    if (!file) {
        return JNI_FALSE;
    }
    int ind = 0;
    char buf[PROCMAPS_LINE_MAX_LENGTH];
    while (!feof(file)) {
        char *str = fgets(buf, PROCMAPS_LINE_MAX_LENGTH, file);
        if (str != NULL) {
            //包含XposedBridge字符串
            const char *compare = strstr(str, "XposedBridge");
            if (compare != NULL) {
                return JNI_TRUE;
            }
            //包含io.va.exposed字符串
            const char *compare1 = strstr(str, "io.va.exposed");
            if (compare1 != NULL) {
                return JNI_TRUE;
            }
        }
    }
    //close file
    fclose(file);
    return JNI_FALSE;
}

/**
 * 尝试从错误堆栈中读取查找xposed
 */
jboolean checkXposed3(JNIEnv *env) {
    jclass myClass = env->FindClass(findClassName);
    if (myClass == nullptr) {
        return JNI_FALSE;
    }
    jmethodID method = env->GetStaticMethodID(myClass, "testException",
                                              "(Ljava/lang/String;)Z");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    jstring param = env->NewStringUTF("XposedBridge;hook.delegate.InstrumentationDelegate");
    jboolean result = env->CallStaticBooleanMethod(myClass, method, param);
    env->DeleteLocalRef(myClass);
    env->DeleteLocalRef(param);
    return result;
}

jobject getApplication(JNIEnv *env) {
    jobject application = NULL;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != NULL) {
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != NULL) {
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        }
        env->DeleteLocalRef(activity_thread_clz);
    }
    return application;
}

/**
 * 读取应用列表安装情况，发现xposed
 */
jboolean checkXposed4(JNIEnv *env) {
    jobject application = getApplication(env);
    // Context(ContextWrapper) class
    jclass contextClass = env->GetObjectClass(application);
    // getPackageManager()
    jmethodID getPackageManager = env->GetMethodID(contextClass, "getPackageManager",
                                                   "()Landroid/content/pm/PackageManager;");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    // android.content.pm.PackageManager object
    jobject packageManager = env->CallObjectMethod(application, getPackageManager);

    jclass packageManagerClass = env->FindClass("android/content/pm/PackageManager");
    jmethodID getInstalledPackages = env->GetMethodID(packageManagerClass,
                                                      "getInstalledPackages",
                                                      "(I)Ljava/util/List;");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    //context.getPackageManager().getInstalledPackages(0)
    jobject listInfo = env->CallObjectMethod(packageManager, getInstalledPackages, 0);

    jclass listClass = env->FindClass("java/util/List");
    jmethodID getLength = env->GetMethodID(listClass, "size", "()I");
    //list.size()
    jint length = env->CallIntMethod(listInfo, getLength);
    int i = 0;
    jboolean resultBool = JNI_FALSE;
    while (i < length) {
        jmethodID getItem = env->GetMethodID(listClass, "get", "(I)Ljava/lang/Object;");
        jobject packageInfoItem = env->CallObjectMethod(listInfo, getItem, i);

        jclass packageInfoClass = env->FindClass("android/content/pm/PackageInfo");
        jfieldID getPackageName = env->GetFieldID(packageInfoClass, "packageName",
                                                  "Ljava/lang/String;");
        if (checkException(env)) {
            return JNI_FALSE;
        }
        //packageInfo.packageName
        jstring packageName = (jstring) env->GetObjectField(packageInfoItem, getPackageName);
        const char *resultStr = env->GetStringUTFChars(packageName, NULL);
        bool result = isContainApp(resultStr);

        env->DeleteLocalRef(packageInfoItem);
        env->DeleteLocalRef(packageInfoClass);
        env->ReleaseStringUTFChars(packageName, resultStr);
        env->DeleteLocalRef(packageName);
        if (result) {
            resultBool = JNI_TRUE;
            break;
        }
        i++;
    }
    env->DeleteLocalRef(application);
    env->DeleteLocalRef(contextClass);
    env->DeleteLocalRef(packageManager);
    env->DeleteLocalRef(packageManagerClass);
    env->DeleteLocalRef(listInfo);
    env->DeleteLocalRef(listClass);
    return resultBool;
}

/**
 * 检查Throwable.getStackTrace()方法标识符
 */
jboolean checkXposed5(JNIEnv *env) {
    jclass myClass = env->FindClass(findClassName);
    if (myClass == nullptr) {
        return JNI_FALSE;
    }
    jmethodID method = env->GetStaticMethodID(myClass, "testStackTrace",
                                              "(Ljava/lang/String;)Z");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    jstring param = env->NewStringUTF("getStackTrace");
    jboolean result = env->CallStaticBooleanMethod(myClass, method, param);
    env->DeleteLocalRef(myClass);
    env->DeleteLocalRef(param);
    return result;
}

/**
 * 从环境变量中寻找vxp
 */
jboolean checkXposed6(JNIEnv *env) {

    jclass myClass = env->FindClass("java/lang/System");
    if (myClass == nullptr) {
        return JNI_FALSE;
    }
    jmethodID methodId = env->GetStaticMethodID(myClass, "getProperty",
                                                "(Ljava/lang/String;)Ljava/lang/String;");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    jstring param = env->NewStringUTF("vxp");
    //System.getProperty("vxp")
    jstring result = (jstring) env->CallStaticObjectMethod(myClass, methodId, param);
    env->DeleteLocalRef(myClass);
    env->DeleteLocalRef(param);
    return result != NULL;

}

/**
 * 从环境变量中读取XposedBridge关键字
 */
jboolean checkXposed7(JNIEnv *env) {
    jclass myClass = env->FindClass("java/lang/System");
    if (myClass == nullptr) {
        return JNI_FALSE;
    }
    jmethodID methodId = env->GetStaticMethodID(myClass, "getenv",
                                                "(Ljava/lang/String;)Ljava/lang/String;");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    jstring param = env->NewStringUTF("CLASSPATH");
    //System.getenv("CLASSPATH")
    jstring result = (jstring) env->CallStaticObjectMethod(myClass, methodId, param);
    env->DeleteLocalRef(myClass);
    env->DeleteLocalRef(param);
    if (result != NULL) {
        const char *resultStr = env->GetStringUTFChars(result, NULL);
        const char *i = strstr(resultStr, "XposedBridge");
        if (i != NULL) {
            env->ReleaseStringUTFChars(result, resultStr);
            env->DeleteLocalRef(result);
            return JNI_TRUE;
        }
    }
    return JNI_FALSE;
}

/**
 * 尝试加载类com.elderdrivers.riru.edxp.config.EdXpConfigGlobal
 */
jboolean checkXposed8(JNIEnv *env) {
    jclass myClass = env->FindClass(findClassName);
    if (myClass == nullptr) {
        return JNI_FALSE;
    }
    jmethodID method = env->GetStaticMethodID(myClass, "testClassLoader",
                                              "(Ljava/lang/String;)Z");
    if (checkException(env)) {
        return JNI_FALSE;
    }
    jstring param = env->NewStringUTF("com.elderdrivers.riru.edxp.config.EdXpConfigGlobal");
    jboolean result = env->CallStaticBooleanMethod(myClass, method, param);
    env->DeleteLocalRef(myClass);
    env->DeleteLocalRef(param);
    return result;
}

void exitApp(JNIEnv *env) {
    jclass myClass = env->FindClass("java/lang/Runtime");
    if (myClass == nullptr) {
        return ;
    }
    jmethodID method = env->GetStaticMethodID(myClass, "getRuntime",
                                              "()Ljava/lang/Runtime;");

    jobject runtime = env->CallStaticObjectMethod(myClass, method);

    jmethodID exitMethod = env->GetMethodID(myClass, "exit", "(I)V");
    env->CallVoidMethod(runtime, exitMethod, 0);
}

bool checkException(JNIEnv *env) {
    if (env->ExceptionCheck()) {
        //输出描述错误信息
        env->ExceptionDescribe();
        //清除掉崩溃信息
        env->ExceptionClear();
        return true;
    }
    return false;
}
