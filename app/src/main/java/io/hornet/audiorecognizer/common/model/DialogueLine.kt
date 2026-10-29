package io.hornet.audiorecognizer.common.model

data class DialogueLine(
    val speaker: String,
    val start: Float,
    val end: Float,
    val text: String
)