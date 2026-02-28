package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class ArrayByteBufferTest {
    @Test
    fun testWriteReadPrimitives() {
        val buffer = ArrayByteBuffer(1)
        buffer.writeByte(0x01)
        buffer.writeInt(0x02030405)
        buffer.writeLong(0x060708090A0B0C0DL)

        assertEquals(1, buffer.readByte().toInt())
        assertEquals(0x02030405, buffer.readInt())
        assertEquals(0x060708090A0B0C0DL, buffer.readLong())
        assertFalse(buffer.readable)
    }

    @Test
    fun testReadWriteArrayAndBuffer() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2, 3, 4))

        val target = ByteArray(2)
        buffer.readToArray(target, 0, 2)
        assertContentEquals(byteArrayOf(1, 2), target)

        val other = ArrayByteBuffer()
        buffer.readToBuffer(other, 2)
        assertEquals(2, other.readableSize)
        assertEquals(3, other.readByte().toInt())
        assertEquals(4, other.readByte().toInt())
    }

    @Test
    fun testSkipAndBackRead() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(10, 11, 12))
        buffer.skipRead(2)
        assertEquals(12, buffer.readByte().toInt())

        buffer.backRead(1)
        assertEquals(12, buffer.readByte().toInt())
    }

    @Test
    fun testBackReadErrors() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2))
        buffer.readByte()

        assertFailsWith<BufferUnderflowException> { buffer.backRead(2) }
        assertFailsWith<BufferUnderflowException> { buffer.backRead(-1) }
    }

    @Test
    fun testIsReadableNegative() {
        val buffer = ArrayByteBuffer()
        assertFalse(buffer.isReadable(-1))
    }

    @Test
    fun testEnsureWriteNegative() {
        val buffer = ArrayByteBuffer()
        assertFailsWith<IllegalArgumentException> { buffer.ensureWrite(-1) }
    }

    @Test
    fun testFlushCompactsOrClears() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2, 3, 4))
        buffer.readByte() // consume 1
        buffer.readByte() // consume 2

        buffer.flush()
        assertEquals(0, buffer.readerIndex)
        assertEquals(2, buffer.writerIndex)
        assertEquals(3, buffer.readByte().toInt())
        assertEquals(4, buffer.readByte().toInt())

        buffer.flush()
        assertEquals(0, buffer.readerIndex)
        assertEquals(0, buffer.writerIndex)
    }

    @Test
    fun testArrayViewAndArraySetter() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2, 3))

        val view = buffer.arrayView
        assertNotNull(view)
        assertEquals(0, view.readerIndex)
        assertEquals(3, view.writerIndex)

        buffer.array = byteArrayOf(9, 8)
        assertEquals(0, buffer.readerIndex)
        assertEquals(2, buffer.writerIndex)
        assertEquals(9, buffer.readByte().toInt())
        assertEquals(8, buffer.readByte().toInt())
    }

    @Test
    fun testWriteBufferFromInput() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(5, 6, 7))

        val target = ArrayByteBuffer()
        target.writeBuffer(source)

        assertEquals(0, source.readableSize)
        assertEquals(3, target.readableSize)
        assertEquals(5, target.readByte().toInt())
        assertEquals(6, target.readByte().toInt())
        assertEquals(7, target.readByte().toInt())
    }

    @Test
    fun testAutoResizeOnWrite() {
        val buffer = ArrayByteBuffer(1)
        buffer.writeArray(ByteArray(10) { it.toByte() })
        val view = buffer.arrayView
        assertNotNull(view)
        assertTrue(view.array.size >= 10)
        assertEquals(10, buffer.readableSize)
    }
}
