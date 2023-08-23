package org.shypl.tool.io

interface InputByteBuffer {
	val readable: Boolean
	
	val readableSize: Int
	
	val arrayView: ArrayView?
	
	fun isReadable(size: Int): Boolean
	
	fun readByte(): Byte
	
	fun readInt(): Int
	
	fun readLong(): Long
	
	fun readToArray(target: ByteArray, offset: Int = 0, size: Int = target.size - offset)
	
	fun readToBuffer(target: OutputByteBuffer, size: Int = readableSize)
	
	fun skipRead(size: Int = readableSize)
	
	fun backRead(size: Int)
	
	interface ArrayView {
		val array: ByteArray
		val readerIndex: Int
	}
}
