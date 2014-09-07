package com.nokia.memhog;

public class NativeHogLib {
    static {
        System.loadLibrary("memhog");
    }

    /**
     * Eat the given size memory.
     */
    public native int Swallow(int size);

    /**
     * Excrete all the memory has been eaten.
     */
    public native void Shit();

    /**
     * How big the memory has been eaten.
     */
    public native int HowFat();
}
