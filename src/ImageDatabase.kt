package dev.toppe.img.host

import com.google.gson.Gson
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bson.types.ObjectId
import java.io.File


class ImageDatabase(mongoClient: MongoClient, database: String, uploadDir: File) {

    private val database = mongoClient.getDatabase(database)
    private val collection = "images"

    suspend fun saveImage(image: Image, objectId: ObjectId? = null): ObjectId {
        return withContext(Dispatchers.IO) {
            val json = Gson().toJson(image)
            // Parse to bson document and insert
            val doc = Document.parse(json)
            doc["_id"] = objectId
            database.getCollection(collection).insertOne(doc)
            return@withContext doc.getObjectId("_id")
        }
    }

    suspend fun findImageById(id: String): Image? {
        return withContext(Dispatchers.IO) {
            val query = BasicDBObject("_id", ObjectId(id))
            val obj = database.getCollection(collection).find(query).first()
            var writerSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()
            return@withContext Gson().fromJson(obj?.toJson(writerSettings), Image::class.java)
        }
    }

    /**
     * Updates the database, adds one to "views" and updates "lastSeen".
     * NOTE: Does not change the given image object, only updates the database
     */
    suspend fun updateOnView(image: Image, id: String){
        withContext(Dispatchers.IO) {
            val updateFields = BasicDBObject()
            updateFields.append("views", image.views+1)
            updateFields.append("lastViewed", System.currentTimeMillis())
            val searchQuery = BasicDBObject("_id", ObjectId(id))
            database.getCollection(collection).updateOne(searchQuery, BasicDBObject("\$set", updateFields))
        }
    }
}