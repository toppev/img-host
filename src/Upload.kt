package dev.toppe

import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.locations.get
import io.ktor.request.receiveStream
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import java.io.BufferedReader
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Route.upload(imageDatabase: ImageDatabase, uploadDir: File) {

    post("/upload") {
        // TODO: implement UploadLimit
        val reader = BufferedReader(call.receiveStream().reader())
        val obj = Gson().fromJson(reader, Map::class.java)
        val imageBase64 = obj["image"] as String
        val fileName = Random().nextInt(1000).toString()
        val targetFile = File(uploadDir, fileName)
        val image = Image(targetFile.name, getInMillis(obj["expiration"] as String))
        imageDatabase.saveImage(image)
        val file = Files.createFile(targetFile.toPath())
        val urlDecoded = URLDecoder.decode(imageBase64, StandardCharsets.UTF_8.name())
        val imageByteArray = Base64.getDecoder().decode(urlDecoded)
        targetFile.writeBytes(imageByteArray)
        call.respondText(Gson().toJson(mapOf("id" to fileName)))
    }

    get<Upload> {
        call.respondText("HELLO POSTER!", contentType = ContentType.Text.Plain)
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