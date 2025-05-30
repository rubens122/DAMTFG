package com.example.deezerproyecto.models

import java.io.Serializable

data class Playlist(
    var id: String = "",
    var nombre: String = "",
    var esPrivada: Boolean = false,
    var rutaFoto: String = "",
    var idUsuario: String = "",
    var canciones: Map<String, Track>? = null
) : Serializable
