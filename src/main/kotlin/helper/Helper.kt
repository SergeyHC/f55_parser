package helper

import java.lang.NumberFormatException
import kotlin.math.pow

internal fun String.bytesFromString(): ByteArray {
    //TODO if (length % 2 != 0) maybe add a 0 or raise exception?
    return chunked(2).map{it.toInt(16).toByte()}.toByteArray()
}

internal fun ByteArray.stringFromBytes(): String {
    return asUByteArray().joinToString("") { it.toString(radix = 16).uppercase().padStart(2, '0')}
}

internal fun ByteArray.intFromBytes(): UInt {
    var result: UInt = 0u
    val workerByteArray = asUByteArray()
    for (i in workerByteArray.indices) {
        result += workerByteArray[i].toUInt()
        if (!(i == workerByteArray.lastIndex))
            result = result shl 8
    }
    return result
}


internal fun encodeTagLen(value: ByteArray): ByteArray {
    val intLength = value.size
    var resultByteArray: ByteArray
    if (intLength <= 0b01111111) {
        resultByteArray = byteArrayOf(intLength.toByte())
    }
    else {
        //Counting number of bytes in addition to the firs one, that is needed to encode tagLength (3 ix max)
        var tagLengthSize = 0
        for (i in 2..6 step 2) {
            if (intLength <= (16.toDouble().pow(i) - 1)) {
                tagLengthSize = i/2
            }
        }
        if (tagLengthSize == 0) {
            throw NumberFormatException("Can not calculate Value length, might be too long!")
        }
        resultByteArray = ByteArray(tagLengthSize + 1)
        //Filling in 1st byte by the spec rule (elder bit is 1, rest of em represent the number of subsequent Length bytes)
        resultByteArray[0] = (0b10000000 or tagLengthSize).toByte()
        // Filling in the rest of tagLength bytes by shifting intLength by 8 * n bits, than 8 * (n-1) etc..
        var shift = 8 * (tagLengthSize -1)
        for (i in 1 until tagLengthSize) {
            resultByteArray[i] = (intLength shr shift).toByte()
            shift -= 8
        }
    }
    return resultByteArray
}