package com.example.deezerproyecto.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.databinding.FragmentEditarPlaylistBinding
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditarPlaylistFragment(private val playlist: Playlist) : Fragment() {

    private var _binding: FragmentEditarPlaylistBinding? = null
    private val binding get() = _binding!!

    private var imagenUri: Uri? = null
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPlaylistBinding.inflate(inflater, container, false)

        binding.campoNombrePlaylist.setText(playlist.nombre)
        binding.switchPrivacidad.isChecked = playlist.esPrivada

        if (playlist.rutaFoto.startsWith("/9j/")) {
            try {
                val bytes = Base64.decode(playlist.rutaFoto, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                binding.imagenPlaylist.setImageBitmap(bitmap)
            } catch (e: Exception) {
                binding.imagenPlaylist.setImageResource(com.example.deezerproyecto.R.drawable.placeholder_image)
            }
        } else if (playlist.rutaFoto.isNotEmpty()) {
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
            val inputStream = requireContext().contentResolver.openInputStream(imagenUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val imagenBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

            playlist.rutaFoto = imagenBase64
        }

        actualizarCampos()
    }

    private fun actualizarCampos() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        if (usuarioId == null) {
            Toast.makeText(requireContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        playlist.idUsuario = usuarioId

        database.child(usuarioId).child("playlists").child(playlist.id)
            .setValue(playlist)
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
