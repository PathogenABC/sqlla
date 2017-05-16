package com.greenbean.poplar.sqlla.view;

import java.util.Date;

/**
 * Created by chrisding on 2017/5/16.
 * Function: 结果集视图对象。一个对象代表表视图(结果集视图)的一行。
 */
public interface ViewObject extends Iterable<ViewObject.Entry> {

    int pairs();

    Object optValue(String key, Object nullVal);

    Object optValue(String key);

    byte optByte(String key, byte nullVal);

    byte optByte(String key);

    short optShort(String key, short nullVal);

    short optShort(String key);

    int optInt(String key, int nullVal);

    int optInt(String key);

    long optLong(String key, long nullVal);

    long optLong(String key);

    float optFloat(String key, float nullVal);

    float optFloat(String key);

    double optDouble(String key, double nullVal);

    double optDouble(String key);

    boolean optBoolean(String key, boolean nullVal);

    boolean optBoolean(String key);

    String optString(String key, String nullVal);

    String optString(String key);

    byte[] optBytes(String key, byte[] nullVal);

    byte[] optBytes(String key);

    Date optDate(String key, Date nullVal);

    Date optDate(String key);

    String toString();

    interface Entry {
        String key();

        Object val();
    }
}
