package dev.toppe.img.host.user

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import dev.toppe.img.host.database.AbstractDatabase
import org.bson.types.ObjectId
import java.io.File


class UserDatabase(mongoClient: MongoClient, database: String) : AbstractDatabase() {

    private val database = mongoClient.getDatabase(database)
    private val collection = "users"


    override fun getDatabase(): MongoDatabase {
        return database;
    }

    suspend fun saveUser(user: User, objectId: ObjectId? = null): ObjectId {
        return super.saveObject(user, collection, objectId);
    }

    suspend fun findUserById(id: String): User? {
        return super.findByProperty("_id", ObjectId(id), collection)
    }

    suspend fun findUserByToken(id: String): User? {
        return super.findByProperty("token", ObjectId(id), collection)
    }


}