package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ByteArrayExtTest {
    @Test
    fun testPutGetIntRoundtrip() {
        val value = 0x7F12A3B4
        val bytes = ByteArray(4)
        bytes.putInt(0, value)
        assertEquals(value, bytes.getInt(0))
    }

    @Test
    fun testPutGetIntByteOrder() {
        val bytes = ByteArray(4)
        bytes.putInt(0, 0x01020304)
        assertEquals(0x01, bytes[0].toInt() and 0xFF)
        assertEquals(0x02, bytes[1].toInt() and 0xFF)
        assertEquals(0x03, bytes[2].toInt() and 0xFF)
        assertEquals(0x04, bytes[3].toInt() and 0xFF)
    }

    @Test
    fun testPutGetLongRoundtrip() {
        val value = 0x7F12A3B4C5D6E7F8L
        val bytes = ByteArray(8)
        bytes.putLong(0, value)
        assertEquals(value, bytes.getLong(0))
    }

    @Test
    fun testGetLongSignBitHandling() {
        val value = 0xFF00A3B4C5D6E7F8uL.toLong()
        val bytes = ByteArray(8)
        bytes.putLong(0, value)
        assertEquals(value, bytes.getLong(0))
    }

    @Test
    fun testPutGetByteArray() {
        val src = byteArrayOf(1, 2, 3, 4)
        val target = ByteArray(6)
        target.putByteArray(1, src)
        assertContentEquals(byteArrayOf(0, 1, 2, 3, 4, 0), target)
        assertContentEquals(src, target.getByteArray(1, 4))
    }
}
