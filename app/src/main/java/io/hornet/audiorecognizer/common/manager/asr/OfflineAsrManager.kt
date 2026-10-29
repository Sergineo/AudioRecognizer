package io.hornet.audiorecognizer.common.manager.asr

import io.hornet.audiorecognizer.common.config.asr.ModelRecognizerConfig
import io.hornet.audiorecognizer.common.config.diarization.SpeakerExtractorConfig
import io.hornet.audiorecognizer.common.manager.file_decoder.FileDecoder
import io.hornet.audiorecognizer.common.model.DialogueLine

class OfflineAsrManager(
    private val fileDecoder: FileDecoder
) {
    fun recognizeFileData(){
        fun processAudioToDialogue(wavePath: String): List<DialogueLine> {
            val recognizer = ModelRecognizerConfig.recognizer

            val samples = fileDecoder.readFile(wavePath)
            val sampleRate = 16000

            val segments = SpeakerExtractorConfig.speakerExtractor.process(samples)

            val dialogueResult = mutableListOf<DialogueLine>()

            for (seg in segments) {
                val startSample = (seg.start * sampleRate).toInt()
                val endSample = (seg.end * sampleRate).toInt()

                val speakerSamples = samples.copyOfRange(startSample, endSample)

                val stream = recognizer.createStream()
                stream.acceptWaveform(speakerSamples, sampleRate)
                recognizer.decode(stream)
                val textResult = recognizer.getResult(stream).text
                stream.release()

                if (textResult.isNotBlank()) {
                    dialogueResult.add(
                        DialogueLine(
                            speaker = "Спикер ${seg.speaker + 1}",
                            start = seg.start,
                            end = seg.end,
                            text = textResult.trim()
                        )
                    )
                }
            }

            return dialogueResult.sortedBy { it.start }
        }
    }
}