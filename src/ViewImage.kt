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
        // Remove file extension if present
        if (!validIdString(it.id)) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val id = if (it.id.contains(".")) it.id.substring(0, it.id.lastIndexOf('.')) else it.id
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
                imageDatabase.updateOnView(img, id)
            }
        }
    }

    get<RawImage> { it ->
        val path = uploadDir + File.separator + it.id
        val file = File(path)
        // Just to be sure
        if (!validIdString(it.id)) {
            call.respond(HttpStatusCode.BadRequest)
        } else if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(
                LocalFileContent(
                    file,
                    contentType = ContentType.fromFilePath(path).first { it.contentType == "image" })
            )
        }
    }
}

private fun validIdString(id: String): Boolean {
    return id.matches("[A-Za-z0-9.]+".toRegex())
}