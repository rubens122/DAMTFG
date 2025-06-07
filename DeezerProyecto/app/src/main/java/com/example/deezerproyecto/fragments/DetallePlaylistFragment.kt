package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.util.Base64
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

class DetallePlaylistFragment(private val playlist: Playlist) : Fragment() {

    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionPlaylistAdapter
    private lateinit var nombrePlaylist: TextView
    private lateinit var imagenPlaylist: ImageView
    private lateinit var botonEditar: Button
    private lateinit var textoPrivacidad: TextView
    private lateinit var textoVacio: TextView
    private lateinit var contenedorComentarios: LinearLayout
    private lateinit var inputComentario: EditText
    private lateinit var botonEnviarComentario: Button

    private val correoUsuario = FirebaseAuth.getInstance().currentUser?.email ?: "Anónimo"
    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_playlist, container, false)

        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylist)
        imagenPlaylist = view.findViewById(R.id.imagenPlaylist)
        botonEditar = view.findViewById(R.id.botonEditar)
        textoPrivacidad = view.findViewById(R.id.textoPrivacidad)
        textoVacio = view.findViewById(R.id.textoVacio)
        contenedorComentarios = view.findViewById(R.id.contenedorComentarios)
        inputComentario = view.findViewById(R.id.inputComentario)
        botonEnviarComentario = view.findViewById(R.id.botonEnviarComentario)

        nombrePlaylist.text = playlist.nombre
        textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        if (playlist.rutaFoto.startsWith("/9j/")) {
            try {
                val bytes = Base64.decode(playlist.rutaFoto, Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imagenPlaylist.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imagenPlaylist.setImageResource(R.drawable.placeholder_image)
            }
        } else {
            val url = playlist.rutaFoto.ifEmpty {
                "https://cdn-icons-png.flaticon.com/512/833/833281.png"
            }
            Picasso.get().load(url).fit().centerCrop().placeholder(R.drawable.placeholder_image).into(imagenPlaylist)
        }

        adapter = CancionPlaylistAdapter(
            playlist.canciones?.values?.toList()?.toMutableList() ?: mutableListOf(),
            R.layout.item_cancion_playlist
        ) { track ->
            registrarArtistaEscuchado(track.artist.name, track.artist.picture)
            eliminarCancion(track)
        }

        recyclerCanciones.layoutManager = LinearLayoutManager(context)
        recyclerCanciones.adapter = adapter

        textoVacio.visibility = if (playlist.canciones?.isEmpty() != false) View.VISIBLE else View.GONE

        botonEditar.visibility = View.VISIBLE
        botonEditar.setOnClickListener {
            val editarFragment = EditarPlaylistFragment(playlist)
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, editarFragment)
                .addToBackStack(null)
                .commit()
        }

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

    private fun eliminarCancion(track: Track) {
        val nuevasCanciones = playlist.canciones?.toMutableMap() ?: mutableMapOf()
        val claveAEliminar = nuevasCanciones.entries.find { it.value.id == track.id }?.key
        if (claveAEliminar != null) {
            nuevasCanciones.remove(claveAEliminar)

            uidActual?.let { uid ->
                database.child(uid).child("playlists").child(playlist.id)
                    .child("canciones").setValue(nuevasCanciones)
                    .addOnSuccessListener {
                        playlist.canciones = nuevasCanciones
                        adapter.actualizarCanciones(nuevasCanciones.values.toList().toMutableList())
                        textoVacio.visibility = if (nuevasCanciones.isEmpty()) View.VISIBLE else View.GONE
                    }
            }
        }
    }

    private fun cargarComentarios() {
        val ref = database.child(uidActual!!).child("playlists").child(playlist.id).child("comentarios")
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
                        val fechaTexto = if (comentario.timestamp is Long) {
                            formato.format(Date(comentario.timestamp))
                        } else "Sin fecha"
                        vista.findViewById<TextView>(R.id.fechaComentario).text = fechaTexto
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
        uidActual?.let { uid ->
            database.child(uid).child("playlists").child(playlist.id)
                .child("comentarios").child(id).setValue(comentario)
        }
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
