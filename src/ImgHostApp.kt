package dev.toppe.img.host

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
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.routing
import kotlinx.css.Position
import java.io.File
import java.io.IOException
import java.util.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Location("/{id}")
data class ViewImage(val id: String)

@Location("/img/{id}")
class RawImage(val id: String)

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
    val imageDatabase = loadDatabase()

    routing {
        upload(imageDatabase)
        viewImage(imageDatabase)
                // styles()
        index()

        static("static") {
            resources("static")
        }
    }
}

fun loadDatabase(): ImageDatabase {
    val prop = Properties()
    val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("database.properties")
    prop.load(stream)
    val database = prop.getProperty("database")
    val user = prop.getProperty("user", null)
    val pass = prop.getProperty("password", null)
    val settings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString("mongodb://${prop.getProperty("url")}:27017"))
    // Credentials are optional
    if (user != null && pass != null) {
        settings.credential(MongoCredential.createCredential(user, database, pass.toCharArray()))
    }
    return ImageDatabase(
        MongoClients.create(settings.build()),
        database,
        File(uploadDir)
    )
}

