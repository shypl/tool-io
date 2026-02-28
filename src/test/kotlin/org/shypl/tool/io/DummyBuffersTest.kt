package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class DummyBuffersTest {
    @Test
    fun testDummyInputByteBuffer() {
        assertNotNull(DummyInputByteBuffer.arrayView)
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readByte() }
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readInt() }
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readLong() }
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readToArray(ByteArray(1), 0, 1) }
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readToBuffer(DummyOutputByteBuffer, 1) }
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.skipRead(1) }
        assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.backRead(1) }
    }

    @Test
    fun testDummyOutputByteBuffer() {
        assertNotNull(DummyOutputByteBuffer.arrayView)
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.writeByte(1) }
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.writeInt(1) }
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.writeLong(1L) }
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.writeArray(byteArrayOf(1), 0, 1) }
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.writeBuffer(DummyInputByteBuffer) }
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.ensureWrite(1) }
        assertFailsWith<UnsupportedOperationException> { DummyOutputByteBuffer.skipWrite(1) }
    }

    @Test
    fun testDummyByteBuffer() {
        assertSame(DummyByteBuffer, DummyByteBuffer.arrayView)
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readByte() }
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readInt() }
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readLong() }
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readToArray(ByteArray(1), 0, 1) }
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readToBuffer(DummyOutputByteBuffer, 1) }
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.skipRead(1) }
        assertFailsWith<BufferUnderflowException> { DummyByteBuffer.backRead(1) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.writeByte(1) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.writeInt(1) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.writeLong(1L) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.writeArray(byteArrayOf(1), 0, 1) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.writeBuffer(DummyInputByteBuffer) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.ensureWrite(1) }
        assertFailsWith<UnsupportedOperationException> { DummyByteBuffer.skipWrite(1) }
    }
}
