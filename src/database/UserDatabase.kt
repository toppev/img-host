package dev.toppe.img.host.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import dev.toppe.img.host.User
import org.bson.types.ObjectId


class UserDatabase(mongoClient: MongoClient, database: String) : AbstractDatabase() {

    override val database: MongoDatabase = mongoClient.getDatabase(database)
    private val collection = "users"

    suspend fun saveUser(user: User, objectId: ObjectId? = null): ObjectId {
        return super.saveObject(user, collection, objectId)
    }

    suspend fun findUserById(id: String): User? {
        return super.findByProperty("_id", id, collection)
    }

    suspend fun findUserByToken(token: String): User? {
        return super.findByProperty("token", token, collection)
    }


}