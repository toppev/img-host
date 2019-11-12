package dev.toppe.img.host

import dev.toppe.img.host.database.DatabaseManager
import dev.toppe.img.host.route.index
import dev.toppe.img.host.route.upload
import dev.toppe.img.host.route.userImages
import dev.toppe.img.host.route.viewImage
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.routing
import java.io.File
import java.io.IOException


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Location("/{id}")
data class ViewImage(val id: String)

@Location("/img/{id}")
class RawImage(val id: String)

@Location("/uploader/{id}")
class UserImages(val id: String)

@Location("/api/upload")
class Upload

@Location("/")
class Index

const val uploadDir = "./uploads"


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {
    // Automatic headers to each response
    install(DefaultHeaders)
    // Log every call (request/response)
    install(CallLogging)
    install(Locations)
    // Automatic '304 Not Modified' responses
    install(ConditionalHeaders)
    install(Compression) {
        default()
        excludeContentType(ContentType.Video.Any)
    }

    // Load templates
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val uploadFile = File(uploadDir)
    if (!uploadFile.mkdirs() && !uploadFile.exists()) {
        throw IOException("Failed to create directory ${uploadFile.absolutePath}")
    }

    val dbManager = DatabaseManager()

    val imageDatabase = dbManager.createImageDatabase()
    val usersDatabase = dbManager.createUsersDatabase()

    routing {
        upload(imageDatabase)
        viewImage(imageDatabase)
        userImages(usersDatabase, imageDatabase)

        index()

        static("static") {
            resources("static")
        }
    }

}

