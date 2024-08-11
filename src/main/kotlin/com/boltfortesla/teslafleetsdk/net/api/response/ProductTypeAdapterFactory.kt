package com.boltfortesla.teslafleetsdk.net.api.response

import com.boltfortesla.teslafleetsdk.net.api.response.Product.EnergySite
import com.boltfortesla.teslafleetsdk.net.api.response.Product.Vehicle
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlin.reflect.KClass

/** Gson [TypeAdapterFactory] for converting JSON to [Product] instances. */
internal class ProductTypeAdapterFactory : TypeAdapterFactory {

  private val subclasses = listOf(Vehicle::class, EnergySite::class)

  override fun <R : Any> create(gson: Gson, type: TypeToken<R>?): TypeAdapter<R>? {
    if (
      type == null ||
        subclasses.isEmpty() ||
        subclasses.none { type.rawType.isAssignableFrom(it.java) }
    )
      return null

    val elementTypeAdapter = gson.getAdapter(JsonElement::class.java)
    val subclassToDelegate: Map<KClass<*>, TypeAdapter<*>> =
      subclasses.associateWith { gson.getDelegateAdapter(this, TypeToken.get(it.java)) }
    return object : TypeAdapter<R>() {
      override fun write(writer: JsonWriter, value: R) {
        throw UnsupportedOperationException()
      }

      override fun read(reader: JsonReader): R {
        val element = elementTypeAdapter.read(reader)
        val isVehicle = element.asJsonObject.has("vin")

        val kClass = if (isVehicle) Vehicle::class else EnergySite::class

        @Suppress("UNCHECKED_CAST") return subclassToDelegate[kClass]!!.fromJsonTree(element) as R
      }
    }
  }
}
