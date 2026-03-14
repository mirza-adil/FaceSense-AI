package com.hobotech.facesenseai.camera

/**
 * Common frame representation for shared business logic.
 * Platform implementations (CameraX / AVFoundation) convert to this type.
 */
data class Frame(
    val bytes: ByteArray,
    val width: Int,
    val height: Int,
    val format: ImageFormat
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Frame
        if (!bytes.contentEquals(other.bytes)) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (format != other.format) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + format.hashCode()
        return result
    }
}

enum class ImageFormat {
    NV21,
    RGB,
    RGBA,
    YUV_420_888
}
