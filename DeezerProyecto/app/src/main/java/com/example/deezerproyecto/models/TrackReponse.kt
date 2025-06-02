package com.example.deezerproyecto.models


data class TrackResponse(
    val data: List<Track>,
    val total: Int
)

data class Track(
    val id: Long = 0,
    val title: String = "",
    val artist: Artist = Artist(),
    val album: Album = Album(),
    val duration: Int = 0,
    val preview: String = ""
) {

    constructor() : this(0, "", Artist(), Album(), 0, "")
}

data class Artist(
    val id: Long = 0,
    val name: String = "",
    val picture: String = "",
    val picture_medium: String = "",
    val picture_big: String = "",
    val picture_xl: String = "",
    val nb_fan: Long = 0

)
 {
    constructor() : this(0, "", "")
}

data class Album(
    val id: Long = 0,
    val title: String = "",
    val cover: String = "",
    val artist: Artist = Artist()
) {
    constructor() : this(0, "", "", Artist())
}

