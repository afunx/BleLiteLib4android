package com.afunx.ble.blelitelib.utils;

/**
 * @author: afunx
 * @email : afunx.bai@ubtrobot.com
 * @time : 2017/06/01
 * desc  : 十六进制字符串转换类
 */

public class HexUtils {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return bytes2HexString(bytes, 0, bytes.length);
    }

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes  字节数组
     * @param offset 开始位置
     * @param count  字节长度
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes, int offset, int count) {
        if (bytes == null || offset < 0 || count <= 0 || count - offset > bytes.length) {
            return null;
        }
        char[] ret = new char[count << 1];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(hexDigits[bytes[i + offset] >>> 4 & 0x0f]);
            sb.append(hexDigits[bytes[i + offset] & 0x0f]);
            if (i != count - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
