package com.example.deezerproyecto.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionAmigoAdapter
import com.example.deezerproyecto.adapters.PlaylistSeleccionAdapter
import com.example.deezerproyecto.databinding.FragmentDetallePlaylistAmigoBinding
import com.example.deezerproyecto.models.Playlist
import com.example.deezerproyecto.models.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class DetallePlaylistAmigoFragment : Fragment() {

    private var _binding: FragmentDetallePlaylistAmigoBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlist: Playlist
    private lateinit var uidAmigo: String
    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private lateinit var adapter: CancionAmigoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlist = requireArguments().getSerializable("playlist") as Playlist
        uidAmigo = requireArguments().getString("uidAmigo") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallePlaylistAmigoBinding.inflate(inflater, container, false)

        binding.nombrePlaylistAmigo.text = playlist.nombre
        binding.textoPrivacidadAmigo.text = if (playlist.esPrivada) "Privada" else "Pública"

        val ruta = playlist.rutaFoto
        if (ruta.startsWith("/9j/")) {
            try {
                val bytes = android.util.Base64.decode(ruta, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.imagenPlaylistAmigo.setImageBitmap(bitmap)
            } catch (e: Exception) {
                binding.imagenPlaylistAmigo.setImageResource(R.drawable.ic_user)
            }
        } else {
            val url = if (ruta.isEmpty()) {
                "https://cdn-icons-png.flaticon.com/512/833/833281.png"
            } else ruta

            Picasso.get().load(url).fit().centerCrop().into(binding.imagenPlaylistAmigo)
        }


        adapter = CancionAmigoAdapter(
            canciones = mutableListOf(),
            onAnadir = { track ->
                registrarArtistaEscuchado(track.artist.name, track.artist.picture)
                mostrarDialogoSeleccionPlaylist(track)
            }
        )


        binding.recyclerCancionesAmigo.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCancionesAmigo.adapter = adapter

        cargarCanciones()
        cargarComentarios()

        binding.botonEnviarComentarioAmigo.setOnClickListener {
            val texto = binding.inputComentarioAmigo.text.toString().trim()
            if (texto.isNotEmpty()) {
                enviarComentario(texto)
                binding.inputComentarioAmigo.setText("")
            }
        }

        return binding.root
    }

    private fun cargarCanciones() {
        database.child(uidAmigo).child("playlists").child(playlist.id).child("canciones")
            .get().addOnSuccessListener { snapshot ->
                val canciones = snapshot.children.mapNotNull {
                    it.getValue(Track::class.java)
                }.toMutableList()
                adapter.actualizarCanciones(canciones)
            }
    }

    private fun cargarComentarios() {
        val ref = database.child(uidAmigo).child("playlists").child(playlist.id).child("comentarios")
        val inflater = LayoutInflater.from(requireContext())
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.contenedorComentariosAmigo.removeAllViews()
                for (comentarioSnap in snapshot.children) {
                    val comentario = comentarioSnap.getValue(com.example.deezerproyecto.models.Comentario::class.java)
                    if (comentario != null) {
                        val vista = inflater.inflate(R.layout.item_comentario, binding.contenedorComentariosAmigo, false)
                        vista.findViewById<TextView>(R.id.autorComentario).text = comentario.autor
                        vista.findViewById<TextView>(R.id.textoComentario).text = comentario.texto

                        val fechaTexto = if (comentario.timestamp is Long) {
                            formato.format(Date(comentario.timestamp))
                        } else {
                            "Sin fecha"
                        }

                        vista.findViewById<TextView>(R.id.fechaComentario).text = fechaTexto
                        binding.contenedorComentariosAmigo.addView(vista)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun enviarComentario(texto: String) {
        val id = database.push().key ?: return
        val comentario = mapOf(
            "id" to id,
            "texto" to texto,
            ("autor" to FirebaseAuth.getInstance().currentUser?.email ?: "Anónimo") as Pair<Any, Any>,
            "timestamp" to ServerValue.TIMESTAMP
        )

        database.child(uidAmigo).child("playlists").child(playlist.id)
            .child("comentarios").child(id).setValue(comentario)
    }

    private fun mostrarDialogoSeleccionPlaylist(track: Track) {
        val uid = uidActual ?: return
        val reference = FirebaseDatabase.getInstance().getReference("usuarios")

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog_elegir_playlist, null)
        val recycler = dialogView.findViewById<RecyclerView>(R.id.recyclerPlaylists)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val alert = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun registrarArtistaEscuchado(nombreArtista: String, urlImagen: String = "") {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()
        val datos = mapOf("timestamp" to timestamp, "imagen" to urlImagen)

        FirebaseDatabase.getInstance().getReference("usuarios")
            .child(uid)
            .child("ultimosArtistas")
            .child(nombreArtista)
            .setValue(datos)
    }

}
