package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionHomeAdapter
import com.example.deezerproyecto.adapters.AlbumAdapter
import com.example.deezerproyecto.api.AlbumResponse
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.TrackResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleArtistaFragment(
    private val artistaId: Long,
    private val artistaNombre: String,
    private val artistaImagen: String
) : Fragment() {

    private lateinit var imagenArtista: ImageView
    private lateinit var nombreArtista: TextView
    private lateinit var recyclerTopCanciones: RecyclerView
    private lateinit var recyclerDiscografia: RecyclerView
    private lateinit var adapterCanciones: CancionHomeAdapter
    private lateinit var adapterDiscografia: AlbumAdapter
    private val deezerService: DeezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_artista, container, false)

        imagenArtista = view.findViewById(R.id.imagenArtista)
        nombreArtista = view.findViewById(R.id.nombreArtista)
        recyclerTopCanciones = view.findViewById(R.id.recyclerTopCanciones)
        recyclerDiscografia = view.findViewById(R.id.recyclerDiscografia)

        nombreArtista.text = artistaNombre

        // ✅ URL de alta calidad
        val urlAltaCalidad = artistaImagen.replace("/image", "/image?size=1000x1000")

        // ✅ Cargar imagen en alta resolución
        Picasso.get()
            .load(urlAltaCalidad)
            .fit()
            .centerCrop()
            .into(imagenArtista)

        // 🔄 Ahora son horizontales
        recyclerTopCanciones.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerDiscografia.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapterCanciones = CancionHomeAdapter(mutableListOf()) { track ->
            Toast.makeText(requireContext(), "Reproduciendo: ${track.title}", Toast.LENGTH_SHORT).show()
        }

        adapterDiscografia = AlbumAdapter(mutableListOf()) { album ->
            Toast.makeText(requireContext(), "Álbum seleccionado: ${album.title}", Toast.LENGTH_SHORT).show()
        }

        recyclerTopCanciones.adapter = adapterCanciones
        recyclerDiscografia.adapter = adapterDiscografia

        cargarTopCanciones()
        cargarDiscografia()

        return view
    }

    /**
     * 🔄 Cargar las canciones más populares del artista
     */
    private fun cargarTopCanciones() {
        val call = deezerService.buscarCancion(artistaNombre)
        call.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val canciones = response.body()?.data ?: emptyList()
                    adapterCanciones.actualizarCanciones(canciones)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar canciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 🔄 Cargar la discografía del artista
     */
    private fun cargarDiscografia() {
        val call = deezerService.buscarDiscografia(artistaId)
        call.enqueue(object : Callback<AlbumResponse> {
            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                if (response.isSuccessful) {
                    val albums = response.body()?.data ?: emptyList()
                    adapterDiscografia.actualizarAlbums(albums)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar discografía", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
