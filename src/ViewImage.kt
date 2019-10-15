package dev.toppe

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import java.text.SimpleDateFormat
import java.util.*

fun Route.viewImage(imageDatabase: ImageDatabase) {

    get<ViewImage> {
        val img = imageDatabase.findImageById(it.id)
        if (img == null || !img.exists()) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val created = SimpleDateFormat().format(Date(img.created))
            val expires =
                if (img.expires()) SimpleDateFormat().format(Date(System.currentTimeMillis() + img.expiration!!)) else "never"
            call.respond(
                FreeMarkerContent(
                    "image.ftl",
                    mapOf("image" to img, "created" to created, "expires" to expires)
                )
            )
        }
    }
}