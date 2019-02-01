package net.octyl.rawr.android.util

/**
 * 32 zeroes for filling in.
 */
private val BASE_16_ZEROES = (0 until 32).joinToString("") { "0" }

/**
 * Convert a 128-bit integer to base 16 that is always 32 characters long.
 */
@ExperimentalUnsignedTypes
fun int128ToPaddedBase16(low: ULong, high: ULong): String {
    val output = BASE_16_ZEROES.toCharArray()
    high.emitBase16(output, 0)
    low.emitBase16(output, 16)
    return String(output)
}

private const val MASK_8 = 0xFF
private val BASE_16_ALPHA = CharArray(16) { it.toString(16)[0] }
@ExperimentalUnsignedTypes
private fun ULong.emitBase16(output: CharArray, start: Int) {
    (0 until 8).forEach { charIdx ->
        val outIdx = charIdx * 2 + start
        val byteIdx = 8 - charIdx - 1
        val value = (this shr (byteIdx * 8)).toInt() and MASK_8
        output[outIdx] = BASE_16_ALPHA[(value shr 4) and 0xF]
        output[outIdx + 1] = BASE_16_ALPHA[value and 0xF]
    }
}

/**
 * @return a pair of unsigned longs, `(low to high)`
 */
@ExperimentalUnsignedTypes
fun paddedBase16ToInt128(base16: String): Pair<ULong, ULong> {
    return base16.readBase16Long(16) to base16.readBase16Long(0)
}

@ExperimentalUnsignedTypes
private fun String.readBase16Long(start: Int): ULong {
    var out = 0uL
    (0 until 8).forEach { charIdx ->
        val byteIdx = 8 - charIdx - 1
        val msc = this[charIdx * 2 + start]
        val lsc = this[charIdx * 2 + start + 1]
        val newByte = (msc.base16ToInt() shl 4) or lsc.base16ToInt()
        out = out or (newByte.toULong() shl (byteIdx * 8))
    }
    return out
}

private fun Char.base16ToInt(): Int = when (this) {
    in '0'..'9' -> this - '0'
    in 'a'..'f' -> this - 'a' + 10
    in 'A'..'F' -> this - 'A' + 10
    else -> throw IllegalArgumentException("Out of range character for base 16: " + this)
}