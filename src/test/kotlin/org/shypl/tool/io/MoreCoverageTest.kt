package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MoreCoverageTest {
    @Test
    fun testInputByteBufferReadArrayExtension() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2, 3, 4))

        val first = buffer.readArray(2)
        assertContentEquals(byteArrayOf(1, 2), first)
        assertEquals(2, buffer.readableSize)

        val rest = buffer.readArray()
        assertContentEquals(byteArrayOf(3, 4), rest)
        assertFalse(buffer.readable)
    }

    @Test
    fun testArrayByteBufferReadWriteEdgeCases() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(9, 8, 7, 6), 1, 2)
        assertEquals(2, buffer.readableSize)
        assertContentEquals(byteArrayOf(8, 7), buffer.readArray())

        buffer.writeArray(byteArrayOf(1, 2, 3))
        val before = buffer.readableSize
        buffer.readToArray(ByteArray(0), 0, 0)
        buffer.readToBuffer(DummyOutputByteBuffer, 0)
        assertEquals(before, buffer.readableSize)

        buffer.skipWrite(3)
        assertEquals(before + 3, buffer.readableSize)
        assertEquals(1, buffer.readByte().toInt())
        assertEquals(2, buffer.readByte().toInt())
        assertEquals(3, buffer.readByte().toInt())
        assertEquals(0, buffer.readByte().toInt())
        assertEquals(0, buffer.readByte().toInt())
        assertEquals(0, buffer.readByte().toInt())
    }

    @Test
    fun testArrayByteBufferEnsureWriteClearsWhenFullyRead() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2))
        buffer.readByte()
        buffer.readByte()

        buffer.ensureWrite(1)
        assertEquals(0, buffer.readerIndex)
        assertEquals(0, buffer.writerIndex)
        assertFalse(buffer.readable)
    }

    @Test
    fun testDummyInputByteBufferZeroSizeOpsAreNoops() {
        DummyInputByteBuffer.readToArray(ByteArray(0), 0, 0)
        DummyInputByteBuffer.readToBuffer(DummyOutputByteBuffer, 0)
        DummyInputByteBuffer.skipRead(0)
        DummyInputByteBuffer.backRead(0)
        assertNotNull(DummyInputByteBuffer.arrayView)
        assertTrue(DummyInputByteBuffer.isReadable(0))
    }

    @Test
    fun testBufferUnderflowExceptionMessages() {
        val negative = BufferUnderflowException(-5)
        assertEquals("Reading size is negative (-5)", negative.message)

        val negativeBack = BufferUnderflowException(-2, true)
        assertEquals("Back reading size is negative (-2)", negativeBack.message)

        val notEnough = BufferUnderflowException(4, 2)
        assertEquals("Not enough data to read (requested: 4, available: 2)", notEnough.message)

        val notEnoughBack = BufferUnderflowException(3, 1, true)
        assertEquals("Not enough data to back read (requested: 3, available: 1)", notEnoughBack.message)
    }
}
