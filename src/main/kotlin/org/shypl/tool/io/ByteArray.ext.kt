package org.shypl.tool.io

fun ByteArray.putInt(offset: Int, value: Int) {
	this[offset] = value.ushr(24).toByte()
	this[offset + 1] = value.ushr(16).toByte()
	this[offset + 2] = value.ushr(8).toByte()
	this[offset + 3] = value.toByte()
}

fun ByteArray.putLong(offset: Int, value: Long) {
	this[offset] = value.ushr(56).toByte()
	this[offset + 1] = value.ushr(48).toByte()
	this[offset + 2] = value.ushr(40).toByte()
	this[offset + 3] = value.ushr(32).toByte()
	this[offset + 4] = value.ushr(24).toByte()
	this[offset + 5] = value.ushr(16).toByte()
	this[offset + 6] = value.ushr(8).toByte()
	this[offset + 7] = value.toByte()
}

fun ByteArray.putByteArray(offset: Int, value: ByteArray) {
	value.copyInto(this, offset)
}

fun ByteArray.getInt(offset: Int): Int {
	return this[offset].toInt().and(0xFF).shl(24) +
		this[offset + 1].toInt().and(0xFF).shl(16) +
		this[offset + 2].toInt().and(0xFF).shl(8) +
		this[offset + 3].toInt().and(0xFF)
}

fun ByteArray.getLong(offset: Int): Long {
	return this[offset].toLong().shl(56) +
		this[offset + 1].toLong().and(0xFF).shl(48) +
		this[offset + 2].toLong().and(0xFF).shl(40) +
		this[offset + 3].toLong().and(0xFF).shl(32) +
		this[offset + 4].toLong().and(0xFF).shl(24) +
		this[offset + 5].toInt().and(0xFF).shl(16) +
		this[offset + 6].toInt().and(0xFF).shl(8) +
		this[offset + 7].toInt().and(0xFF)
}

fun ByteArray.getByteArray(offset: Int, size: Int): ByteArray {
	return copyOfRange(offset, offset + size)
}