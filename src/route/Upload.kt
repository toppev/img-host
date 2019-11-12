package dev.toppe.img.host.route

import com.google.gson.Gson
import dev.toppe.img.host.Upload
import dev.toppe.img.host.Image
import dev.toppe.img.host.database.ImageDatabase
import dev.toppe.img.host.getInMillis
import dev.toppe.img.host.uploadDir
import io.ktor.application.call
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
        // TODO: implement UploadLimit
        val input = call.receiveStream()
        val map = Gson().fromJson(InputStreamReader(input.buffered()), Map::class.java)
        val id = ObjectId()
        // TODO different extensions
        val fileName = "$id.png"
        val targetFile = File(uploadDir, fileName)
        val image = Image(targetFile.path, getInMillis(map["expiration"] as String))
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