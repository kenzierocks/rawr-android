package net.octyl.rawr.android.data

import net.octyl.rawr.android.data.entity.RawrId
import net.octyl.rawr.android.util.expectedPaddedBase16
import org.junit.jupiter.api.DisplayName
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

@UseExperimental(ExperimentalUnsignedTypes::class)
class RawrConversionsTest {
    private val conversions = RawrConversions()

    private inline fun <T> assertEquals(expected: T, actualGen: RawrConversions.() -> T) {
        assertEquals(expected, conversions.actualGen())
    }

    private fun assertRawrIdToString(msb: Long, lsb: Long) {
        assertEquals(expectedPaddedBase16(msb.toULong(), lsb.toULong())) {
            RawrId(UUID(msb, lsb)).toPersistString()
        }
    }

    @Test
    @DisplayName("Can convert RawrId to String")
    internal fun rawrIdToString() {
        // most of this is tested in Base16Util, we just check that we're using it
        assertRawrIdToString(0, 0)
        assertRawrIdToString(0xA, 0)
        assertRawrIdToString(0xF, Long.MAX_VALUE)
        assertRawrIdToString(0xF, -1)
    }

    private fun assertStringToRawrId(msb: Long, lsb: Long) {
        assertEquals(RawrId(UUID(msb, lsb))) {
            expectedPaddedBase16(msb.toULong(), lsb.toULong()).toRawrId()
        }
    }

    @Test
    @DisplayName("Can convert String to RawrId")
    internal fun stringToRawrId() {
        // most of this is tested in Base16Util, we just check that we're using it
        assertStringToRawrId(0, 0)
        assertStringToRawrId(0xA, 0)
        assertStringToRawrId(0xF, Long.MAX_VALUE)
        assertStringToRawrId(0xF, -1)
    }
}