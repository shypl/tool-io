package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SubInputByteBufferTest {
    @Test
    fun testBindSourceNegativeSize() {
        val source = ArrayByteBuffer()
        assertFailsWith<BufferUnderflowException> { SubInputByteBuffer(source, -1) }
    }

    @Test
    fun testBindSourceInsufficient() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(1, 2))
        assertFailsWith<BufferUnderflowException> { SubInputByteBuffer(source, 3) }
    }

    @Test
    fun testReadWithinBound() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(7, 8, 9))
        val sub = SubInputByteBuffer(source, 2)

        assertTrue(sub.readable)
        assertEquals(2, sub.readableSize)
        assertEquals(7, sub.readByte().toInt())
        assertEquals(8, sub.readByte().toInt())
        assertFalse(sub.readable)
        assertEquals(1, source.readableSize)
        assertEquals(9, source.readByte().toInt())
    }

    @Test
    fun testSkipAndBackRead() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(10, 11, 12, 13))
        val sub = SubInputByteBuffer(source, 3)

        sub.skipRead(2)
        assertEquals(12, sub.readByte().toInt())
        sub.backRead(1)
        assertEquals(12, sub.readByte().toInt())
        assertEquals(13, source.readByte().toInt())
    }

    @Test
    fun testBackReadErrors() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(1, 2))
        val sub = SubInputByteBuffer(source, 2)
        sub.readByte()

        assertFailsWith<BufferUnderflowException> { sub.backRead(2) }
        assertFailsWith<BufferUnderflowException> { sub.backRead(-1) }
    }

    @Test
    fun testArrayViewIsSourceView() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(5, 6, 7))
        source.readByte() // readerIndex = 1

        val sub = SubInputByteBuffer(source, 2)
        val view = sub.arrayView
        assertNotNull(view)
        assertEquals(1, view.readerIndex)
        assertEquals(6, view.array[view.readerIndex])

        sub.readByte()
        assertEquals(2, view.readerIndex)
        assertEquals(7, view.array[view.readerIndex])
    }

    @Test
    fun testUnbindSourceResets() {
        val source = ArrayByteBuffer()
        source.writeArray(byteArrayOf(1, 2))
        val sub = SubInputByteBuffer(source, 1)
        sub.unbindSource()
        assertFalse(sub.readable)
        assertEquals(0, sub.readableSize)
    }
}
