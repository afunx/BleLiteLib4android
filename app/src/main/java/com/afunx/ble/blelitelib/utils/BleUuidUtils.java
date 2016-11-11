package com.afunx.ble.blelitelib.utils;

import java.util.UUID;

/**
 * Created by afunx on 11/11/2016.
 */

/**
 * for UUID has a long magic String "-0000-1000-8000-00805f9b34fb",
 * only 0x0000 - 0xffff is valid for hex int
 * <p>
 * UUID String: 0000eeee-0000-1000-8000-00805f9b34fb
 * UUID Int: 0xeeee
 */
public class BleUuidUtils {

    private final static int UUID_LENGTH = 36;
    private final static String MAGIC_STR = "-0000-1000-8000-00805f9b34fb";

    /**
     * convert uuid from int to UUID
     *
     * @param uuid uuid int
     * @return UUID of uuid
     */
    public static UUID int2uuid(int uuid) {
        String uuidStr = int2str(uuid);
        return UUID.fromString(uuidStr);
    }

    /**
     * convert uuid from String to UUID
     *
     * @param uuid uuid String
     * @return UUID of uuid
     */
    public static UUID str2uuid(String uuid) {
        return UUID.fromString(uuid);
    }

    /**
     * convert uuid from UUID to String
     *
     * @param uuid uuid UUID
     * @return String of uuid
     */
    public static String uuid2str(UUID uuid) {
        return uuid.toString();
    }

    /**
     * convert uuid from int to String
     *
     * @param uuid uuid int
     * @return String of uuid
     */
    public static String int2str(int uuid) {
        if (uuid < 0x0000 || uuid > 0xffff) {
            throw new IllegalArgumentException("uuid int range is [0x0000,0xffff]");
        }
        StringBuilder sb = new StringBuilder();
        String hexString = Integer.toHexString(uuid);
        for (int i = 0; i < 8 - hexString.length(); i++) {
            sb.append("0");
        }
        sb.append(hexString);
        sb.append(MAGIC_STR);
        return sb.toString();
    }

    /**
     * convert uuid from String to int
     *
     * @param uuid uuid String
     * @return int of uuid
     */
    public static int str2int(String uuid) {
        if (uuid == null || uuid.length() != UUID_LENGTH || !uuid.endsWith(MAGIC_STR)) {
            throw new IllegalArgumentException("invalid uuid string");
        }
        String hexString = uuid.substring(4, 8);
        int value = Integer.parseInt(hexString, 16);
        return value;
    }

    /**
     * convert uuid from UUID to int
     *
     * @param uuid uuid UUID
     * @return int of uuid
     */
    public static int uuid2int(UUID uuid) {
        String uuidStr = uuid.toString();
        return str2int(uuidStr);
    }
}
