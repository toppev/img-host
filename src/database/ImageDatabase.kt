package dev.toppe.img.host.database

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import dev.toppe.img.host.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import java.io.File


class ImageDatabase(mongoClient: MongoClient, database: String, uploadDir: File) : AbstractDatabase() {

    private val database = mongoClient.getDatabase(database)
    private val collection = "images"


    override fun getDatabase(): MongoDatabase {
        return database
    }

    suspend fun saveImage(image: Image, objectId: ObjectId? = null): ObjectId {
        if (objectId != null) {
            image._id = objectId
        }
        return super.saveObject(image, collection, objectId)
    }

    suspend fun findImageById(id: String): Image? {
        return super.findByProperty("_id", ObjectId(id), collection)
    }

    suspend fun findImagesByUserId(id: String, from: Int = 0, to: Int = 10): List<Image>? {
        return super.findAllByProperty("user", ObjectId(id), from, to, collection)
    }

    suspend fun findImagesByToken(token: String, from: Int = 0, to: Int = 10): List<Image>? {
        return super.findAllByProperty("token", token, from, to, collection)
    }

    /**
     * Updates the database, adds one to "views" and updates "lastSeen".
     * NOTE: Does not change the given image object, only updates the database
     */
    suspend fun updateOnView(image: Image, id: String) {
        withContext(Dispatchers.IO) {
            val updateFields = BasicDBObject()
            updateFields.append("views", image.views + 1)
            updateFields.append("lastViewed", System.currentTimeMillis())
            val searchQuery = BasicDBObject("_id", ObjectId(id))
            database.getCollection(collection).updateOne(searchQuery, BasicDBObject("\$set", updateFields))
        }
    }
}