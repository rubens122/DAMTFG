package com.example.deezerproyecto.models

import java.io.Serializable

data class Comentario(
    val id: String = "",
    val texto: String = "",
    val autor: String = "",
    val timestamp: Long = 0L
) : Serializable
