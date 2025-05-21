package com.example.deezerproyecto.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.databinding.FragmentCrearPlaylistBinding
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CrearPlaylistFragment : Fragment() {

    private var _binding: FragmentCrearPlaylistBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearPlaylistBinding.inflate(inflater, container, false)

        binding.botonSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }

        binding.botonCrearPlaylist.setOnClickListener {
            crearPlaylist()
        }

        return binding.root
    }

    private fun crearPlaylist() {
        val nombre = binding.campoNombrePlaylist.text.toString().trim()
        val esPrivada = binding.switchPrivacidad.isChecked

        if (nombre.isNotEmpty()) {
            val idPlaylist = database.reference.push().key ?: return
            if (imageUri != null) {
                subirImagenAFirebase(imageUri!!) { url ->
                    guardarPlaylistEnFirebase(idPlaylist, nombre, esPrivada, url)
                }
            } else {
                guardarPlaylistEnFirebase(idPlaylist, nombre, esPrivada, "")
            }
        } else {
            Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subirImagenAFirebase(uri: Uri, onSuccess: (String) -> Unit) {
        val nombreArchivo = "playlist_${UUID.randomUUID()}.jpg"
        val referenciaStorage = storage.getReference("imagenes_playlists/$nombreArchivo")

        referenciaStorage.putFile(uri)
            .addOnSuccessListener {
                referenciaStorage.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                    Toast.makeText(requireContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarPlaylistEnFirebase(idPlaylist: String, nombre: String, esPrivada: Boolean, rutaFoto: String) {
        if (uidUsuario == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevaPlaylist = Playlist(
            id = idPlaylist,
            nombre = nombre,
            esPrivada = esPrivada,
            rutaFoto = rutaFoto,
            idUsuario = uidUsuario // ✅ Guardamos el UID
        )

        database.reference.child("usuarios").child(uidUsuario)
            .child("playlists").child(idPlaylist)
            .setValue(nuevaPlaylist)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Playlist creada correctamente", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al crear la playlist", Toast.LENGTH_SHORT).show()
            }

        // Opcional: también guardar en nodo global
        database.reference.child("playlists").child(idPlaylist).setValue(nuevaPlaylist)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.imagenPreview.setImageURI(imageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
