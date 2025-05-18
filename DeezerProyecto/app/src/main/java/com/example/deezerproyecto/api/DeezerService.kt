package com.example.deezerproyecto.api

import com.example.deezerproyecto.models.Album
import com.example.deezerproyecto.models.Artist
import com.example.deezerproyecto.models.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerService {

    @GET("search")
    fun buscarCancion(
        @Query("q") query: String
    ): Call<TrackResponse>

    @GET("chart/0/artists")
    fun buscarArtistas(): Call<ArtistResponse>

    @GET("chart/0/albums")
    fun buscarAlbums(): Call<AlbumResponse>

    // ✅ 🔄 NUEVO método para obtener discografía de un artista
    @GET("artist/{id}/albums")
    fun buscarDiscografia(
        @Path("id") artistaId: Long
    ): Call<AlbumResponse>

}

/**
 * ✅ Respuesta de artistas
 */
data class ArtistResponse(
    val data: List<Artist>
)

/**
 * ✅ Respuesta de álbumes
 */
data class AlbumResponse(
    val data: List<Album>
)
