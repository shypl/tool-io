package org.shypl.tool.io

class BufferUnderflowException(message: String) : RuntimeException(message) {
	constructor(requestedSize: Int, availableSize: Int, readingBack: Boolean = false) : this(
		if (readingBack)
			"Not enough data to back read (requested: $requestedSize, available: $availableSize)"
		else
			"Not enough data to read (requested: $requestedSize, available: $availableSize)"
	)
	
	constructor(negativeSize: Int, readingBack: Boolean = false) : this(
		if (readingBack)
			"Back reading size is negative ($negativeSize)"
		else
			"Reading size is negative ($negativeSize)"
	)
	
}
