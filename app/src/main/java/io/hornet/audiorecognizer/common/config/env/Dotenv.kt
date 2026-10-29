package io.hornet.audiorecognizer.common.config.env

import org.dotenv.vault.dotenvVault

object Dotenv {
    val dotenv = dotenvVault()
}