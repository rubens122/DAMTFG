package com.example.deezerproyecto.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.ArtistaSugeridoAdapter
import com.example.deezerproyecto.adapters.CancionAdapter
import com.example.deezerproyecto.adapters.PlaylistSeleccionAdapter
import com.example.deezerproyecto.api.ArtistResponse
import com.example.deezerproyecto.api.DeezerClient
import com.example.deezerproyecto.api.DeezerService
import com.example.deezerproyecto.models.Playlist
import com.example.deezerproyecto.models.Track
import com.example.deezerproyecto.models.TrackResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuscarFragment : Fragment() {

    private lateinit var campoBusqueda: EditText
    private lateinit var botonBuscar: Button
    private lateinit var recyclerContenido: RecyclerView
    private lateinit var tituloArtistas: TextView

    private lateinit var adapterCanciones: CancionAdapter
    private lateinit var adapterArtistas: ArtistaSugeridoAdapter

    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid
    private val reference = FirebaseDatabase.getInstance().getReference("usuarios")
    private val deezerService = DeezerClient.retrofit.create(DeezerService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_buscar, container, false)

        campoBusqueda = view.findViewById(R.id.campoBusqueda)
        botonBuscar = view.findViewById(R.id.botonBuscar)
        recyclerContenido = view.findViewById(R.id.recyclerContenido)
        tituloArtistas = view.findViewById(R.id.tituloArtistas)

        adapterCanciones = CancionAdapter(
            canciones = mutableListOf(),
            layout = R.layout.item_cancion,
            onClickAdd = { track -> mostrarDialogoSeleccionPlaylist(track) }
        )

        recyclerContenido.layoutManager = LinearLayoutManager(context)
        recyclerContenido.adapter = adapterCanciones

        botonBuscar.setOnClickListener {
            val query = campoBusqueda.text.toString().trim()
            if (query.isNotEmpty()) {
                buscarCanciones(query)
            }
        }

        cargarArtistasSugeridos()

        return view
    }

    private fun cargarArtistasSugeridos() {
        deezerService.obtenerArtistasPopulares().enqueue(object : Callback<ArtistResponse> {
            override fun onResponse(call: Call<ArtistResponse>, response: Response<ArtistResponse>) {
                if (response.isSuccessful) {
                    val artistas = response.body()?.data ?: emptyList()

                    tituloArtistas.visibility = View.VISIBLE
                    recyclerContenido.layoutManager = GridLayoutManager(context, 2)

                    adapterArtistas = ArtistaSugeridoAdapter(artistas.take(20)) { nombreArtista ->
                        campoBusqueda.setText(nombreArtista)
                        buscarCanciones(nombreArtista)
                    }

                    recyclerContenido.adapter = adapterArtistas
                } else {
                    Toast.makeText(requireContext(), "Error al cargar artistas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArtistResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun buscarCanciones(query: String) {
        deezerService.buscarCancion(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val canciones = response.body()?.data ?: emptyList()
                    tituloArtistas.visibility = View.GONE
                    recyclerContenido.layoutManager = LinearLayoutManager(context)
                    adapterCanciones.actualizarCanciones(canciones)
                    recyclerContenido.adapter = adapterCanciones
                } else {
                    Toast.makeText(requireContext(), "Error al buscar canciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun mostrarDialogoSeleccionPlaylist(track: Track) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog_elegir_playlist, null)
        val recycler = dialogView.findViewById<RecyclerView>(R.id.recyclerPlaylists)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val alert = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val uid = uidActual ?: return

        reference.child(uid).child("playlists")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaActualizada = mutableListOf<Playlist>()

                    for (playlistSnapshot in snapshot.children) {
                        val playlist = playlistSnapshot.getValue(Playlist::class.java)
                        if (playlist != null) {
                            listaActualizada.add(playlist)
                        }
                    }

                    val adapter = PlaylistSeleccionAdapter(listaActualizada) { playlist ->
                        val idCancion = track.id.toString()
                        val yaExiste = playlist.canciones?.containsKey(idCancion) == true

                        if (yaExiste) {
                            Toast.makeText(requireContext(), "La canción ya está en la playlist", Toast.LENGTH_SHORT).show()
                        } else {
                            val nuevasCanciones = playlist.canciones?.toMutableMap() ?: mutableMapOf()
                            nuevasCanciones[idCancion] = track

                            reference.child(uid).child("playlists").child(playlist.id)
                                .child("canciones").setValue(nuevasCanciones)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Canción añadida", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Error al guardar canción", Toast.LENGTH_SHORT).show()
                                }
                        }

                        alert.dismiss()
                    }

                    recycler.adapter = adapter
                    alert.show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar playlists", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
