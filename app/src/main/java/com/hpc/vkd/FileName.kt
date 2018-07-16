package com.hpc.vkd

/**
 * Class describing name of song
 */

class FileName(val artist: String?, val title: String?) {

    val isCompleted: Boolean
        get() = artist != null && title != null

    fun generateFileName(): String {
        return ((artist ?: Constants.EMPTY_STRING) +
                Constants.SPACE +
                Constants.DASH +
                Constants.SPACE +
                (title ?: Constants.EMPTY_STRING)
                + Constants.AUDIO_EXT)
    }
}
