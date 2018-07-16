package com.hpc.vkd;

import android.util.Log;

import java.io.File;

/**
 * Created by hpc on 9/4/17.
 */

public final class FileUtils {

    private FileUtils(){}

    public static String getNotExistentFilePath(String baseName) {
        int suffixInt = 1;
        File file = new File(baseName);
        while (file.exists()) {
            String suffix = "_" + String.valueOf(suffixInt++);
            file = new File(appendSuffixToName(baseName, suffix));
        }
        return file.getAbsolutePath();
    }

    private static String appendSuffixToName(String baseName, String suffix) {
        if (baseName == null) {
            return null;
        }


        int pos = baseName.lastIndexOf(".");


        if (pos == -1) {
            return baseName + suffix;
        }

        String ext = baseName.substring(pos);
        String shortName = baseName.substring(0, pos);
        shortName = shortName + suffix;
        return shortName + ext;
    }
}
