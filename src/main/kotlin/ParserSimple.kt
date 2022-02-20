import helper.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ParserSimple(data: ByteArray) {
    private var rawData: ByteArray
    private var byteInputStream: ByteArrayInputStream

    init {
        //Maybe need to add check if data.size % 2 == 0 or, if data is prefixed with length, check is prefix = data.size
        this.rawData = data
        byteInputStream = ByteArrayInputStream(rawData)
    }

    constructor(sData: String) : this(sData.bytesFromString()) {
        val stringData = sData
    }

    fun parseData(): ArrayList<TLVObject> {

        var tagCollection: ArrayList<TLVObject> = ArrayList()

        while (byteInputStream.available() > 0) {
            try {
                val tag = getTag()
                val (tagLenToRead, tagLength) = getTagLength()
                if (tagLenToRead == 0u)
                    continue
                var tagValueBuffer: ByteArray = ByteArray(tagLenToRead.toInt())
                byteInputStream.read(tagValueBuffer, 0, tagValueBuffer.size)

                tagCollection.add(TLVObject(tag, tagLength, tagValueBuffer))
            } catch (ex: Exception) {
                println("Something went wrong. Last read byte was: ${rawData.size - byteInputStream.available()}\n" +
                        "Was able to parse:\n")
                for (item in tagCollection)
                    item.toFancyString()
            }
        }
        return tagCollection
    }

    private fun getTag(): ByteArray {
        // ByteArrayOutputStream for writing bytes encoding Tag
        var tagStream: ByteArrayOutputStream = ByteArrayOutputStream()

        // Getting 1st byte of tag
        val current = byteInputStream.read()
        // If bits from 1 to 5 of 1st byte are 1, that means next byte is also belongs to Tag
        if ((current shl 3).toByte() == 0b11111000.toByte()) {
            // Storing 1st byte and getting next
            tagStream.write(current)
            // If in each next byte most significant bit is 1 means nex byte belongs to Tag
            while (true) {
                val next = byteInputStream.read()
                if ((next shr 7).toByte() == 0b00000001.toByte())
                    tagStream.write(next)
                else {
                    tagStream.write(next)
                    break
                }
            }
        } else
            tagStream.write(current)

        tagStream.flush()
        return tagStream.toByteArray()
    }

    // In returned Pair Int is the number of bytes to read as a value of a Tag
    private fun getTagLength(): Pair<UInt, ByteArray> {
        // ByteArrayOutputStream for writing bytes encoding Length
        var lenStream: ByteArrayOutputStream = ByteArrayOutputStream()
        val firstLenByte = byteInputStream.read()
        // If most significant bit is 0 - this whole byte is a length byte
        if ((firstLenByte shr 7).toByte() == 0b00000000.toByte()) {
            lenStream.write(firstLenByte)
            return Pair(firstLenByte.toUInt(), lenStream.toByteArray())
        }

        // If we're made it here, length is encoded with more than 1 byte
        lenStream.write(firstLenByte)
        // Zeroing 8-th bit of first byte to get number of subsequent bytes that encode length
        var bytesToRead = (firstLenByte shl 1).toByte().toInt()
        bytesToRead = bytesToRead shr 1
        // Buffer ByteArray to store length for convenient further conversion to Int
        var lenBuffer: ByteArray = ByteArray(bytesToRead)
        for (i in 0 until bytesToRead) {
            lenBuffer[i]=byteInputStream.read().toByte()
        }
        lenStream.write(lenBuffer)
        lenStream.flush()

        return Pair(lenBuffer.intFromBytes(), lenStream.toByteArray())
    }
}

