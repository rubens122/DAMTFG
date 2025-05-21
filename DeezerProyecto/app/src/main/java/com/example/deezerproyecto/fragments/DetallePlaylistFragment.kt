package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionPlaylistAdapter
import com.example.deezerproyecto.models.Playlist
import com.example.deezerproyecto.models.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylist)
        imagenPlaylist = view.findViewById(R.id.imagenPlaylist)
        botonEditar = view.findViewById(R.id.botonEditar)
        textoPrivacidad = view.findViewById(R.id.textoPrivacidad)
        textoVacio = view.findViewById(R.id.textoVacio)

        nombrePlaylist.text = playlist.nombre
        textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        if (playlist.rutaFoto.isNotEmpty()) {
            Picasso.get().load(playlist.rutaFoto).fit().centerCrop().into(imagenPlaylist)
        } else {
            Picasso.get()
                .load("https://cdn-icons-png.flaticon.com/512/833/833281.png")
                .fit().centerCrop().into(imagenPlaylist)
        }

        adapter = CancionPlaylistAdapter(
            canciones = playlist.canciones,
            layout = R.layout.item_cancion_playlist,
            onEliminarCancion = { track -> eliminarCancionDePlaylist(track) }
        )

        recyclerCanciones.layoutManager = LinearLayoutManager(context)
        recyclerCanciones.adapter = adapter

        textoVacio.visibility = if (playlist.canciones.isEmpty()) View.VISIBLE else View.GONE

        // Log para depuración
        Log.d("BOTON_EDITAR", "playlist.idUsuario=${playlist.idUsuario}, uidActual=$uidActual")

        // Visibilidad forzada para verificar funcionamiento
        botonEditar.visibility = View.VISIBLE
        botonEditar.setOnClickListener {
            Toast.makeText(requireContext(), "EDITANDO playlist", Toast.LENGTH_SHORT).show()
            val editarFragment = EditarPlaylistFragment(playlist)
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, editarFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun eliminarCancionDePlaylist(track: Track) {
        val uid = uidActual ?: return

        playlist.canciones.removeIf { it.id == track.id }

        val dbRef = FirebaseDatabase.getInstance().getReference("usuarios")
        dbRef.child(uid).child("playlists").child(playlist.id).setValue(playlist)
            .addOnSuccessListener {
                adapter.notifyDataSetChanged()
                if (playlist.canciones.isEmpty()) {
                    textoVacio.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al eliminar canción", Toast.LENGTH_SHORT).show()
            }
    }
}
