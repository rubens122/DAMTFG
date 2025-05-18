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
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.TrackResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleAlbumFragment(
    private val albumId: Long,
    private val albumNombre: String,
    private val albumImagen: String
) : Fragment() {

    private lateinit var imagenAlbum: ImageView
    private lateinit var nombreAlbum: TextView
    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapterCanciones: CancionHomeAdapter
    private val deezerService: DeezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_album, container, false)

        imagenAlbum = view.findViewById(R.id.imagenAlbum)
        nombreAlbum = view.findViewById(R.id.nombreAlbum)
        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)

        nombreAlbum.text = albumNombre

        // 🔄 URL de alta calidad para el álbum
        val urlAltaCalidad = albumImagen.replace("/image", "/image?size=1000x1000")

        // 🔄 Cargar con alta resolución
        Picasso.get()
            .load(urlAltaCalidad)
            .fit()
            .centerCrop()
            .into(imagenAlbum)

        // 🔄 Inicializar el RecyclerView
        recyclerCanciones.layoutManager = LinearLayoutManager(context)
        adapterCanciones = CancionHomeAdapter(mutableListOf()) { track ->
            Toast.makeText(requireContext(), "Reproduciendo: ${track.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerCanciones.adapter = adapterCanciones

        // 🔄 Cargar las canciones del álbum
        cargarCanciones()

        return view
    }

    /**
     * 🔄 Método para cargar las canciones del álbum seleccionado
     */
    private fun cargarCanciones() {
        val call = deezerService.buscarCancion(albumNombre)
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
}
