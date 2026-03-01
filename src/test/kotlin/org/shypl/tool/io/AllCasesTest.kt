package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AllCasesTest {
    @Test
    fun testArrayByteBufferCompanionFactories() {
        val fromBlock = ArrayByteBuffer {
            writeByte(0x11)
            writeByte(0x22)
        }
        assertContentEquals(byteArrayOf(0x11, 0x22), fromBlock.readArray())

        val withCapacity = ArrayByteBuffer(0) {
            writeInt(0x01020304)
        }
        assertEquals(0x01020304, withCapacity.readInt())

        val source = ArrayByteBuffer().apply {
            writeArray(byteArrayOf(7, 8, 9))
        }
        val fromInput = ArrayByteBuffer(source as InputByteBuffer)
        assertEquals(0, source.readableSize)
        assertContentEquals(byteArrayOf(7, 8, 9), fromInput.readArray())

        val fromBytes = ArrayByteBuffer(byteArrayOf(4, 5))
        assertContentEquals(byteArrayOf(4, 5), fromBytes.readArray())
    }

    @Test
    fun testArrayByteBufferZeroSizeBranchesAndFlushNoop() {
        val buffer = ArrayByteBuffer(0)
        assertTrue(buffer.isReadable(0))
        assertFalse(buffer.isReadable(1))

        buffer.writeArray(byteArrayOf(1, 2, 3))
        buffer.flush() // readerIndex == 0, flush should be a no-op
        assertEquals(0, buffer.readerIndex)
        assertEquals(3, buffer.writerIndex)

        buffer.readToArray(ByteArray(0), 0, 0)
        buffer.readToBuffer(ArrayByteBuffer(), 0)
        buffer.skipRead(0)
        buffer.skipWrite(0)
        buffer.writeArray(byteArrayOf(9), 0, 0)
        buffer.writeBuffer(ArrayByteBuffer())

        assertEquals(3, buffer.readableSize)
        assertContentEquals(byteArrayOf(1, 2, 3), buffer.readArray())
    }

    @Test
    fun testArrayByteBufferNegativeAndUnderflowReadChecks() {
        val buffer = ArrayByteBuffer()

        assertFailsWith<BufferUnderflowException> { buffer.skipRead(-1) }
        assertFailsWith<BufferUnderflowException> { buffer.readToArray(ByteArray(1), 0, -1) }
        assertFailsWith<BufferUnderflowException> { buffer.readToBuffer(DummyOutputByteBuffer, -1) }

        buffer.writeArray(byteArrayOf(1, 2, 3))
        assertFailsWith<BufferUnderflowException> { buffer.readInt() }
        assertFailsWith<BufferUnderflowException> { buffer.readLong() }

        buffer.clear()
        assertFailsWith<BufferUnderflowException> { buffer.readByte() }
    }

    @Test
    fun testArrayByteBufferEnsureWriteOverflowBranch() {
        val buffer = ArrayByteBuffer(1)
        val writerIndexField = ArrayByteBuffer::class.java.getDeclaredField("_writerIndex")
        writerIndexField.isAccessible = true
        writerIndexField.setInt(buffer, Int.MAX_VALUE)

        assertFailsWith<BufferOverflowException> { buffer.ensureWrite(1) }
    }

    @Test
    fun testArrayByteBufferBackReadZeroIsNoop() {
        val buffer = ArrayByteBuffer()
        buffer.writeArray(byteArrayOf(1, 2))

        assertEquals(1, buffer.readByte().toInt())
        val before = buffer.readerIndex

        buffer.backRead(0)
        assertEquals(before, buffer.readerIndex)

        buffer.backRead(1)
        assertEquals(1, buffer.readByte().toInt())
    }

    @Test
    fun testSubInputDefaultStateAndRebind() {
        val sub = SubInputByteBuffer()
        assertFalse(sub.readable)
        assertEquals(0, sub.readableSize)
        assertTrue(sub.isReadable(0))
        assertFalse(sub.isReadable(1))
        assertFalse(sub.isReadable(-1))
        assertSame(DummyInputByteBuffer, sub.arrayView)

        val source = ArrayByteBuffer().apply {
            writeArray(byteArrayOf(5, 6, 7, 8))
        }

        sub.bindSource(source, 3)
        assertEquals(5, sub.readByte().toInt())

        sub.bindSource(source, 2)
        assertEquals(6, sub.readByte().toInt())
        assertEquals(7, sub.readByte().toInt())

        sub.unbindSource()
        assertSame(DummyInputByteBuffer, sub.arrayView)
    }

    @Test
    fun testSubInputReadIntLongAndTransferMethods() {
        val source = ArrayByteBuffer().apply {
            writeInt(0x01020304)
            writeLong(0x1112131415161718L)
            writeArray(byteArrayOf(9, 10))
        }

        val sub = SubInputByteBuffer(source, 12)
        assertEquals(0x01020304, sub.readInt())

        val bytes = ByteArray(8)
        sub.readToArray(bytes, 0, 8)
        assertEquals(0x1112131415161718L, bytes.getLong(0))
        assertFalse(sub.readable)
        assertEquals(2, source.readableSize)

        val subTail = SubInputByteBuffer(source, 2)
        val target = ArrayByteBuffer()
        subTail.readToBuffer(target, 2)
        assertContentEquals(byteArrayOf(9, 10), target.readArray())

        val longSource = ArrayByteBuffer().apply {
            writeLong(0x2122232425262728L)
        }
        val longSub = SubInputByteBuffer(longSource, 8)
        assertEquals(0x2122232425262728L, longSub.readLong())
    }

    @Test
    fun testSubInputNegativeAndUnderflowReadChecks() {
        val source = ArrayByteBuffer().apply {
            writeArray(byteArrayOf(1, 2, 3))
        }
        val sub = SubInputByteBuffer(source, 3)

        assertFailsWith<BufferUnderflowException> { sub.skipRead(-1) }
        assertFailsWith<BufferUnderflowException> { sub.readToArray(ByteArray(1), 0, -1) }
        assertFailsWith<BufferUnderflowException> { sub.readToBuffer(DummyOutputByteBuffer, -1) }
        assertFailsWith<BufferUnderflowException> { sub.readInt() }
        assertFailsWith<BufferUnderflowException> { sub.skipRead(4) }
    }

    @Test
    fun testDummyByteBufferNoopPathsAndProperties() {
        assertFalse(DummyByteBuffer.readable)
        assertEquals(0, DummyByteBuffer.readableSize)
        assertEquals(0, DummyByteBuffer.readerIndex)
        assertEquals(0, DummyByteBuffer.writerIndex)
        assertTrue(DummyByteBuffer.isReadable(0))
        assertFalse(DummyByteBuffer.isReadable(-1))

        DummyByteBuffer.readToArray(ByteArray(0), 0, 0)
        DummyByteBuffer.readToBuffer(DummyOutputByteBuffer, 0)
        DummyByteBuffer.skipRead(0)
        DummyByteBuffer.backRead(0)
        DummyByteBuffer.clear()
        DummyByteBuffer.flush()

        assertContentEquals(byteArrayOf(), DummyByteBuffer.arrayView.array)
    }

    @Test
    fun testByteArrayExtensionsWithNonZeroOffsets() {
        val bytes = ByteArray(16)
        bytes.putInt(3, 0x11223344)
        assertEquals(0x11223344, bytes.getInt(3))

        bytes.putLong(5, 0x0102030405060708L)
        assertEquals(0x0102030405060708L, bytes.getLong(5))

        val payload = byteArrayOf(9, 8, 7)
        bytes.putByteArray(1, payload)
        assertContentEquals(payload, bytes.getByteArray(1, 3))
    }

    @Test
    fun testReadArrayExtensionZeroAndUnderflow() {
        val buffer = ArrayByteBuffer().apply {
            writeArray(byteArrayOf(1, 2))
        }

        val empty = buffer.readArray(0)
        assertEquals(0, empty.size)
        assertEquals(2, buffer.readableSize)

        assertFailsWith<BufferUnderflowException> { buffer.readArray(3) }
    }

    @Test
    fun testBufferOverflowExceptionMessage() {
        val ex = BufferOverflowException("overflow")
        assertEquals("overflow", ex.message)
    }
}
