package io.hornet.audiorecognizer.domain

interface FileDecoder {
    fun readFile(filePath: String): FloatArray
}