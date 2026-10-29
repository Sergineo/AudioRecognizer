package io.hornet.audiorecognizer.common.manager.file_decoder

import ws.schild.jave.Encoder
import ws.schild.jave.MultimediaObject
import ws.schild.jave.encode.AudioAttributes
import ws.schild.jave.encode.EncodingAttributes

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale

class FileDecoder {
    fun readFile(filePath: String): FloatArray {
        val file = File(filePath)
        if (!file.exists()) throw IllegalArgumentException("Файл не найден: $filePath")

        val extension = file.extension.lowercase(Locale.getDefault())

        return when (extension) {
            "wav" -> readWavDirectly(file)
            "mp3", "m4a", "flac", "ogg", "aac", "opus", "wma", "webm", "amr" -> decodeCompressedAudio(file)
            else -> throw IllegalArgumentException("Неподдерживаемый формат файла: .$extension")
        }
    }

    private fun readWavDirectly(file: File): FloatArray {
        val bytes = file.readBytes()

        var dataOffset = 12
        while (dataOffset < bytes.size - 8) {
            val chunkId = String(bytes, dataOffset, 4)
            val chunkSize = ByteBuffer.wrap(bytes, dataOffset + 4, 4)
                .order(ByteOrder.LITTLE_ENDIAN).int

            if (chunkId == "data") {
                dataOffset += 8 
                break
            }
            dataOffset += 8 + chunkSize
        }

        val pcmLength = bytes.size - dataOffset
        val shortBuffer = ByteBuffer.wrap(bytes, dataOffset, pcmLength)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()

        val floatArray = FloatArray(shortBuffer.remaining())
        for (i in floatArray.indices) {
            floatArray[i] = shortBuffer.get(i) / 32768.0f
        }
        return floatArray
    }

    private fun decodeCompressedAudio(sourceFile: File): FloatArray {
        val tempWavFile = File.createTempFile("sherpa_audio_convert_", ".wav")
        tempWavFile.deleteOnExit()

        try {
            val audioAttr = AudioAttributes().apply {
                setCodec("pcm_s16le") 
                setSamplingRate(16000) 
                setChannels(1)         
            }
            val attrs = EncodingAttributes().apply {
                setOutputFormat("wav") 
                setAudioAttributes(audioAttr)
            }
            val encoder = Encoder()
            encoder.encode(MultimediaObject(sourceFile), tempWavFile, attrs)

            return readWavDirectly(tempWavFile)
        } finally {
            if (tempWavFile.exists()) {
                tempWavFile.delete()
            }
        }
    }
}