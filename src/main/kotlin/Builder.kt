import java.io.ByteArrayOutputStream

class Builder(val tagCollection: List<TLVObject>) {
    public fun buildF55(): ByteArray {
        var buffer: ByteArrayOutputStream = ByteArrayOutputStream()
        for (tag in tagCollection)
            buffer.write(tag.getTLV())
        buffer.flush()
        return buffer.toByteArray()
    }
}