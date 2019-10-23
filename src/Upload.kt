package dev.toppe.img.host

import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.locations.post
import io.ktor.request.receiveStream
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        // This possibly can be spoofed if not behind a reverse proxy (?)
        val address = call.request.origin.remoteHost
        // TODO: implement UploadLimit
        val input = call.receiveStream()
        val map = Gson().fromJson(InputStreamReader(input.buffered()), Map::class.java)
        val id = ObjectId()
        // TODO different extensions
        val fileName = "$id.png"
        val targetFile = File(uploadDir, fileName)
        val image = Image(targetFile.path, getInMillis(map["expiration"] as String), address)

        imageDatabase.saveImage(image, id)
        val imgStr = map["image"] as String
        val len = imgStr.length.toLong()
        if(len > maxPostSize) {
            call.respond(HttpStatusCode.BadRequest, "Post size limit exceeded")
        }
        if (!checkDiskSpace(len)) {
            call.respond(HttpStatusCode.InsufficientStorage)
        }
        call.respondText(Gson().toJson(mapOf("id" to id.toString())))
        saveToFile(targetFile, decodeImage(imgStr))
    }
}

private fun checkDiskSpace(bytes: Long): Boolean {
    if (bytes == 0L) return true
    spaceUsed += bytes
    // In megabytes
    val max = System.getProperty("maxDiskUsage")?.toLongOrNull()
    return if (max == null) true else spaceUsed < max * 1024 * 1024
}

private suspend fun decodeImage(encoded: String): ByteArray {
    return withContext(Dispatchers.Unconfined) {
        val urlDecoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name())
        return@withContext Base64.getDecoder().decode(urlDecoded)
    }
}

private suspend fun saveToFile(targetFile: File, byteArray: ByteArray) {
    withContext(Dispatchers.IO) {
        targetFile.writeBytes(byteArray)
    }
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