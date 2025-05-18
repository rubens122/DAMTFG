package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.R
import com.example.deezerproyecto.databinding.FragmentCrearPlaylistBinding
import com.google.firebase.auth.FirebaseAuth

class CrearPlaylistFragment : Fragment() {

    private var _binding: FragmentCrearPlaylistBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearPlaylistBinding.inflate(inflater, container, false)

        // Obtener el correo del usuario y mostrarlo
        val currentUser = auth.currentUser
        binding.nombreUsuario.text = currentUser?.email ?: "Usuario no identificado"

        // Lógica para el Switch de privacidad
        binding.switchPrivacidad.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.textPrivacidad.text = "Pública"
            } else {
                binding.textPrivacidad.text = "Privada"
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
