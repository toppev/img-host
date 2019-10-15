package dev.toppe

import com.google.gson.Gson
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import org.bson.Document
import java.io.File


class ImageDatabase(mongoClient: MongoClient, database: String, uploadDir: File) {

    private val database = mongoClient.getDatabase(database)
    private val collection = "images"

    fun saveImage(image: Image) {
        val json = Gson().toJson(image)
        // Parse to bson document and insert
        database.getCollection(collection).insertOne(Document.parse(json))
    }

    fun findImageById(id: Long): Image? {
        val query = BasicDBObject()
        query["_id"] = id
        val obj = database.getCollection(collection).find(query).first()
        return Gson().fromJson(obj?.toString(), Image::class.java)
    }
}