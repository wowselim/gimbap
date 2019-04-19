package co.selim.gimbap.util

import java.io.InputStream

fun getBytesFromInputStream(inputStream: InputStream): ByteArray {
    return inputStream.use {
        it.readBytes()
    }
}