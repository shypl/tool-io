package org.shypl.tool.io

import org.shypl.tool.lang.EMPTY_BYTE_ARRAY

object DummyInputByteBuffer : InputByteBuffer, InputByteBuffer.ArrayView {
	override val readable: Boolean
		get() = false
	
	override val readableSize: Int
		get() = 0
	
	override val readerIndex: Int
		get() = 0
	
	override val arrayView: InputByteBuffer.ArrayView
		get() = this
	
	override val array: ByteArray
		get() = EMPTY_BYTE_ARRAY
	
	override fun isReadable(size: Int): Boolean {
		return false
	}
	
	override fun readByte(): Byte {
		throw BufferUnderflowException(1, 0)
	}
	
	override fun readInt(): Int {
		throw BufferUnderflowException(4, 0)
	}
	
	override fun readLong(): Long {
		throw BufferUnderflowException(8, 0)
	}
	
	override fun readToArray(target: ByteArray, offset: Int, size: Int) {
		if (size != 0) {
			throw BufferUnderflowException(size, 0)
		}
	}
	
	override fun readToBuffer(target: OutputByteBuffer, size: Int) {
		if (size != 0) {
			throw BufferUnderflowException(size, 0)
		}
	}
	
	override fun skipRead(size: Int) {
		if (size != 0) {
			throw BufferUnderflowException(size, 0)
		}
	}
	
	override fun backRead(size: Int) {
		if (size != 0) {
			throw BufferUnderflowException(size, 0, true)
		}
	}
}
