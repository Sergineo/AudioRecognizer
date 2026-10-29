package io.hornet.audiorecognizer.common.config.asr

import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineSenseVoiceModelConfig
import io.hornet.audiorecognizer.common.config.env.Dotenv

object ModelRecognizerConfig {
    val modelsDir: String = Dotenv.dotenv["ASR_MODEL_DIR"]
    val supportedLanguages: String = Dotenv.dotenv["SUPPORTED_LANG"]
    val modelTokens: String = Dotenv.dotenv["MODEL_TOKENS"]

    val recognizer: OfflineRecognizer by lazy {
        val config = OfflineRecognizerConfig(
            modelConfig = OfflineModelConfig(
                senseVoice = OfflineSenseVoiceModelConfig(
                    model = "$modelsDir",
                    language = "$supportedLanguages",
                    useInverseTextNormalization = true
                ),
                tokens = "$modelTokens",
                numThreads = 4,
                debug = false
            )
        )
        OfflineRecognizer(config = config)
    }
}