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
import com.example.deezerproyecto.adapters.PlaylistAdapter
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PerfilAmigoFragment(private val amigoId: String) : Fragment() {

    private lateinit var imagenPerfil: ImageView
    private lateinit var nombreUsuario: TextView
    private lateinit var recyclerPlaylists: RecyclerView
    private lateinit var adapter: PlaylistAdapter
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_amigo, container, false)

        imagenPerfil = view.findViewById(R.id.imagenPerfilAmigo)
        nombreUsuario = view.findViewById(R.id.nombreAmigo)
        recyclerPlaylists = view.findViewById(R.id.recyclerPlaylists)

        adapter = PlaylistAdapter(mutableListOf()) { playlist ->
            abrirPlaylist(playlist)
        }

        recyclerPlaylists.layoutManager = LinearLayoutManager(context)
        recyclerPlaylists.adapter = adapter

        cargarDatosAmigo()
        cargarPlaylistsPublicas()

        return view
    }

    /**
     * 🔄 Cargar datos del amigo (nombre e imagen)
     */
    private fun cargarDatosAmigo() {
        database.child(amigoId).get().addOnSuccessListener {
            val nombre = it.child("nombre").value as? String ?: "Sin Nombre"
            val fotoUrl = it.child("imagenPerfil").value as? String
            nombreUsuario.text = nombre

            if (fotoUrl != null) {
                Picasso.get().load(fotoUrl).fit().centerCrop().into(imagenPerfil)
            } else {
                Picasso.get().load("https://cdn-icons-png.flaticon.com/512/1946/1946429.png")
                    .fit()
                    .centerCrop()
                    .into(imagenPerfil)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 🔄 Cargar solo las playlists públicas del amigo
     */
    private fun cargarPlaylistsPublicas() {
        database.child(amigoId).child("playlists")
            .orderByChild("esPrivada")
            .equalTo(false) // 🔥 Solo las públicas
            .get()
            .addOnSuccessListener { snapshot ->
                val listaPlaylists = mutableListOf<Playlist>()
                for (playlistSnapshot in snapshot.children) {
                    val playlist = playlistSnapshot.getValue(Playlist::class.java)
                    if (playlist != null) {
                        listaPlaylists.add(playlist)
                    }
                }
                adapter.actualizarPlaylists(listaPlaylists)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar las playlists", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * 🔄 Abrir Playlist en un Fragment Detalle
     */
    private fun abrirPlaylist(playlist: Playlist) {
        val fragment = DetallePlaylistAmigoFragment(playlist)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
