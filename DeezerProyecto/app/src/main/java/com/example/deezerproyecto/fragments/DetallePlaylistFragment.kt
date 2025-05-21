package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionPlaylistAdapter
import com.example.deezerproyecto.models.Comentario
import com.example.deezerproyecto.models.Playlist
import com.example.deezerproyecto.models.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ServerValue
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class DetallePlaylistFragment(private val playlist: Playlist) : Fragment() {

    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionPlaylistAdapter
    private lateinit var nombrePlaylist: TextView
    private lateinit var imagenPlaylist: ImageView
    private lateinit var botonEditar: Button
    private lateinit var textoPrivacidad: TextView
    private lateinit var textoVacio: TextView
    private lateinit var inputComentario: EditText
    private lateinit var botonEnviarComentario: Button
    private lateinit var contenedorComentarios: LinearLayout

    private val auth = FirebaseAuth.getInstance()
    private val uidActual = auth.currentUser?.uid
    private val correoUsuario = auth.currentUser?.email ?: "Anónimo"
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detalle_playlist, container, false)

        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylist)
        imagenPlaylist = view.findViewById(R.id.imagenPlaylist)
        botonEditar = view.findViewById(R.id.botonEditar)
        textoPrivacidad = view.findViewById(R.id.textoPrivacidad)
        textoVacio = view.findViewById(R.id.textoVacio)
        inputComentario = view.findViewById(R.id.inputComentario)
        botonEnviarComentario = view.findViewById(R.id.botonEnviarComentario)
        contenedorComentarios = view.findViewById(R.id.contenedorComentarios)

        nombrePlaylist.text = playlist.nombre
        textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        Picasso.get().load(playlist.rutaFoto.ifEmpty {
            "https://cdn-icons-png.flaticon.com/512/833/833281.png"
        }).fit().centerCrop().into(imagenPlaylist)

        adapter = CancionPlaylistAdapter(
            canciones = playlist.canciones,
            layout = R.layout.item_cancion_playlist
        ) { track ->
            eliminarCancionDePlaylist(track)
        }

        recyclerCanciones.layoutManager = LinearLayoutManager(context)
        recyclerCanciones.adapter = adapter

        textoVacio.visibility = if (playlist.canciones.isEmpty()) View.VISIBLE else View.GONE

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

        botonEnviarComentario.setOnClickListener {
            val texto = inputComentario.text.toString().trim()
            if (texto.isNotEmpty()) {
                enviarComentario(texto)
                inputComentario.setText("")
            }
        }

        cargarComentarios()

        return view
    }

    private fun enviarComentario(texto: String) {
        val comentarioId = database.push().key ?: return
        val comentarioMap = mapOf(
            "id" to comentarioId,
            "texto" to texto,
            "autor" to correoUsuario,
            "timestamp" to ServerValue.TIMESTAMP
        )

        database.child(uidActual!!).child("playlists")
            .child(playlist.id).child("comentarios").child(comentarioId)
            .setValue(comentarioMap)
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al enviar comentario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarComentarios() {
        val inflater = LayoutInflater.from(requireContext())
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        database.child(uidActual!!).child("playlists").child(playlist.id).child("comentarios")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contenedorComentarios.removeAllViews()

                    for (comentarioSnapshot in snapshot.children) {
                        val comentario = comentarioSnapshot.getValue(Comentario::class.java)
                        if (comentario != null) {
                            val viewComentario = inflater.inflate(R.layout.item_comentario, contenedorComentarios, false)

                            val autor = viewComentario.findViewById<TextView>(R.id.autorComentario)
                            val texto = viewComentario.findViewById<TextView>(R.id.textoComentario)
                            val fecha = viewComentario.findViewById<TextView>(R.id.fechaComentario)

                            autor.text = comentario.autor
                            texto.text = comentario.texto
                            fecha.text = formatoFecha.format(Date(comentario.timestamp))

                            contenedorComentarios.addView(viewComentario)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar comentarios", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun eliminarCancionDePlaylist(track: Track) {
        val uid = uidActual ?: return
        val playlistRef = database.child(uid).child("playlists").child(playlist.id)

        playlistRef.child("comentarios").get().addOnSuccessListener { snapshot ->
            val comentariosGuardados = snapshot.value

            playlist.canciones.removeIf { it.id == track.id }

            val playlistMap = mutableMapOf<String, Any?>(
                "id" to playlist.id,
                "nombre" to playlist.nombre,
                "esPrivada" to playlist.esPrivada,
                "rutaFoto" to playlist.rutaFoto,
                "idUsuario" to playlist.idUsuario,
                "canciones" to playlist.canciones
            )

            if (comentariosGuardados != null) {
                playlistMap["comentarios"] = comentariosGuardados
            }

            playlistRef.setValue(playlistMap)
                .addOnSuccessListener {
                    adapter.notifyDataSetChanged()
                    if (playlist.canciones.isEmpty()) {
                        textoVacio.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al actualizar playlist", Toast.LENGTH_SHORT).show()
                }

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al conservar comentarios", Toast.LENGTH_SHORT).show()
        }
    }
}
