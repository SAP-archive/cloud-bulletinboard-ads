package com.sap.bulletinboard.statistics.util;

import java.nio.charset.Charset;

public class UTF8StringConverter {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public byte[] toByteArray(String string) {
        return string.getBytes(CHARSET_UTF8);
    }

    public String toString(byte[] byteArray) {
        return new String(byteArray, CHARSET_UTF8);
    }
}
