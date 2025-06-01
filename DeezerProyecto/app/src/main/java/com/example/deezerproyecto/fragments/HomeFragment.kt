package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageButton
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
    private lateinit var botonPerfil: ImageButton

    private val deezerService: DeezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        inicializarVistas(view)
        inicializarAdaptadores()
        aplicarAnimaciones()
        cargarDatosDeezer()

        botonPerfil.setOnClickListener {
            val fragment = PerfilFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun inicializarVistas(view: View) {
        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        recyclerArtistas = view.findViewById(R.id.recyclerArtistas)
        recyclerAlbums = view.findViewById(R.id.recyclerAlbums)
        botonPerfil = view.findViewById(R.id.botonPerfil)

        recyclerCanciones.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerArtistas.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerAlbums.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

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
            val fragment = DetalleAlbumFragment(
                album.id.toString(),
                album.title,
                album.artist.name,
                album.cover
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerCanciones.adapter = adapterCanciones
        recyclerArtistas.adapter = adapterArtistas
        recyclerAlbums.adapter = adapterAlbums
    }

    private fun aplicarAnimaciones() {
        val animacion = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
        recyclerCanciones.startAnimation(animacion)
        recyclerArtistas.startAnimation(animacion)
        recyclerAlbums.startAnimation(animacion)
    }

    private fun cargarTopCanciones() {
        deezerService.obtenerTopCanciones().enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val canciones = response.body()?.data ?: emptyList()
                    adapterCanciones.actualizarCanciones(canciones)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar canciones populares", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarTopArtistas() {
        deezerService.buscarArtistas().enqueue(object : Callback<ArtistResponse> {
            override fun onResponse(call: Call<ArtistResponse>, response: Response<ArtistResponse>) {
                if (response.isSuccessful) {
                    val artistas = response.body()?.data ?: emptyList()
                    adapterArtistas.actualizarArtistas(artistas)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar artistas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArtistResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarTopAlbums() {
        deezerService.buscarAlbums().enqueue(object : Callback<AlbumResponse> {
            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                if (response.isSuccessful) {
                    val albums = response.body()?.data ?: emptyList()
                    adapterAlbums.actualizarAlbums(albums)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar álbumes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarDatosDeezer() {
        cargarTopCanciones()
        cargarTopArtistas()
        cargarTopAlbums()
    }
}
