package dev.toppe.img.host.database

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.bson.types.ObjectId
import java.io.IOException

class ObjectIdTypeAdapter : TypeAdapter<ObjectId>() {

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, value: ObjectId) {
        writer.beginObject()
            .name("\$oid")
            .value(value.toString())
            .endObject()
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): ObjectId {
        reader.beginObject()
        assert("\$oid" == reader.nextName())
        val objectId = reader.nextString()
        reader.endObject()
        return ObjectId(objectId)
    }
}
