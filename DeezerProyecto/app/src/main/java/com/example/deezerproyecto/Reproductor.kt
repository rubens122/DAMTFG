package com.example.deezerproyecto

import android.content.Context
import android.media.MediaPlayer

object Reproductor {
    private var mediaPlayer: MediaPlayer? = null
    var estaReproduciendo = false

    fun reproducir(contexto: Context, url: String, cuandoFinaliza: () -> Unit) {
        detener()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                start()
                estaReproduciendo = true
            }
            setOnCompletionListener {
                estaReproduciendo = false
                cuandoFinaliza()
            }
            prepareAsync()
        }
    }

    fun pausar() {
        mediaPlayer?.pause()
        estaReproduciendo = false
    }

    fun continuar() {
        mediaPlayer?.start()
        estaReproduciendo = true
    }

    fun detener() {
        mediaPlayer?.release()
        mediaPlayer = null
        estaReproduciendo = false
    }
}
