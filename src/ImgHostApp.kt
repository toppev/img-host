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
import io.ktor.http.content.*
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

@Location("/api/upload")
class Upload

@Location("/")
class Index

private val user = "root"
private val database = "uploads"
private val password = "root"
private val credential = MongoCredential.createCredential(user, database, password.toCharArray())
private val connectionString = ConnectionString("mongodb://localhost:27017")
private val settings = MongoClientSettings.builder()
    .applyConnectionString(connectionString).build()

val uploadDir = "./uploads"

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
    routing {
        upload(imageDatabase)
        viewImage(imageDatabase)
        styles()
        index()

        static("static") {
            resources("static")
        }
    }
}

