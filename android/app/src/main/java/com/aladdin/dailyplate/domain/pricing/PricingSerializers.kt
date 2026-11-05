package com.aladdin.dailyplate.domain.pricing

import java.math.BigDecimal
import java.time.Duration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Strings preserve decimal precision/scale and ISO duration without display rounding. */
object DecimalStringSerializer : KSerializer<BigDecimal> {
    override val descriptor = PrimitiveSerialDescriptor("DecimalString", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder) = BigDecimal(decoder.decodeString())
}

object DurationStringSerializer : KSerializer<Duration> {
    override val descriptor = PrimitiveSerialDescriptor("DurationString", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Duration) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Duration = Duration.parse(decoder.decodeString())
}
