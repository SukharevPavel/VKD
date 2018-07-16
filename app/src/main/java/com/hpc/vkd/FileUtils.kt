package com.hpc.vkd

import java.io.File

/**
 * Created by hpc on 9/4/17.
 */

object FileUtils {

    fun getNotExistentFilePath(baseName: String): String {
        var suffixInt = 1
        var file = File(baseName)
        while (file.exists()) {
            val suffix = "_" + suffixInt++.toString()
            file = File(appendSuffixToName(baseName, suffix)!!)
        }
        return file.absolutePath
    }

    private fun appendSuffixToName(baseName: String?, suffix: String): String? {
        if (baseName == null) {
            return null
        }


        val pos = baseName.lastIndexOf(".")


        if (pos == -1) {
            return baseName + suffix
        }

        val ext = baseName.substring(pos)
        var shortName = baseName.substring(0, pos)
        shortName = shortName + suffix
        return shortName + ext
    }
}
