package com.example.deezerproyecto.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionAdapter
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.Playlist
import com.example.deezerproyecto.models.Track
import com.example.deezerproyecto.models.TrackResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuscarFragment : Fragment() {

    private lateinit var campoBusqueda: EditText
    private lateinit var botonBuscar: Button
    private lateinit var recyclerViewCanciones: RecyclerView
    private lateinit var adapter: CancionAdapter
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("usuarios")
    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid
    private val deezerService: DeezerService = DeezerClient.retrofit.create(DeezerService::class.java)
    private val playlists = mutableListOf<Playlist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buscar, container, false)

        campoBusqueda = view.findViewById(R.id.campoBusqueda)
        botonBuscar = view.findViewById(R.id.botonBuscar)
        recyclerViewCanciones = view.findViewById(R.id.recyclerCanciones)

        adapter = CancionAdapter(
            canciones = mutableListOf(),
            layout = R.layout.item_cancion,
            onClickAdd = { track ->
                mostrarDialogoSeleccionPlaylist(track)
            }
        )

        recyclerViewCanciones.layoutManager = LinearLayoutManager(context)
        recyclerViewCanciones.adapter = adapter

        botonBuscar.setOnClickListener {
            val query = campoBusqueda.text.toString()
            if (query.isNotEmpty()) {
                buscarCanciones(query)
            }
        }

        cargarPlaylists()
        return view
    }

    private fun buscarCanciones(query: String) {
        val call = deezerService.buscarCancion(query)
        call.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val canciones = response.body()?.data ?: emptyList()
                    adapter.actualizarCanciones(canciones)
                } else {
                    Toast.makeText(requireContext(), "Error al buscar canciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarPlaylists() {
        if (uidActual == null) return

        reference.child(uidActual).child("playlists").get().addOnSuccessListener { snapshot ->
            playlists.clear()
            for (playlistSnapshot in snapshot.children) {
                val playlist = playlistSnapshot.getValue(Playlist::class.java)
                playlist?.let { playlists.add(it) }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cargar las playlists", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoSeleccionPlaylist(track: Track) {
        val nombresPlaylists = playlists.map { it.nombre }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una Playlist")
            .setItems(nombresPlaylists) { _, index ->
                val playlistSeleccionada = playlists[index]
                anadirCancionAPlaylist(track, playlistSeleccionada)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun anadirCancionAPlaylist(track: Track, playlist: Playlist) {
        val existe = playlist.canciones.any { it.id == track.id }
        if (existe) {
            Toast.makeText(requireContext(), "La canción ya existe en esta Playlist", Toast.LENGTH_SHORT).show()
            return
        }

        playlist.canciones.add(track)
        reference.child(uidActual!!).child("playlists").child(playlist.id).setValue(playlist)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Canción añadida a ${playlist.nombre}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al añadir canción", Toast.LENGTH_SHORT).show()
            }
    }
}
