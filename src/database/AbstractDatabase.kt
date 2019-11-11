package dev.toppe.img.host.database

import com.google.gson.Gson
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.Dispatchers
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Aggregates.limit
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bson.types.ObjectId

abstract class AbstractDatabase {

    val writerSettings: JsonWriterSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()

    suspend fun saveObject(obj: Any, collection: String, objectId: ObjectId? = null): ObjectId {
        return withContext(Dispatchers.IO) {
            val json = Gson().toJson(obj)
            // Parse to bson document and insert
            val doc = Document.parse(json)
            doc["_id"] = objectId
            getDatabase().getCollection(collection).insertOne(doc)
            return@withContext doc.getObjectId("_id")
        }
    }

    suspend inline fun<reified T> findByProperty(property: String, value: Any, collection: String): T? {
        return withContext(Dispatchers.IO) {
            val query = BasicDBObject(property, value)
            val obj = getDatabase().getCollection(collection).find(query).first()
            return@withContext Gson().fromJson(obj?.toJson(writerSettings), T::class.java)
        }
    }

    suspend inline fun<reified T> findAllByProperty(property: String, value: Any, from: Int = 0, to: Int = 10, collection: String): List<T> {
        return withContext(Dispatchers.IO) {
            val query = BasicDBObject(property, value)
            return@withContext getDatabase().getCollection(collection).find(query).skip(from).limit(to-from).toList().map {
                Gson().fromJson(it.toJson(writerSettings), T::class.java)
            }
        }
    }

    abstract fun getDatabase(): MongoDatabase
}