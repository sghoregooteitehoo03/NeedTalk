package com.sghore.needtalk.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ImageBitmapSerializer : KSerializer<ImageBitmap> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ImageBitmap", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ImageBitmap) {
        val byteArray = bitmapToByteArray(value.asAndroidBitmap())
        encoder.encodeString(
            android.util.Base64.encodeToString(
                byteArray,
                android.util.Base64.DEFAULT
            )
        )
    }

    override fun deserialize(decoder: Decoder): ImageBitmap {
        val byteArray =
            android.util.Base64.decode(decoder.decodeString(), android.util.Base64.DEFAULT)
        return byteArrayToBitmap(byteArray).asImageBitmap()
    }
}
