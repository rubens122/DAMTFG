package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionPlaylistAdapter
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class DetallePlaylistFragment(private val playlist: Playlist) : Fragment() {

    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionPlaylistAdapter
    private lateinit var nombrePlaylist: TextView
    private lateinit var imagenPlaylist: ImageView
    private lateinit var botonEditar: Button
    private lateinit var textoPrivacidad: TextView
    private lateinit var textoVacio: TextView

    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_playlist, container, false)

        // Enlazar vistas
        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylist)
        imagenPlaylist = view.findViewById(R.id.imagenPlaylist)
        botonEditar = view.findViewById(R.id.botonEditar)
        textoPrivacidad = view.findViewById(R.id.textoPrivacidad)
        textoVacio = view.findViewById(R.id.textoVacio)

        // Asignar datos
        nombrePlaylist.text = playlist.nombre
        textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        // Cargar imagen
        if (playlist.rutaFoto.isNotEmpty()) {
            Picasso.get().load(playlist.rutaFoto).fit().centerCrop().into(imagenPlaylist)
        } else {
            Picasso.get()
                .load("https://cdn-icons-png.flaticon.com/512/833/833281.png")
                .fit().centerCrop().into(imagenPlaylist)
        }

        // Configurar RecyclerView
        adapter = CancionPlaylistAdapter(playlist.canciones, R.layout.item_cancion_playlist)
        recyclerCanciones.layoutManager = LinearLayoutManager(context)
        recyclerCanciones.adapter = adapter

        // Mostrar texto si no hay canciones
        textoVacio.visibility = if (playlist.canciones.isEmpty()) View.VISIBLE else View.GONE

        // Mostrar botón de edición solo si es del usuario
        if (playlist.idUsuario == uidActual) {
            botonEditar.visibility = View.VISIBLE
            botonEditar.setOnClickListener {
                val editarFragment = EditarPlaylistFragment(playlist)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragment, editarFragment)
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            botonEditar.visibility = View.GONE
        }

        return view
    }
}
