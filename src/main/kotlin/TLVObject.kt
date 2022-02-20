import helper.*

class TLVObject(tag: ByteArray, length: ByteArray, value: ByteArray) {
    private var tag: ByteArray
    private var tagLength: ByteArray
    private var tagValue: ByteArray
    private var isConstructed: Boolean = false
    private var nestedTLVObjectList: ArrayList<TLVObject>

    init {
        this.tag = tag
        this.tagLength = length
        this.tagValue = value

        if ((tag[0].toUByte().toInt() or 0b11011111) == 0b11111111) {
            isConstructed = true
            nestedTLVObjectList = Parser(tagValue).parseData()
        }
        else
            // Do not know how to better deal with empty list
            nestedTLVObjectList = ArrayList()

    }

    constructor(sTag: String, sValue: String): this(sTag.bytesFromString(), byteArrayOf(), sValue.bytesFromString()) {
        tagLength = encodeTagLen(tagValue)
    }

    public fun isConstructed(): Boolean = isConstructed

    internal fun getTLV(): ByteArray {
//        var result = ByteArray(tag.size + tagLength.size + tagValue.size)
        return tag + tagLength + tagValue
    }

    public fun toPair(): Pair<String, String> {
        return Pair(tag.decodeToString(), tagValue.decodeToString())
    }

    public fun toTriple(): Triple<String, String, String> {
        return Triple(tag.stringFromBytes(), tagLength.stringFromBytes(), tagValue.stringFromBytes())
    }

    override fun toString(): String {
        val t = this.toTriple()
        if (isConstructed) { //TODO need to thinl of better output of nested objects
            var buffer: StringBuffer = StringBuffer("${t.first} ${t.second} ${t.third}\n")
            for (item in nestedTLVObjectList) {
                buffer.append(item.toString())
                if (item.isConstructed)
                    buffer.append("\n")
                else
                    buffer.append(", ")
            }
//            buffer.append("\n")
            return buffer.toString()
        }

        else
            return "${t.first} ${t.second} ${t.third}"
    }

    public fun toFancyString(): String {
        val t = this.toTriple()
        return "Tag: ${t.first}\n" +
               "Length: ${t.second}\n" +
               "Value: ${t.third}\n"
    }
}