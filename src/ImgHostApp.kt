package dev.toppe

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.client.MongoClients
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.http.ContentType
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.routing
import java.io.File
import java.io.IOException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Location("/img/{id}")
data class ViewImage(val id: Long)

@Location("/upload")
class Upload

private val user = "root"
private val database = "uploads"
private val password = "root"
private val credential = MongoCredential.createCredential(user, database, password.toCharArray())
private val connectionString = ConnectionString("mongodb://localhost:27017")
private val settings = MongoClientSettings.builder()
    .applyConnectionString(connectionString).build()

private val uploadDir = "uploads"

private val imageDatabase = ImageDatabase(
    MongoClients.create(settings),
    database,
    File(uploadDir)
)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {
    // Automatic headers to each response
    install(DefaultHeaders)
    // Log every call (request/response)
    install(CallLogging)
    install(Locations)
    // Automatic '304 Not Modified' Responses
    install(ConditionalHeaders)
    install(Compression) {
        default()
        excludeContentType(ContentType.Video.Any)
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val uploadDir = File(uploadDir)
    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }
    routing {
        upload(imageDatabase, uploadDir)
        viewImage(imageDatabase)
        styles()
    }
}

