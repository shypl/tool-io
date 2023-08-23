package org.shypl.tool.io

fun InputByteBuffer.readArray(size: Int = readableSize): ByteArray {
	return ByteArray(size).also { readToArray(it) }
}