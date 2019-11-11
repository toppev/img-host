package dev.toppe.img.host

import dev.toppe.img.host.image.ImageDatabase
import dev.toppe.img.host.user.UserDatabase
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.async

fun Route.userImages(
    usersDatabase: UserDatabase,
    imageDatabase: ImageDatabase
) {

    get<UserImages> {
        if (!validIdString(it.token)) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            val images = async {imageDatabase.findImagesByToken(it.token)}
            val user = async {usersDatabase.findUserByToken(it.token)}

        }
    }
}