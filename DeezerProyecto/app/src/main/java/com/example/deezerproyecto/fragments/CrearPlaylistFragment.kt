package com.example.deezerproyecto.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CrearPlaylistFragment : Fragment() {

    private lateinit var imagenPreview: ImageView
    private lateinit var botonSeleccionarImagen: ImageButton
    private lateinit var campoNombrePlaylist: EditText
    private lateinit var switchPrivacidad: Switch
    private lateinit var botonCrearPlaylist: Button

    private var imagenUri: Uri? = null
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val storage = FirebaseStorage.getInstance().getReference("imagenesPlaylists")
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crear_playlist, container, false)

        imagenPreview = view.findViewById(R.id.imagenPreview)
        botonSeleccionarImagen = view.findViewById(R.id.botonSeleccionarImagen)
        campoNombrePlaylist = view.findViewById(R.id.campoNombrePlaylist)
        switchPrivacidad = view.findViewById(R.id.switchPrivacidad)
        botonCrearPlaylist = view.findViewById(R.id.botonCrearPlaylist)

        botonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }

        botonCrearPlaylist.setOnClickListener {
            crearPlaylist()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imagenUri = data?.data
            imagenPreview.setImageURI(imagenUri)
        }
    }

    private fun crearPlaylist() {
        val nombre = campoNombrePlaylist.text.toString().trim()
        val esPrivada = switchPrivacidad.isChecked
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Escribe un nombre", Toast.LENGTH_SHORT).show()
            return
        }

        if (usuarioId == null) {
            Toast.makeText(requireContext(), "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val id = UUID.randomUUID().toString()
        val playlist = Playlist(
            id = id,
            nombre = nombre,
            esPrivada = esPrivada,
            rutaFoto = "",
            idUsuario = usuarioId
        )

        if (imagenUri != null) {
            val refImagen = storage.child("playlist_$id.jpg")

            refImagen.putFile(imagenUri!!)
                .addOnSuccessListener {
                    refImagen.downloadUrl.addOnSuccessListener { uri ->
                        playlist.rutaFoto = uri.toString()
                        Log.d("RutaFirebase", "URL guardada: ${playlist.rutaFoto}")
                        guardarPlaylist(playlist)
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al obtener URL", Toast.LENGTH_SHORT).show()
                        guardarPlaylist(playlist)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    guardarPlaylist(playlist)
                }
        } else {
            guardarPlaylist(playlist)
        }
    }

    private fun guardarPlaylist(playlist: Playlist) {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child(usuarioId).child("playlists").child(playlist.id).setValue(playlist)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Playlist creada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar playlist", Toast.LENGTH_SHORT).show()
            }
    }
}
