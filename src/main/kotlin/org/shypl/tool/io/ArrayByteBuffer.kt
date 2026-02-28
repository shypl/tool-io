package org.shypl.tool.io

import org.shypl.tool.lang.EMPTY_BYTE_ARRAY
import kotlin.concurrent.Volatile

class ArrayByteBuffer(initialCapacity: Int = 10) : ByteBuffer, ByteBuffer.ArrayView {
	companion object {
		inline operator fun invoke(block: ArrayByteBuffer.() -> Unit): ArrayByteBuffer {
			return ArrayByteBuffer().apply(block)
		}
		
		inline operator fun invoke(initialCapacity: Int, block: ArrayByteBuffer.() -> Unit): ArrayByteBuffer {
			return ArrayByteBuffer(initialCapacity).apply(block)
		}
		
		operator fun invoke(from: InputByteBuffer): ArrayByteBuffer {
			return ArrayByteBuffer(from.readableSize) { writeBuffer(from) }
		}
		
		operator fun invoke(bytes: ByteArray): ArrayByteBuffer {
			return ArrayByteBuffer(bytes.size) { writeArray(bytes) }
		}
	}
	
	@Volatile private var _array = if (initialCapacity == 0) EMPTY_BYTE_ARRAY else ByteArray(initialCapacity)
	@Volatile private var _readerIndex = 0
	@Volatile private var _writerIndex = 0
	
	
	override val readerIndex: Int
		get() = _readerIndex
	
	override val writerIndex: Int
		get() = _writerIndex
	
	override val readable: Boolean
		get() = _readerIndex < _writerIndex
	
	override val readableSize: Int
		get() = _writerIndex - _readerIndex
	
	override val arrayView: ByteBuffer.ArrayView
		get() = this
	
	override var array: ByteArray
		get() = _array
		set(value) {
			clear()
			_array = value
			_writerIndex = _array.size
		}
	
	
	override fun clear() {
		_readerIndex = 0
		_writerIndex = 0
	}
	
	override fun flush() {
		if (_readerIndex > 0) {
			if (_readerIndex < _writerIndex) {
				_array.copyInto(_array, 0, _readerIndex, _writerIndex)
				_writerIndex -= _readerIndex
				_readerIndex = 0
			}
			else if (_readerIndex == _writerIndex) {
				clear()
			}
		}
	}
	
	override fun isReadable(size: Int): Boolean {
		return size >= 0 && readableSize >= size
	}
	
	override fun readByte(): Byte {
		checkRead(1)
		return _array[_readerIndex].also {
			completeRead(1)
		}
	}
	
	override fun readInt(): Int {
		checkRead(4)
		return _array.getInt(_readerIndex).also {
			completeRead(4)
		}
	}
	
	override fun readLong(): Long {
		checkRead(8)
		return _array.getLong(_readerIndex).also {
			completeRead(8)
		}
	}
	
	override fun readToArray(target: ByteArray, offset: Int, size: Int) {
		if (size != 0) {
			checkRead(size)
			_array.copyInto(target, offset, _readerIndex, _readerIndex + size)
			completeRead(size)
		}
	}
	
	override fun readToBuffer(target: OutputByteBuffer, size: Int) {
		if (size != 0) {
			checkRead(size)
			target.writeArray(_array, _readerIndex, size)
			completeRead(size)
		}
	}
	
	override fun skipRead(size: Int) {
		if (size != 0) {
			checkRead(size)
			completeRead(size)
		}
	}
	
	override fun backRead(size: Int) {
		if (size < 0) {
			throw BufferUnderflowException(size, true)
		}
		if (size > _readerIndex) {
			throw BufferUnderflowException(size, _readerIndex, true)
		}
		_readerIndex -= size
	}
	
	override fun writeByte(value: Byte) {
		ensureWrite(1)
		_array[_writerIndex] = value
		completeWrite(1)
	}
	
	override fun writeInt(value: Int) {
		ensureWrite(4)
		_array.putInt(_writerIndex, value)
		completeWrite(4)
	}
	
	override fun writeLong(value: Long) {
		ensureWrite(8)
		_array.putLong(_writerIndex, value)
		completeWrite(8)
	}
	
	override fun writeArray(value: ByteArray, offset: Int, size: Int) {
		if (size != 0) {
			ensureWrite(size)
			value.copyInto(_array, _writerIndex, offset, offset + size)
			completeWrite(size)
		}
	}
	
	override fun writeBuffer(value: InputByteBuffer) {
		val size = value.readableSize
		if (size != 0) {
			ensureWrite(size)
			value.readToArray(_array, _writerIndex, size)
			completeWrite(size)
		}
	}
	
	override fun ensureWrite(size: Int) {
		if (size < 0) {
			throw IllegalArgumentException("Writing size is negative ($size)")
		}
		if (_readerIndex == _writerIndex && _readerIndex != 0) {
			clear()
		}
		
		val minCapacity = _writerIndex + size
		val oldCapacity = _array.size
		if (minCapacity > oldCapacity) {
			var newCapacity = oldCapacity + oldCapacity.shr(1)
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity
			}
			_array = _array.copyOf(newCapacity)
		}
		else if (minCapacity < 0) {
			throw BufferOverflowException("Buffer capacity overflow (current: $oldCapacity, additional: $size, max: ${Int.MAX_VALUE})")
		}
	}
	
	override fun skipWrite(size: Int) {
		if (size != 0) {
			ensureWrite(size)
			completeWrite(size)
		}
	}
	
	private fun checkRead(size: Int) {
		if (size < 0) {
			throw BufferUnderflowException(size)
		}
		if (size > readableSize) {
			throw BufferUnderflowException(size, readableSize)
		}
	}
	
	private fun completeRead(size: Int) {
		_readerIndex += size
	}
	
	private fun completeWrite(size: Int) {
		_writerIndex += size
	}
}
