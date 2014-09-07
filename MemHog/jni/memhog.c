#include "com_nokia_memhog_NativeHogLib.h"
#include <stdlib.h>

static void **l_stomach_array = NULL;
static int l_stomach_array_size = 0;
static int l_stomach_size = 0;

JNIEXPORT jint JNICALL
Java_com_nokia_memhog_NativeHogLib_Swallow (JNIEnv *env, jobject obj, jint value1)
{
    char *r;
    void **tmp = l_stomach_array;

    r = malloc(value1);
    if (!r) return 0;

    memset(r, 0xfc, value1);
    l_stomach_array = malloc((l_stomach_array_size + 1) * sizeof(char*));
    memcpy(l_stomach_array, tmp, l_stomach_array_size * sizeof(char*));
    free(tmp);
    l_stomach_array[l_stomach_array_size++] = (void *)r;
    l_stomach_size += value1;

    return (int)r;
}


JNIEXPORT void JNICALL
Java_com_nokia_memhog_NativeHogLib_Shit (JNIEnv *env, jobject obj)
{
    int i;
    for (i = 0; i < l_stomach_array_size; i++) {
        free(l_stomach_array[i]);
    }
    free(l_stomach_array);
    l_stomach_array = NULL;
    l_stomach_array_size = 0;
    l_stomach_size = 0;
    return;
}

JNIEXPORT jint JNICALL
Java_com_nokia_memhog_NativeHogLib_HowFat (JNIEnv *env, jobject obj)
{
    return l_stomach_size;
}
