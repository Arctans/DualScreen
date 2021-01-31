#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lv_dualscreen_1different_1input_FFmpeg_stringFromJNI(JNIEnv *env, jclass clazz) {
    // TODO: implement stringFromJNI()
    // TODO: implement stringFromJNI()
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}