package dev.toppe.img.host.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.client.MongoClients
import dev.toppe.img.host.image.ImageDatabase
import dev.toppe.img.host.uploadDir
import dev.toppe.img.host.user.UserDatabase
import java.io.File
import java.io.InputStream
import java.util.*


class DatabaseManager(val propertiesStream: InputStream? = null) {

    private val properties = Properties()
    private val settings = getDatabaseSettings()

    init {
        val stream = propertiesStream ?: Thread.currentThread().contextClassLoader.getResourceAsStream("database.properties")
        properties.load(stream)
    }

    private fun getDatabaseSettings(): MongoClientSettings {
        val database = properties.getProperty("database")
        val user = properties.getProperty("user", null)
        val pass = properties.getProperty("password", null)
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString("mongodb://${properties.getProperty("url")}:27017"))
        // Credentials are optional
        if (user != null && pass != null) {
            settings.credential(MongoCredential.createCredential(user, database, pass.toCharArray()))
        }
        return settings.build()
    }

    fun createImageDatabase(): ImageDatabase {
        return ImageDatabase(
            MongoClients.create(settings),
            properties.getProperty("image-database"),
            File(uploadDir)
        )
    }

    fun createUsersDatabase(): UserDatabase {
        return UserDatabase(
            MongoClients.create(settings),
            properties.getProperty("users-database")
        )
    }

}