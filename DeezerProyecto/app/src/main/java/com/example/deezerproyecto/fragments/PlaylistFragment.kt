package com.example.deezerproyecto.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.databinding.FragmentPlaylistBinding
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.database.FirebaseDatabase

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()

        binding.botonCrearPlaylist.setOnClickListener {
            val nombre = binding.campoNombrePlaylist.text.toString()
            val esPrivada = binding.switchPrivacidad.isChecked
            val rutaFoto = imageUri?.toString() ?: ""

            if (nombre.isNotEmpty()) {
                val idUnico = database.getReference("playlists").push().key ?: ""

                // 🔥 Comprobación de datos para evitar errores en Firebase
                if (nombre.isNotEmpty() && idUnico.isNotEmpty()) {
                    val nuevaPlaylist = Playlist(idUnico, nombre, esPrivada, rutaFoto)
                    database.getReference("playlists").child(idUnico).setValue(nuevaPlaylist)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Playlist creada correctamente", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error al crear la playlist", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Datos inválidos, no se guardó la playlist.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
