package dev.toppe.img.host

import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.post
import io.ktor.request.receiveStream
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import org.bson.types.ObjectId
import java.io.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


/**
 * Represents how much disk space we use (approximately)
 */
private var spaceUsed = Files.walk(Paths.get(uploadDir)).mapToLong { it.toFile().length() }.sum()
private var maxPostSize = 2000000

fun Route.upload(imageDatabase: ImageDatabase) {

    post<Upload> {
        // TODO: implement UploadLimit
        val input = call.receiveStream()
        val map = Gson().fromJson(InputStreamReader(input.buffered()), Map::class.java)
        val id = ObjectId()
        // TODO different extensions
        val fileName = "$id.png"
        val targetFile = File(uploadDir, fileName)
        val image = Image(targetFile.path, getInMillis(map["expiration"] as String))
        imageDatabase.saveImage(image, id)
        val byteArray = decodeImage(map["image"] as String)
        if(byteArray.size > maxPostSize) {
            call.respond(HttpStatusCode.BadRequest, "Post size limit exceeded")
        }
        if(!checkDiskSpace(byteArray.size.toLong())) {
            call.respond(HttpStatusCode.InsufficientStorage)
        }
        saveToFile(targetFile, byteArray)
        call.respondText(Gson().toJson(mapOf("id" to id.toString())))
    }
}

private fun checkDiskSpace(bytes: Long): Boolean {
    if (bytes == 0L) return true
    spaceUsed += bytes
    // In megabytes
    val max = System.getProperty("maxDiskUsage")?.toLongOrNull()
    return if (max == null) true else spaceUsed < max * 1024 * 1024
}

private fun decodeImage(encoded: String): ByteArray {
    val urlDecoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
    return Base64.getDecoder().decode(urlDecoded)
}

private fun saveToFile(targetFile: File, byteArray: ByteArray) {
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
        if (uploadsLeft < i && nextReset < System.currentTimeMillis()) {
            nextReset = System.currentTimeMillis() + duration
            uploadsLeft = maxUploads
        }
        uploadsLeft -= i
        return uploadsLeft > 0
    }
}