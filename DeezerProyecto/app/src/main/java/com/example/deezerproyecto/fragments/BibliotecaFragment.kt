package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.PlaylistAdapter
import com.example.deezerproyecto.databinding.FragmentBibliotecaBinding
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BibliotecaFragment : Fragment() {

    private var _binding: FragmentBibliotecaBinding? = null
    private val binding get() = _binding!!
    private val playlists = mutableListOf<Playlist>()
    private lateinit var adapter: PlaylistAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBibliotecaBinding.inflate(inflater, container, false)

        adapter = PlaylistAdapter(playlists) { playlist ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, DetallePlaylistFragment(playlist))
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerPlaylists.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPlaylists.adapter = adapter

        database = FirebaseDatabase.getInstance()

        if (uidUsuario != null) {
            reference = database.getReference("usuarios").child(uidUsuario).child("playlists")
            cargarPlaylists()
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }

        binding.fabAddPlaylist.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, PlaylistFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun cargarPlaylists() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playlists.clear()
                for (playlistSnapshot in snapshot.children) {
                    val playlist = playlistSnapshot.getValue(Playlist::class.java)
                    playlist?.let {
                        playlists.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                context?.let {
                    Toast.makeText(it, "Error al cargar playlists", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
