package com.example.deezerproyecto.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.databinding.FragmentEditarPlaylistBinding
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditarPlaylistFragment(private val playlist: Playlist) : Fragment() {

    private var _binding: FragmentEditarPlaylistBinding? = null
    private val binding get() = _binding!!

    private var imagenUri: Uri? = null
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val storage = FirebaseStorage.getInstance().getReference("imagenesPlaylists")
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPlaylistBinding.inflate(inflater, container, false)

        binding.campoNombrePlaylist.setText(playlist.nombre)
        binding.switchPrivacidad.isChecked = playlist.esPrivada

        if (playlist.rutaFoto.isNotEmpty()) {
            Picasso.get().load(playlist.rutaFoto).into(binding.imagenPlaylist)
        }

        binding.botonSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }

        binding.botonGuardarCambios.setOnClickListener {
            guardarCambios()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imagenUri = data?.data
            binding.imagenPlaylist.setImageURI(imagenUri)
        }
    }

    private fun guardarCambios() {
        playlist.nombre = binding.campoNombrePlaylist.text.toString()
        playlist.esPrivada = binding.switchPrivacidad.isChecked

        if (imagenUri != null) {
            val nombreImagen = "playlist_${playlist.id}.jpg"
            val refImagen = storage.child(nombreImagen)

            refImagen.putFile(imagenUri!!)
                .addOnSuccessListener {
                    refImagen.downloadUrl.addOnSuccessListener { uri ->
                        playlist.rutaFoto = uri.toString()
                        actualizarCampos()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al obtener URL", Toast.LENGTH_SHORT).show()
                        actualizarCampos()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    actualizarCampos()
                }
        } else {
            actualizarCampos()
        }
    }

    private fun actualizarCampos() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        if (usuarioId == null) {
            Toast.makeText(requireContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        // Aseguramos que idUsuario esté actualizado
        playlist.idUsuario = usuarioId

        val ref = database.child(usuarioId).child("playlists").child(playlist.id)
        ref.setValue(playlist)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Playlist actualizada", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
