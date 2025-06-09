package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.TopPlaylistAdapter
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.database.*

class TopPlaylistsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: TopPlaylistAdapter
    private val listaTop = mutableListOf<Triple<Playlist, String, Int>>()
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val refLikes = FirebaseDatabase.getInstance().getReference("likes")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_top_playlists, container, false)
        recyclerView = vista.findViewById(R.id.recyclerTopPlaylists)
        progressBar = vista.findViewById(R.id.progressBarTop)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TopPlaylistAdapter(listaTop) { playlist, uidAutor ->
            val fragment = DetallePlaylistAmigoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("playlist", playlist)
                    putString("uidAmigo", uidAutor)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        cargarPlaylistsConLikes()

        return vista
    }

    private fun cargarPlaylistsConLikes() {
        progressBar.visibility = View.VISIBLE

        database.get().addOnSuccessListener { snapshot ->
            val listaTemporal = mutableListOf<Triple<Playlist, String, Int>>()
            var procesadas = 0
            val total = contarPlaylistsPublicas(snapshot)

            if (total == 0) {
                progressBar.visibility = View.GONE
                return@addOnSuccessListener
            }

            for (usuarioSnap in snapshot.children) {
                val nombreUsuario = usuarioSnap.child("correo").value as? String ?: "Desconocido"
                val playlistsSnap = usuarioSnap.child("playlists")
                for (playlistSnap in playlistsSnap.children) {
                    val playlist = playlistSnap.getValue(Playlist::class.java)
                    if (playlist != null && !playlist.esPrivada) {
                        val id = playlist.id
                        refLikes.child(id).get().addOnSuccessListener { likesSnap ->
                            val cantidadLikes = likesSnap.childrenCount.toInt()
                            listaTemporal.add(Triple(playlist, nombreUsuario, cantidadLikes))
                            procesadas++
                            if (procesadas == total) {
                                listaTop.clear()
                                listaTop.addAll(listaTemporal.sortedByDescending { it.third })
                                adapter.notifyDataSetChanged()
                                progressBar.visibility = View.GONE
                            }
                        }.addOnFailureListener {
                            procesadas++
                            if (procesadas == total) {
                                progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al cargar playlists", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
    }

    private fun contarPlaylistsPublicas(snapshot: DataSnapshot): Int {
        var total = 0
        for (usuario in snapshot.children) {
            val playlists = usuario.child("playlists")
            total += playlists.children.count {
                val p = it.getValue(Playlist::class.java)
                p != null && !p.esPrivada
            }
        }
        return total
    }
}
