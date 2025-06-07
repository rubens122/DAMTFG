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
import com.example.deezerproyecto.adapters.CancionDescubrimientoAdapter
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.Track
import com.example.deezerproyecto.models.TrackResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleAlbumFragment : Fragment() {

    private lateinit var albumId: String
    private lateinit var nombreAlbumTexto: String
    private lateinit var nombreArtistaTexto: String
    private lateinit var imagenUrl: String

    private lateinit var imagenAlbum: ImageView
    private lateinit var nombreAlbum: TextView
    private lateinit var detallesAlbum: TextView
    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionDescubrimientoAdapter

    private val canciones = mutableListOf<Track>()
    private val deezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            albumId = it.getString("albumId") ?: ""
            nombreAlbumTexto = it.getString("nombreAlbumTexto") ?: ""
            nombreArtistaTexto = it.getString("nombreArtistaTexto") ?: ""
            imagenUrl = it.getString("imagenUrl") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_album, container, false)

        imagenAlbum = view.findViewById(R.id.imagenAlbum)
        nombreAlbum = view.findViewById(R.id.nombreAlbum)
        detallesAlbum = view.findViewById(R.id.detallesAlbum)
        recyclerCanciones = view.findViewById(R.id.recyclerCancionesAlbum)

        nombreAlbum.text = nombreAlbumTexto
        detallesAlbum.text = "$nombreArtistaTexto • Álbum"

        if (imagenUrl.isNotEmpty()) {
            Picasso.get().load(imagenUrl).fit().centerCrop().into(imagenAlbum)
        } else {
            Picasso.get()
                .load("https://cdn-icons-png.flaticon.com/512/833/833281.png")
                .fit().centerCrop()
                .into(imagenAlbum)
        }

        adapter = CancionDescubrimientoAdapter(canciones) { track ->
            val imagenArtista = if (track.artist.picture.isNullOrEmpty()) {
                track.album.cover
            } else {
                track.artist.picture
            }

            registrarArtistaEscuchado(track.artist.name, imagenArtista)

            (activity as? HomeActivity)?.iniciarBarra(
                titulo = track.title,
                artista = track.artist.name,
                imagenUrl = track.album.cover,
                urlCancion = track.preview
            )
        }

        recyclerCanciones.layoutManager = LinearLayoutManager(requireContext())
        recyclerCanciones.adapter = adapter

        cargarCancionesDelAlbum()

        return view
    }

    private fun cargarCancionesDelAlbum() {
        deezerService.obtenerCancionesAlbum(albumId).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    canciones.clear()
                    canciones.addAll(response.body()?.data ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Error al cargar canciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registrarArtistaEscuchado(nombreArtista: String, urlImagen: String = "") {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()
        val datos = mapOf("timestamp" to timestamp, "imagen" to urlImagen)

        FirebaseDatabase.getInstance().getReference("usuarios")
            .child(uid)
            .child("ultimosArtistas")
            .child(nombreArtista)
            .setValue(datos)
    }
}
