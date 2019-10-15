package dev.toppe

import java.io.File


data class Image(
    val path: String,
    val expiration: Long? = null,
    val created: Long = System.currentTimeMillis()
) {

    var views: Int = 0

    fun getFile(): File {
        return File(path)
    }

    fun exists(): Boolean {
        return (expiration == null || created + expiration > System.currentTimeMillis()) && getFile().exists()
    }

    fun expires(): Boolean {
        return expiration != null
    }
}

