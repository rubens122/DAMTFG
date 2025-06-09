package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.PlaylistAdapter
import com.example.deezerproyecto.models.Playlist
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator

class BibliotecaFragment : Fragment() {

    private lateinit var campoBusqueda: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaylistAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textoVacio: TextView

    private val playlists = mutableListOf<Playlist>()
    private val database = FirebaseDatabase.getInstance()
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_biblioteca, container, false)

        campoBusqueda = view.findViewById(R.id.campoBusqueda)
        recyclerView = view.findViewById(R.id.recyclerPlaylists)
        progressBar = view.findViewById(R.id.progressBarBiblioteca)
        textoVacio = view.findViewById(R.id.textoVacio)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PlaylistAdapter(playlists.toMutableList(), soloContador = true) { playlist ->
            val fragment = DetallePlaylistFragment(playlist)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        campoBusqueda.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().lowercase()
                val filtradas = playlists.filter {
                    it.nombre.lowercase().contains(texto)
                }
                adapter.actualizarPlaylists(filtradas)
                textoVacio.visibility = if (filtradas.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        view.findViewById<FloatingActionButton>(R.id.fabAddPlaylist).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, CrearPlaylistFragment())
                .addToBackStack(null)
                .commit()
        }

        cargarPlaylists()

        return view
    }

    private fun cargarPlaylists() {
        uidUsuario?.let { uid ->
            progressBar.visibility = View.VISIBLE

            database.getReference("usuarios").child(uid).child("playlists")
                .get().addOnSuccessListener { snapshot ->
                    playlists.clear()

                    val playlistsMap = snapshot.getValue(object : GenericTypeIndicator<HashMap<String, Playlist>>() {})
                    val listaPlaylists = playlistsMap?.values?.toList() ?: listOf()

                    playlists.addAll(listaPlaylists)
                    adapter.actualizarPlaylists(playlists)

                    textoVacio.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener {
                    textoVacio.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
        }
    }
}
