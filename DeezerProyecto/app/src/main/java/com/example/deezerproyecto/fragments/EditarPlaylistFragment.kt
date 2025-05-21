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
import com.example.deezerproyecto.databinding.FragmentEditarPlaylistBinding
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class EditarPlaylistFragment(private val playlist: Playlist) : Fragment() {

    private var _binding: FragmentEditarPlaylistBinding? = null
    private val binding get() = _binding!!
    private var imagenUri: Uri? = null
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("usuarios")
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditarPlaylistBinding.inflate(inflater, container, false)

        binding.campoNombrePlaylist.setText(playlist.nombre)
        binding.switchPrivacidad.isChecked = !playlist.esPrivada

        if (playlist.rutaFoto.isNotEmpty()) {
            Picasso.get().load(playlist.rutaFoto).into(binding.imagenPlaylist)
        }

        binding.botonSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }

        binding.botonGuardarCambios.setOnClickListener {
            actualizarPlaylist()
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

    private fun actualizarPlaylist() {
        playlist.nombre = binding.campoNombrePlaylist.text.toString()
        playlist.esPrivada = !binding.switchPrivacidad.isChecked
        playlist.rutaFoto = imagenUri?.toString() ?: playlist.rutaFoto

        uidUsuario?.let { uid ->
            playlist.idUsuario = uid // ✅ Muy importante: conservar el dueño de la playlist

            reference.child(uid).child("playlists").child(playlist.id).setValue(playlist)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Playlist actualizada", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(requireContext(), "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
