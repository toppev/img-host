package dev.toppe

import io.ktor.application.call
import io.ktor.http.content.resolveResource
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

@Location("/styles/main.css")
class MainCss

fun Route.styles() {

    get<MainCss> {
        call.respond(call.resolveResource("body {\n  background-color: #516794;\n  font-family: 'Open Sans', sans-serif;\n}\n.release {\n  margin: 60px 20px 45px;\n}\n#shortcuts {\n  margin-top: 40px;\n  margin-bottom: 40px;\n}\n.link {\n  color: lightgrey;\n}\n.desc {\n  margin-top: 55px;\n  margin-bottom: 45px;\n}")!!)
    }
}