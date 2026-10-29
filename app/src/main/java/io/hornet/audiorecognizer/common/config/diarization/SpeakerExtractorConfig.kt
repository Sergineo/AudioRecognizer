package io.hornet.audiorecognizer.common.config.diarization

import com.k2fsa.sherpa.onnx.FastClusteringConfig
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarization
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationConfig
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationModelConfig
import com.k2fsa.sherpa.onnx.SpeakerEmbeddingExtractorConfig
import io.hornet.audiorecognizer.common.config.env.Dotenv

object SpeakerExtractorConfig {
    val modelsDir: String = Dotenv.dotenv["ASR_MODEL_DIR"]
    val diarizationProvider: String = Dotenv.dotenv["DIARIZATION_PROVIDER"]
    val diarizationModel: String = Dotenv.dotenv["DIARIZATION_MODEL"]

    val speakerExtractor: OfflineSpeakerDiarization by lazy {
        val config = OfflineSpeakerDiarizationConfig(
            segmentation = OfflineSpeakerSegmentationModelConfig(
                provider = "$diarizationProvider",
                numThreads = 4
            ),
            embedding = SpeakerEmbeddingExtractorConfig(
                model = "$diarizationModel",
                numThreads = 4
            ),
            clustering = FastClusteringConfig(
                threshold = 0.6f,
                numClusters = 4,
            ),
            minDurationOn = 0.3f,
            minDurationOff = 0.5f
        )
        OfflineSpeakerDiarization(config = config)
    }
}