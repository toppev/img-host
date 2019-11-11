package dev.toppe.img.host.image

import com.google.gson.annotations.SerializedName
import dev.toppe.img.host.user.User
import kotlinx.css.img
import org.bson.types.ObjectId
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


data class Image(
    val path: String,
    val expiration: Long? = null,
    val token: String? = null,
    val created: Long = System.currentTimeMillis()
) {

    var _id: ObjectId? = null
    var views: Int = 0
    var lastViewed: Long? = null

    fun getFile(): File {
        return File(path)
    }

    fun getCreatedFormatted(): String {
        return SimpleDateFormat().format(Date(created))
    }

    fun getExpiresFormatted(): String {
        return if (expires()) SimpleDateFormat().format(Date(System.currentTimeMillis() + expiration!!)) else "never"
    }

    fun getLastViewedFormatted(): String {
        return if (lastViewed != null) SimpleDateFormat().format(Date(lastViewed!!)) else "never"
    }

    fun exists(): Boolean {
        return (expiration == null || created + expiration > System.currentTimeMillis()) && getFile().exists()
    }

    fun expires(): Boolean {
        return expiration != null
    }
}

