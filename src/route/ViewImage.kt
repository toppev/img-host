package dev.toppe.img.host.route

import dev.toppe.img.host.RawImage
import dev.toppe.img.host.ViewImage
import dev.toppe.img.host.database.ImageDatabase
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.LocalFileContent
import io.ktor.http.fromFilePath
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import org.bson.types.ObjectId
import java.io.File

fun Route.viewImage(imageDatabase: ImageDatabase) {

    get<ViewImage> {
        if (!ObjectId.isValid(it.id)) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            // Remove file extension if present
            val id = stripExtension(it.id)
            val img = imageDatabase.findImageById(id)
            if (img == null || !img.exists()) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(
                    FreeMarkerContent(
                        "image.ftl",
                        mapOf("image" to img)
                    )
                )
            }
        }
    }

    get<RawImage> { it ->
        if (!ObjectId.isValid(it.id)) {
            call.respond(HttpStatusCode.NotFound)
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