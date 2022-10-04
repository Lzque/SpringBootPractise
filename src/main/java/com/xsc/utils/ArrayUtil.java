package com.xsc.utils;

public class ArrayUtil {
    private ArrayUtil() {

    }

    public static byte[] merge(byte[] btX, byte[] btY) {
        //定义目标数组  目标数组应该等于将要拼接的两个数组的总长度
        byte[] btZ = new byte[btX.length + btY.length];
        System.arraycopy(btX, 0, btZ, 0, btX.length);
        System.arraycopy(btY, 0, btZ, btX.length, btY.length);
        return btZ;
    }
}
