package dev.toppe.img.host.route

import dev.toppe.img.host.Index
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

fun Route.index() {

    get<Index> {
        // TODO: Change map later
        call.respond(FreeMarkerContent("index.ftl", kotlinx.html.emptyMap))
    }

}