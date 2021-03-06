package dev.toppe.img.host.database

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.bson.types.ObjectId

abstract class AbstractDatabase {

    val writerSettings: JsonWriterSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()
    abstract val database: MongoDatabase

    suspend fun saveObject(obj: Any, collection: String, objectId: ObjectId? = null): ObjectId {
        return withContext(Dispatchers.IO) {
            val json = createGson().toJson(obj)
            // Parse to bson document and insert
            val doc = Document.parse(json)
            doc["_id"] = objectId
            database.getCollection(collection).insertOne(doc)
            return@withContext doc.getObjectId("_id")
        }
    }

    suspend inline fun <reified T> findByProperty(property: String, value: Any, collection: String): T? {
        return withContext(Dispatchers.IO) {
            val query = BasicDBObject(property, value)
            val obj = database.getCollection(collection).find(query).first()
            return@withContext createGson().fromJson(obj?.toJson(writerSettings), T::class.java)
        }
    }

    suspend inline fun <reified T> findAllByProperty(
        property: String,
        value: Any,
        from: Int = 0,
        to: Int = 10,
        collection: String
    ): List<T> {
        return withContext(Dispatchers.IO) {
            val query = BasicDBObject(property, value)
            return@withContext database.getCollection(collection).find(query).skip(from).limit(to - from).toList()
                .map {
                    createGson().fromJson(it.toJson(writerSettings), T::class.java)
                }
        }
    }

    fun createGson(): Gson {
        return GsonBuilder().registerTypeAdapter(ObjectId::class.java, ObjectIdTypeAdapter()).create()
    }

}