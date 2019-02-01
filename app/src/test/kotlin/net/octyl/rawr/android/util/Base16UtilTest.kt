package net.octyl.rawr.android.util

import org.junit.jupiter.api.DisplayName
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.Collectors.toSet
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
fun expectedPaddedBase16(numberHigh: ULong, numberLow: ULong) =
        String.format("%016x%016x", numberHigh.toLong(), numberLow.toLong())

@UseExperimental(ExperimentalUnsignedTypes::class)
class Base16UtilTest {

    private fun assertTo16(numberHigh: ULong, numberLow: ULong) {
        val paddedExp = expectedPaddedBase16(numberHigh, numberLow)
        assertEquals(paddedExp, int128ToPaddedBase16(numberLow, numberHigh))
    }

    private fun assertFrom16(numberHigh: ULong, numberLow: ULong) {
        val paddedExp = expectedPaddedBase16(numberHigh, numberLow)
        assertEquals(numberLow to numberHigh, paddedBase16ToInt128(paddedExp))
    }

    // edge cases + fuzz
    private val options = setOf(
            ULong.MIN_VALUE, 1uL, 0xAuL, 0xFuL, 0xFFuL, 0x1FFuL, ULong.MAX_VALUE
    ) + fuzz()

    private fun fuzz(): Set<ULong> {
        val rng = ThreadLocalRandom.current()
        // make it reproducible...
        val seed = rng.nextLong()
        println("Fuzz seed: $seed")
        return rng.longs(1000).mapToObj(Long::toULong).collect(toSet())
    }

    @Test
    @DisplayName("can convert unsigned 128-bit ints to base 16")
    internal fun toBase16() {
        options.forEach { high ->
            options.forEach { low ->
                assertTo16(high, low)
            }
        }
    }

    @Test
    @DisplayName("can convert base 16 to unsigned 128-bit ints")
    internal fun fromBase16() {
        options.forEach { high ->
            options.forEach { low ->
                assertFrom16(high, low)
            }
        }
    }
}