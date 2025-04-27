package pl.marcin.malocha.minivideojournalapp.app.navigation

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import pl.marcin.malocha.minivideojournalapp.domain.model.VideoEntity
import java.util.Date

object CustomNavType {
    private val json = Json {
        serializersModule = SerializersModule {
            contextual(Uri::class, UriSerializer)
            contextual(Date::class, DateSerializer)
        }
    }

    val VideoType = object : NavType<VideoEntity>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): VideoEntity? {
            return json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): VideoEntity {
            return json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: VideoEntity): String {
            return Uri.encode(json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: VideoEntity) {
            bundle.putString(key, json.encodeToString(value))
        }
    }

    private object UriSerializer : KSerializer<Uri> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Uri) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): Uri {
            return decoder.decodeString().toUri()
        }
    }

    private object DateSerializer : KSerializer<Date> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

        override fun serialize(encoder: Encoder, value: Date) {
            encoder.encodeLong(value.time)
        }

        override fun deserialize(decoder: Decoder): Date {
            return Date(decoder.decodeLong())
        }
    }
}