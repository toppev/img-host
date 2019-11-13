package dev.toppe.img.host.route

import dev.toppe.img.host.UserImages
import dev.toppe.img.host.database.ImageDatabase
import dev.toppe.img.host.database.UserDatabase
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.async
import org.bson.types.ObjectId

fun Route.userImages(
    usersDatabase: UserDatabase,
    imageDatabase: ImageDatabase
) {

    get<UserImages> {
        if (!validIdString(it.id)) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val valid = ObjectId.isValid(it.id)
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val from = page*10
            val to = from+10
            val userImages = async { if (valid) imageDatabase.findImagesByUserId(it.id, from, to) else null }
            val user = async { if (valid) usersDatabase.findUserById(it.id) else null }
            val tokenImages = async { imageDatabase.findImagesByToken(it.id, from, to) }
            val imagesList = tokenImages.await().orEmpty() + tokenImages.await().orEmpty()
            // TODO: Better (error) handling
            if (imagesList.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "no images found :(")
            } else {
                call.respond(
                    FreeMarkerContent(
                        "userimages.ftl",
                        mapOf(
                            "images" to imagesList,
                            "user" to user,
                            "page" to page
                        )
                    )
                )
            }
        }
    }
}