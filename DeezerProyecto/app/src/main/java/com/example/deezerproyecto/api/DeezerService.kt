package com.example.deezerproyecto.api

import com.example.deezerproyecto.models.Album
import com.example.deezerproyecto.models.Artist
import com.example.deezerproyecto.models.Track
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

    @GET("chart/0/tracks")
    fun obtenerTopCanciones(): Call<TrackResponse>

    @GET("chart/0/artists")
    fun buscarArtistas(): Call<ArtistResponse>

    @GET("chart/0/albums")
    fun buscarAlbums(): Call<AlbumResponse>

    @GET("artist/{id}/albums")
    fun buscarDiscografia(
        @Path("id") artistaId: Long
    ): Call<AlbumResponse>

    @GET("album/{id}/tracks")
    fun obtenerCancionesAlbum(
        @Path("id") albumId: String
    ): Call<TrackResponse>
    @GET("artist/{id}/top?limit=10")
    fun obtenerTopCancionesArtista(
        @Path("id") artistaId: Long
    ): Call<TrackResponse>

    @GET("track/{id}")
    fun obtenerDetalleTrack(
        @Path("id") trackId: String
    ): Call<Track>

    @GET("artist/{id}")
    fun obtenerDetalleArtista(
        @Path("id") artistaId: Long
    ): Call<Artist>

    @GET("album/{id}")
    fun obtenerDetalleAlbum(
        @Path("id") albumId: String
    ): Call<Album>
    @GET("chart/0/artists")
    fun obtenerArtistasPopulares(): Call<ArtistResponse>

}

data class ArtistResponse(val data: List<Artist>)
data class AlbumResponse(val data: List<Album>)
