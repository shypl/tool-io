package org.shypl.tool.io

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class CoverageAllTest {
	@Test
	fun testDummyInputByteBufferContractBranches() {
		assertFalse(DummyInputByteBuffer.readable)
		assertEquals(0, DummyInputByteBuffer.readableSize)
		assertEquals(0, DummyInputByteBuffer.readerIndex)
		assertSame(DummyInputByteBuffer, DummyInputByteBuffer.arrayView)
		assertContentEquals(byteArrayOf(), DummyInputByteBuffer.array)

		assertTrue(DummyInputByteBuffer.isReadable(0))
		assertFalse(DummyInputByteBuffer.isReadable(1))
		assertFalse(DummyInputByteBuffer.isReadable(-1))

		assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readToArray(ByteArray(1), 0, -1) }
		assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.readToBuffer(DummyOutputByteBuffer, -1) }
		assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.skipRead(-1) }
		assertFailsWith<BufferUnderflowException> { DummyInputByteBuffer.backRead(-1) }
	}

	@Test
	fun testDummyByteBufferContractBranches() {
		assertFalse(DummyByteBuffer.readable)
		assertEquals(0, DummyByteBuffer.readableSize)
		assertEquals(0, DummyByteBuffer.readerIndex)
		assertEquals(0, DummyByteBuffer.writerIndex)
		assertSame(DummyByteBuffer, DummyByteBuffer.arrayView)
		assertContentEquals(byteArrayOf(), DummyByteBuffer.array)

		assertTrue(DummyByteBuffer.isReadable(0))
		assertFalse(DummyByteBuffer.isReadable(1))
		assertFalse(DummyByteBuffer.isReadable(-1))

		assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readToArray(ByteArray(1), 0, -1) }
		assertFailsWith<BufferUnderflowException> { DummyByteBuffer.readToBuffer(DummyOutputByteBuffer, -1) }
		assertFailsWith<BufferUnderflowException> { DummyByteBuffer.skipRead(-1) }
		assertFailsWith<BufferUnderflowException> { DummyByteBuffer.backRead(-1) }
	}

	@Test
	fun testDummyOutputByteBufferProperties() {
		assertSame(DummyOutputByteBuffer, DummyOutputByteBuffer.arrayView)
		assertEquals(0, DummyOutputByteBuffer.writerIndex)
		assertContentEquals(byteArrayOf(), DummyOutputByteBuffer.array)
	}

	@Test
	fun testArrayByteBufferReadableBranches() {
		val buffer = ArrayByteBuffer()
		assertFalse(buffer.readable)
		buffer.writeByte(1)
		assertTrue(buffer.readable)
		buffer.readByte()
		assertFalse(buffer.readable)
	}

	@Test
	fun testArrayByteBufferFlushInvalidStateBranch() {
		val buffer = ArrayByteBuffer()
		val readerIndexField = ArrayByteBuffer::class.java.getDeclaredField("_readerIndex")
		val writerIndexField = ArrayByteBuffer::class.java.getDeclaredField("_writerIndex")
		readerIndexField.isAccessible = true
		writerIndexField.isAccessible = true
		readerIndexField.setInt(buffer, 2)
		writerIndexField.setInt(buffer, 1)
		buffer.flush()
	}

	@Test
	fun testArrayByteBufferCompanionInvokeViaReflection() {
		val method = ArrayByteBuffer.Companion::class.java.getDeclaredMethod("invoke", Function1::class.java)
		val block = object : Function1<ArrayByteBuffer, Unit> {
			override fun invoke(p1: ArrayByteBuffer) {
				p1.writeByte(0x2A)
			}
		}
		val buffer = method.invoke(ArrayByteBuffer.Companion, block) as ArrayByteBuffer
		assertEquals(1, buffer.readableSize)
		assertEquals(0x2A, buffer.readByte().toInt())
	}

	@Test
	fun testInterfaceDefaultArgumentStubs() {
		val buffer = ArrayByteBuffer().apply {
			writeArray(byteArrayOf(1, 2, 3))
		}
		val target = ByteArray(2)

		val readToArrayDefault = InputByteBuffer::class.java.getDeclaredMethod(
			"readToArray\$default",
			InputByteBuffer::class.java,
			ByteArray::class.java,
			Int::class.javaPrimitiveType,
			Int::class.javaPrimitiveType,
			Int::class.javaPrimitiveType,
			Any::class.java
		)
		readToArrayDefault.invoke(null, buffer, target, 0, 0, 6, null)
		assertContentEquals(byteArrayOf(1, 2), target)
		assertEquals(1, buffer.readableSize)

		val out = ArrayByteBuffer()
		val readToBufferDefault = InputByteBuffer::class.java.getDeclaredMethod(
			"readToBuffer\$default",
			InputByteBuffer::class.java,
			OutputByteBuffer::class.java,
			Int::class.javaPrimitiveType,
			Int::class.javaPrimitiveType,
			Any::class.java
		)
		readToBufferDefault.invoke(null, buffer, out, 0, 2, null)
		assertEquals(0, buffer.readableSize)
		assertEquals(1, out.readableSize)

		val buffer2 = ArrayByteBuffer().apply {
			writeArray(byteArrayOf(9, 8, 7))
		}
		val skipReadDefault = InputByteBuffer::class.java.getDeclaredMethod(
			"skipRead\$default",
			InputByteBuffer::class.java,
			Int::class.javaPrimitiveType,
			Int::class.javaPrimitiveType,
			Any::class.java
		)
		skipReadDefault.invoke(null, buffer2, 0, 1, null)
		assertEquals(0, buffer2.readableSize)

		val writeArrayDefault = OutputByteBuffer::class.java.getDeclaredMethod(
			"writeArray\$default",
			OutputByteBuffer::class.java,
			ByteArray::class.java,
			Int::class.javaPrimitiveType,
			Int::class.javaPrimitiveType,
			Int::class.javaPrimitiveType,
			Any::class.java
		)
		val out2 = ArrayByteBuffer()
		writeArrayDefault.invoke(null, out2, byteArrayOf(4, 5), 0, 0, 6, null)
		assertContentEquals(byteArrayOf(4, 5), out2.readArray())

		val readToArrayEx = assertFailsWith<java.lang.reflect.InvocationTargetException> {
			readToArrayDefault.invoke(null, DummyInputByteBuffer, ByteArray(1), 0, 0, 0, Any())
		}
		assertTrue(readToArrayEx.cause is UnsupportedOperationException)

		val readToBufferEx = assertFailsWith<java.lang.reflect.InvocationTargetException> {
			readToBufferDefault.invoke(null, DummyInputByteBuffer, DummyOutputByteBuffer, 0, 0, Any())
		}
		assertTrue(readToBufferEx.cause is UnsupportedOperationException)

		val skipReadEx = assertFailsWith<java.lang.reflect.InvocationTargetException> {
			skipReadDefault.invoke(null, DummyInputByteBuffer, 0, 0, Any())
		}
		assertTrue(skipReadEx.cause is UnsupportedOperationException)

		val writeArrayEx = assertFailsWith<java.lang.reflect.InvocationTargetException> {
			writeArrayDefault.invoke(null, DummyOutputByteBuffer, byteArrayOf(1), 0, 0, 0, Any())
		}
		assertTrue(writeArrayEx.cause is UnsupportedOperationException)
	}
}
