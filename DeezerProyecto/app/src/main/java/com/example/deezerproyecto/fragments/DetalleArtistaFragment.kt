package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.HomeActivity
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionHomeAdapter
import com.example.deezerproyecto.adapters.AlbumAdapter
import com.example.deezerproyecto.api.AlbumResponse
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.Artist
import com.example.deezerproyecto.models.TrackResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

class DetalleArtistaFragment(
    private val artistaId: Long,
    private val artistaNombre: String,
    private val artistaImagen: String
) : Fragment() {

    private lateinit var imagenArtista: ImageView
    private lateinit var nombreArtista: TextView
    private lateinit var textoOyentes: TextView
    private lateinit var recyclerTopCanciones: RecyclerView
    private lateinit var recyclerDiscografia: RecyclerView
    private lateinit var adapterCanciones: CancionHomeAdapter
    private lateinit var adapterDiscografia: AlbumAdapter
    private val deezerService: DeezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detalle_artista, container, false)

        imagenArtista = view.findViewById(R.id.imagenArtista)
        nombreArtista = view.findViewById(R.id.nombreArtista)
        textoOyentes = view.findViewById(R.id.textoOyentes)
        recyclerTopCanciones = view.findViewById(R.id.recyclerTopCanciones)
        recyclerDiscografia = view.findViewById(R.id.recyclerDiscografia)

        nombreArtista.text = artistaNombre

        val urlAltaCalidad = artistaImagen.ifEmpty {
            "https://cdn-icons-png.flaticon.com/512/833/833281.png"
        }

        Picasso.get()
            .load(urlAltaCalidad)
            .fit()
            .centerCrop()
            .into(imagenArtista)

        recyclerTopCanciones.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerDiscografia.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapterCanciones = CancionHomeAdapter(mutableListOf()) { track ->
            (activity as? HomeActivity)?.iniciarBarra(
                titulo = track.title,
                artista = track.artist.name,
                imagenUrl = track.album.cover_xl.ifEmpty { track.album.cover_big },
                urlCancion = track.preview
            )
        }

        adapterDiscografia = AlbumAdapter(mutableListOf()) { album ->
            val fragment = DetalleAlbumFragment().apply {
                arguments = Bundle().apply {
                    putString("albumId", album.id.toString())
                    putString("nombreAlbumTexto", album.title)
                    putString("nombreArtistaTexto", album.artist.name)
                    putString("imagenUrl", album.cover_xl.ifEmpty { album.cover_big })
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerTopCanciones.adapter = adapterCanciones
        recyclerDiscografia.adapter = adapterDiscografia

        cargarTopCanciones()
        cargarDiscografia()
        cargarSeguidores()

        return view
    }

    private fun cargarTopCanciones() {
        deezerService.buscarCancion(artistaNombre).enqueue(object : Callback<TrackResponse> {
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

    private fun cargarDiscografia() {
        deezerService.buscarDiscografia(artistaId).enqueue(object : Callback<AlbumResponse> {
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

    private fun cargarSeguidores() {
        deezerService.obtenerDetalleArtista(artistaId).enqueue(object : Callback<Artist> {
            override fun onResponse(call: Call<Artist>, response: Response<Artist>) {
                if (response.isSuccessful) {
                    val artista = response.body()
                    val seguidores = artista?.nb_fan ?: 0
                    val formateado = NumberFormat.getInstance().format(seguidores)
                    textoOyentes.text = "$formateado seguidores"
                }
            }

            override fun onFailure(call: Call<Artist>, t: Throwable) {}
        })
    }
}
