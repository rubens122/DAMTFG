package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.AlbumAdapter
import com.example.deezerproyecto.adapters.ArtistaAdapter
import com.example.deezerproyecto.adapters.CancionHomeAdapter
import com.example.deezerproyecto.api.AlbumResponse
import com.example.deezerproyecto.api.ArtistResponse
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.Album
import com.example.deezerproyecto.models.Artist
import com.example.deezerproyecto.models.Track
import com.example.deezerproyecto.models.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var recyclerArtistas: RecyclerView
    private lateinit var recyclerAlbums: RecyclerView
    private lateinit var adapterCanciones: CancionHomeAdapter
    private lateinit var adapterArtistas: ArtistaAdapter
    private lateinit var adapterAlbums: AlbumAdapter
    private val deezerService: DeezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        inicializarVistas(view)
        inicializarAdaptadores()
        cargarDatosDeezer()

        return view
    }

    /**
     * 🔄 Inicialización de vistas
     */
    private fun inicializarVistas(view: View) {
        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        recyclerArtistas = view.findViewById(R.id.recyclerArtistas)
        recyclerAlbums = view.findViewById(R.id.recyclerAlbums)

        recyclerCanciones.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerArtistas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerAlbums.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * 🔄 Inicialización de adaptadores
     */
    private fun inicializarAdaptadores() {
        adapterCanciones = CancionHomeAdapter(mutableListOf()) { track ->
            Toast.makeText(requireContext(), "Pulsado: ${track.title}", Toast.LENGTH_SHORT).show()
        }

        adapterArtistas = ArtistaAdapter(mutableListOf()) { artist ->
            val fragment = DetalleArtistaFragment(artist.id, artist.name, artist.picture)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        adapterAlbums = AlbumAdapter(mutableListOf()) { album ->
            val fragment = DetalleAlbumFragment(album.id, album.title, album.cover)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerCanciones.adapter = adapterCanciones
        recyclerArtistas.adapter = adapterArtistas
        recyclerAlbums.adapter = adapterAlbums
    }

    /**
     * 🔄 Cargar datos de Deezer
     */
    private fun cargarDatosDeezer() {
        cargarTopCanciones()
        cargarTopArtistas()
        cargarTopAlbums()
    }

    private fun cargarTopCanciones() {
        val call = deezerService.buscarCancion("top")
        call.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val canciones = response.body()?.data ?: emptyList()
                    Log.d("HomeFragment", "📌 Canciones encontradas: ${canciones.size}")
                    adapterCanciones.actualizarCanciones(canciones)
                } else {
                    Log.e("HomeFragment", "Error en la respuesta de canciones: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Log.e("HomeFragment", "Error al cargar canciones: ${t.message}")
            }
        })
    }

    private fun cargarTopArtistas() {
        val call = deezerService.buscarArtistas()
        call.enqueue(object : Callback<ArtistResponse> {
            override fun onResponse(call: Call<ArtistResponse>, response: Response<ArtistResponse>) {
                if (response.isSuccessful) {
                    val artistas = response.body()?.data ?: emptyList()
                    Log.d("HomeFragment", "📌 Artistas encontrados: ${artistas.size}")
                    adapterArtistas.actualizarArtistas(artistas)
                } else {
                    Log.e("HomeFragment", "Error en la respuesta de artistas: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<ArtistResponse>, t: Throwable) {
                Log.e("HomeFragment", "Error al cargar artistas: ${t.message}")
            }
        })
    }

    private fun cargarTopAlbums() {
        val call = deezerService.buscarAlbums()
        call.enqueue(object : Callback<AlbumResponse> {
            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                if (response.isSuccessful) {
                    val albums = response.body()?.data ?: emptyList()
                    Log.d("HomeFragment", "📌 Álbumes encontrados: ${albums.size}")
                    adapterAlbums.actualizarAlbums(albums)
                } else {
                    Log.e("HomeFragment", "Error en la respuesta de álbumes: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                Log.e("HomeFragment", "Error al cargar álbumes: ${t.message}")
            }
        })
    }
}
