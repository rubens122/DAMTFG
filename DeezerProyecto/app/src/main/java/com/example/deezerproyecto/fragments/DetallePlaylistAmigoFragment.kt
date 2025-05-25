package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
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
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class DetallePlaylistAmigoFragment(
    private val playlist: Playlist,
    private val uidAmigo: String
) : Fragment() {

    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionPlaylistAdapter
    private lateinit var nombrePlaylist: TextView
    private lateinit var imagenPlaylist: ImageView
    private lateinit var textoPrivacidad: TextView
    private lateinit var contenedorComentarios: LinearLayout
    private lateinit var inputComentario: EditText
    private lateinit var botonEnviarComentario: Button

    private val correoUsuario = FirebaseAuth.getInstance().currentUser?.email ?: "Anónimo"
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_playlist_amigo, container, false)

        recyclerCanciones = view.findViewById(R.id.recyclerCancionesAmigo)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylistAmigo)
        imagenPlaylist = view.findViewById(R.id.imagenPlaylistAmigo)
        textoPrivacidad = view.findViewById(R.id.textoPrivacidadAmigo)
        contenedorComentarios = view.findViewById(R.id.contenedorComentariosAmigo)
        inputComentario = view.findViewById(R.id.inputComentarioAmigo)
        botonEnviarComentario = view.findViewById(R.id.botonEnviarComentarioAmigo)

        nombrePlaylist.text = playlist.nombre
        textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        Picasso.get().load(
            playlist.rutaFoto.ifEmpty {
                "https://cdn-icons-png.flaticon.com/512/833/833281.png"
            }
        ).fit().centerCrop().into(imagenPlaylist)

        cargarCanciones()
        cargarComentarios()

        botonEnviarComentario.setOnClickListener {
            val texto = inputComentario.text.toString().trim()
            if (texto.isNotEmpty()) {
                enviarComentario(texto)
                inputComentario.setText("")
            }
        }

        return view
    }

    private fun cargarCanciones() {
        val ref = database.child(uidAmigo)
            .child("playlists")
            .child(playlist.id)
            .child("canciones")

        ref.get().addOnSuccessListener { snapshot ->
            val canciones = snapshot.children.mapNotNull {
                it.getValue(Track::class.java)
            }

            adapter = CancionPlaylistAdapter(
                canciones = canciones,
                layout = R.layout.item_cancion_playlist
            )

            recyclerCanciones.layoutManager = LinearLayoutManager(context)
            recyclerCanciones.adapter = adapter
        }
    }

    private fun cargarComentarios() {
        val ref = database.child(uidAmigo).child("playlists").child(playlist.id).child("comentarios")
        val inflater = LayoutInflater.from(requireContext())
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contenedorComentarios.removeAllViews()
                for (comentarioSnap in snapshot.children) {
                    val comentario = comentarioSnap.getValue(Comentario::class.java)
                    if (comentario != null) {
                        val vista = inflater.inflate(R.layout.item_comentario, contenedorComentarios, false)
                        vista.findViewById<TextView>(R.id.autorComentario).text = comentario.autor
                        vista.findViewById<TextView>(R.id.textoComentario).text = comentario.texto
                        vista.findViewById<TextView>(R.id.fechaComentario).text = formato.format(Date(comentario.timestamp))
                        contenedorComentarios.addView(vista)
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
            "autor" to correoUsuario,
            "timestamp" to ServerValue.TIMESTAMP
        )

        database.child(uidAmigo).child("playlists").child(playlist.id)
            .child("comentarios").child(id).setValue(comentario)
    }
}
