package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ByteBufferTest {
    @Test
    fun testSubInputByteBufferBackRead() {
        val buffer = ArrayByteBuffer()
        buffer.writeByte(1)
        buffer.writeByte(2)
        buffer.writeByte(3)
        
        val subBuffer = SubInputByteBuffer(buffer, 2)
        assertEquals(1, subBuffer.readByte())
        assertEquals(2, subBuffer.readByte())
        
        // Попытка вернуться на 1 байт назад в subBuffer
        subBuffer.backRead(1)
        
        // Теперь subBuffer должен снова прочитать 2
        assertEquals(2, subBuffer.readByte())
    }

    @Test
    fun testSubInputByteBufferArrayView() {
        val buffer = ArrayByteBuffer()
        buffer.writeByte(10)
        buffer.writeByte(20)
        buffer.writeByte(30)
        
        buffer.readByte() // пропускаем 10, readerIndex = 1
        
        val subBuffer = SubInputByteBuffer(buffer, 2) // должен видеть [20, 30]
        val view = subBuffer.arrayView
        assertNotNull(view)
        
        assertEquals(1, view.readerIndex)
        assertEquals(20, view.array[view.readerIndex])
        
        subBuffer.readByte() // читаем 20. subBuffer._readerIndex = 1. buffer.readerIndex = 2.
        
        assertEquals(2, view.readerIndex)
        assertEquals(30, view.array[view.readerIndex])
    }
}
