package org.shypl.tool.io

import org.shypl.tool.lang.EMPTY_BYTE_ARRAY

object DummyOutputByteBuffer : OutputByteBuffer, OutputByteBuffer.ArrayView {
	override val writerIndex: Int
		get() = 0
	
	override val arrayView: OutputByteBuffer.ArrayView
		get() = this
	
	override val array: ByteArray
		get() = EMPTY_BYTE_ARRAY
	
	override fun writeByte(value: Byte) {
		throw UnsupportedOperationException()
	}
	
	override fun writeInt(value: Int) {
		throw UnsupportedOperationException()
	}
	
	override fun writeLong(value: Long) {
		throw UnsupportedOperationException()
	}
	
	override fun writeArray(value: ByteArray, offset: Int, size: Int) {
		throw UnsupportedOperationException()
	}
	
	override fun writeBuffer(value: InputByteBuffer) {
		throw UnsupportedOperationException()
	}
	
	override fun ensureWrite(size: Int) {
		throw UnsupportedOperationException()
	}
	
	override fun skipWrite(size: Int) {
		throw UnsupportedOperationException()
	}
}