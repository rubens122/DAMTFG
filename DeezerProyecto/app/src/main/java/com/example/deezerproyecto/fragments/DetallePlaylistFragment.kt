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
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class DetallePlaylistFragment(private val playlist: Playlist) : Fragment() {

    private lateinit var adapter: CancionPlaylistAdapter
    private lateinit var recyclerViewCanciones: RecyclerView
    private lateinit var textoVacio: TextView
    private lateinit var botonEditar: Button
    private lateinit var imagenPlaylist: ImageView
    private lateinit var nombrePlaylist: TextView
    private lateinit var textoPrivacidad: TextView
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("playlists")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_playlist, container, false)

        recyclerViewCanciones = view.findViewById(R.id.recyclerCanciones)
        textoVacio = view.findViewById(R.id.textoVacio)
        botonEditar = view.findViewById(R.id.botonEditar)
        imagenPlaylist = view.findViewById(R.id.imagenPlaylist)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylist)
        textoPrivacidad = view.findViewById(R.id.textoPrivacidad)

        textoVacio.visibility = View.GONE

        adapter = CancionPlaylistAdapter(
            canciones = mutableListOf(),
            layout = R.layout.item_cancion_playlist,
            onEliminarCancion = { track ->
                eliminarCancionDePlaylist(track)
            }
        )

        recyclerViewCanciones.layoutManager = LinearLayoutManager(context)
        recyclerViewCanciones.adapter = adapter

        // 🔄 Cargar canciones al iniciar
        cargarCanciones()
        cargarDatosPlaylist()

        // 🚀 Navegación al fragmento de edición
        botonEditar.setOnClickListener {
            val fragment = EditarPlaylistFragment(playlist)
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    /**
     * 🔄 Método para cargar las canciones desde Firebase
     */
    private fun cargarCanciones() {
        Log.d("DetallePlaylistFragment", "📌 Iniciando carga manual de canciones...")

        reference.child(playlist.id).child("canciones")
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("DetallePlaylistFragment", "📌 Datos recibidos de Firebase: ${snapshot.value}")

                if (snapshot.exists()) {
                    val listaCanciones = mutableListOf<Track>()

                    for (cancionSnapshot in snapshot.children) {
                        val track = cancionSnapshot.getValue(Track::class.java)
                        if (track != null) {
                            listaCanciones.add(track)
                        }
                    }

                    if (listaCanciones.isEmpty()) {
                        textoVacio.visibility = View.VISIBLE
                    } else {
                        textoVacio.visibility = View.GONE
                    }

                    adapter.actualizarCanciones(listaCanciones)

                } else {
                    Log.w("DetallePlaylistFragment", "⚠️ No se encontraron canciones en la playlist.")
                    textoVacio.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Log.e("DetallePlaylistFragment", "❌ Error al acceder a Firebase: ${it.message}")
                Toast.makeText(requireContext(), "Error al cargar canciones", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * 🔄 Método para cargar los datos principales de la Playlist
     */
    private fun cargarDatosPlaylist() {
        reference.child(playlist.id)
            .get()
            .addOnSuccessListener { snapshot ->
                val datosActualizados = snapshot.getValue(Playlist::class.java)
                if (datosActualizados != null) {
                    nombrePlaylist.text = datosActualizados.nombre
                    textoPrivacidad.text = if (datosActualizados.esPrivada) "Privada" else "Pública"
                    if (datosActualizados.rutaFoto.isNotEmpty()) {
                        Picasso.get().load(datosActualizados.rutaFoto).into(imagenPlaylist)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("DetallePlaylistFragment", "❌ Error al cargar datos de la playlist: ${it.message}")
            }
    }

    /**
     * 🔥 Eliminar canción y actualizar en Firebase
     */
    private fun eliminarCancionDePlaylist(track: Track) {
        playlist.canciones.removeIf { it.id == track.id }
        reference.child(playlist.id).child("canciones").setValue(playlist.canciones)
            .addOnSuccessListener {
                adapter.eliminarCancion(track)
                Toast.makeText(requireContext(), "Canción eliminada correctamente", Toast.LENGTH_SHORT).show()

                if (playlist.canciones.isEmpty()) {
                    textoVacio.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al eliminar la canción", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        // 🔄 Recargar datos al volver
        cargarDatosPlaylist()
    }
}
