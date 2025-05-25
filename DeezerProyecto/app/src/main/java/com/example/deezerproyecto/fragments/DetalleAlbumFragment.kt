package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionDescubrimientoAdapter
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.Track
import com.example.deezerproyecto.models.TrackResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class DetalleAlbumFragment(
    private val albumId: String,
    private val nombreAlbumTexto: String,
    private val nombreArtistaTexto: String,
    private val imagenUrl: String
) : Fragment() {

    private lateinit var imagenAlbum: ImageView
    private lateinit var nombreAlbum: TextView
    private lateinit var detallesAlbum: TextView
    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionDescubrimientoAdapter

    private val canciones = mutableListOf<Track>()
    private val deezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_album, container, false)

        imagenAlbum = view.findViewById(R.id.imagenAlbum)
        nombreAlbum = view.findViewById(R.id.nombreAlbum)
        detallesAlbum = view.findViewById(R.id.detallesAlbum)
        recyclerCanciones = view.findViewById(R.id.recyclerCancionesAlbum)

        // ✅ Título y detalles
        nombreAlbum.text = nombreAlbumTexto
        detallesAlbum.text = "$nombreArtistaTexto • Álbum"

        // ✅ Imagen en alta calidad si es posible
        val imagenAlta = imagenUrl.replace("/image", "/image?size=1000x1000")
        if (imagenAlta.isNotEmpty()) {
            Picasso.get().load(imagenAlta).fit().centerCrop().into(imagenAlbum)
        } else {
            Picasso.get()
                .load("https://cdn-icons-png.flaticon.com/512/833/833281.png")
                .fit().centerCrop()
                .into(imagenAlbum)
        }

        adapter = CancionDescubrimientoAdapter(canciones)
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
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                // Opcional: Toast de error si lo deseas
            }
        })
    }
}
