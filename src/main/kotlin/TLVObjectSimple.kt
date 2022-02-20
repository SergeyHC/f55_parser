import helper.*

class TLVObjectSimple(tag: ByteArray, length: ByteArray, value: ByteArray) {
    private var tag: ByteArray
    private var tagLength: ByteArray
    private var tagValue: ByteArray

    init {
        this.tag = tag
        this.tagLength = length
        this.tagValue = value
    }

    constructor(sTag: String, sValue: String): this(sTag.bytesFromString(), byteArrayOf(), sValue.bytesFromString()) {
        tagLength = encodeTagLen(tagValue)
    }

    public fun toPair(): Pair<String, String> {
        return Pair(tag.decodeToString(), tagValue.decodeToString())
    }

    public fun toTriple(): Triple<String, String, String> {
        return Triple(tag.stringFromBytes(), tagLength.stringFromBytes(), tagValue.stringFromBytes())
    }

    override fun toString(): String {
        val t = this.toTriple()
        return "${t.first} ${t.second} ${t.third}"
    }

    public fun toFancyString(): String {
        val t = this.toTriple()
        return "Tag: ${t.first}\n" +
               "Length: ${t.second}\n" +
               "Value: ${t.third}\n"
    }


}