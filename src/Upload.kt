package dev.toppe

import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.locations.post
import io.ktor.request.receiveStream
import io.ktor.response.respondText
import io.ktor.routing.Route
import org.bson.types.ObjectId
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

fun Route.upload(imageDatabase: ImageDatabase, uploadDir: File) {

    post<Upload> {
        // TODO: implement UploadLimit
        val map = parseUpload(call.receiveStream())
        if (map != null) {
            val id = ObjectId()
            // TODO different extensions
            val fileName = "$id.png"
            val targetFile = File(uploadDir, fileName)
            val image = Image(targetFile.name, getInMillis(map["expiration"] as String))
            imageDatabase.saveImage(image, id)
            saveToFile(targetFile, decodeImage(map["image"] as String))
            call.respondText(Gson().toJson(mapOf("id" to id.toString())))
        }
    }
}

private fun parseUpload(stream: InputStream): Map<*, *>? {
    val reader = BufferedReader(stream.reader())
    return Gson().fromJson(reader, Map::class.java)
}

private fun decodeImage(encoded: String): ByteArray {
    val urlDecoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
    return Base64.getDecoder().decode(urlDecoded)
}

private fun saveToFile(targetFile: File, byteArray: ByteArray) {
    val file = Files.createFile(targetFile.toPath())
    targetFile.writeBytes(byteArray)
}

data class UploadLimit(val address: String, val maxUploads: Int, val duration: Long) {

    private var nextReset: Long = System.currentTimeMillis() + duration
    private var uploadsLeft = maxUploads

    init {
        require(maxUploads > 0) { "maxUploads must be positive" }
        require(duration > 0) { "duration must be positive" }
        require(address.isNotEmpty()) { "address must not be empty" }
    }

    fun tryConsume(i: Int): Boolean {
        require(i > 0) { "tryConsume with parameter 0 is not allowed" }
        if (uploadsLeft < i) {
            if (nextReset < System.currentTimeMillis()) {
                nextReset = System.currentTimeMillis() + duration
                uploadsLeft = maxUploads
            }
        }
        uploadsLeft -= i
        return uploadsLeft > 0
    }
}