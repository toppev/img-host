package dev.toppe.img.host

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.LocalFileContent
import io.ktor.http.fromFilePath
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun Route.viewImage(imageDatabase: ImageDatabase) {

    get<ViewImage> {
        if (!validIdString(it.id)) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            // Remove file extension if present
            val id = stripExtension(it.id)
            val img = imageDatabase.findImageById(id)
            if (img == null || !img.exists()) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val created = SimpleDateFormat().format(Date(img.created))
                val expires =
                    if (img.expires()) SimpleDateFormat().format(Date(System.currentTimeMillis() + img.expiration!!)) else "never"
                val lastViewed =
                    if (img.lastViewed != null) SimpleDateFormat().format(Date(img.lastViewed!!)) else "never"
                call.respond(
                    FreeMarkerContent(
                        "image.ftl",
                        mapOf(
                            "image" to img,
                            "id" to id,
                            "created" to created,
                            "expires" to expires,
                            "lastViewed" to lastViewed
                        )
                    )
                )
            }
        }
    }

    get<RawImage> { it ->
        if (!validIdString(it.id)) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            // Remove file extension if present
            val id = stripExtension(it.id)
            val img = imageDatabase.findImageById(id)
            if (img == null || !img.exists()) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val file = File(img.path)
                if (!file.exists()) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(
                        LocalFileContent(
                            file,
                            contentType = ContentType.fromFilePath(file.path).first { it.contentType == "image" })
                    )
                    imageDatabase.updateOnView(img, id)
                }
            }
        }
    }
}

private fun stripExtension(str: String): String {
    return if (str.contains(".")) str.substring(0, str.lastIndexOf('.')) else str
}

private fun validIdString(id: String): Boolean {
    return id.matches("[A-Za-z0-9.]+".toRegex())
}