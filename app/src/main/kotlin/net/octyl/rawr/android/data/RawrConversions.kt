package net.octyl.rawr.android.data

import androidx.room.TypeConverter
import net.octyl.rawr.android.data.entity.RawrId
import net.octyl.rawr.android.util.int128ToPaddedBase16
import net.octyl.rawr.android.util.paddedBase16ToInt128
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.util.UUID

@UseExperimental(ExperimentalUnsignedTypes::class)
class RawrConversions {
    companion object {
        private val MASK_64 = BigInteger.valueOf(ULong.MAX_VALUE.toLong())
    }

    @TypeConverter
    fun Duration.durationToLong(): Long = toMillis()

    @TypeConverter
    fun Long.longToDuration(): Duration = Duration.ofMillis(this)

    @TypeConverter
    fun Instant.toLong(): Long = toEpochMilli()

    @TypeConverter
    fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

    // Treat UUID as a 128-bit integer, store as base-16 string.
    // Always store full (128 bits/4-bits-per-char) = 32 chars
    @TypeConverter
    fun RawrId.toPersistString(): String {
        return int128ToPaddedBase16(
                low = uuid.leastSignificantBits.toULong(),
                high = uuid.mostSignificantBits.toULong())
    }

    @TypeConverter
    fun String.toRawrId(): RawrId {
        val (low, high) = paddedBase16ToInt128(this)
        return RawrId(UUID(high.toLong(), low.toLong()))
    }
}