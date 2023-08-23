package org.shypl.tool.io

interface OutputByteBuffer {
	val arrayView: ArrayView?
	
	fun writeByte(value: Byte)
	
	fun writeInt(value: Int)
	
	fun writeLong(value: Long)
	
	fun writeArray(value: ByteArray, offset: Int = 0, size: Int = value.size - offset)
	
	fun writeBuffer(value: InputByteBuffer)
	
	fun ensureWrite(size: Int)
	
	fun skipWrite(size: Int)
	
	interface ArrayView {
		val array: ByteArray
		val writerIndex: Int
	}
}
