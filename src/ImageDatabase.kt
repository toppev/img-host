package dev.toppe

import com.google.gson.Gson
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import org.bson.Document
import org.bson.types.ObjectId
import java.io.File


class ImageDatabase(mongoClient: MongoClient, database: String, uploadDir: File) {

    private val database = mongoClient.getDatabase(database)
    private val collection = "images"

    fun saveImage(image: Image, objectId: ObjectId? = null): ObjectId {
        val json = Gson().toJson(image)
        // Parse to bson document and insert
        val doc = Document.parse(json)
        doc["_id"] = objectId
        database.getCollection(collection).insertOne(doc)
        return doc.getObjectId("_id")
    }

    fun findImageById(id: Long): Image? {
        val query = BasicDBObject()
        query["_id"] = id
        val obj = database.getCollection(collection).find(query).first()
        return Gson().fromJson(obj?.toString(), Image::class.java)
    }
}