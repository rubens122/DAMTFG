package com.example.deezerproyecto.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.deezerproyecto.R
import com.example.deezerproyecto.databinding.FragmentPlaylistsPublicasBinding

class PlaylistsPublicasFragment : Fragment() {

    private var _binding: FragmentPlaylistsPublicasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsPublicasBinding.inflate(inflater, container, false)

        // 🔹 Navegar a Buscar Amigos
        binding.botonBuscarAmigos.setOnClickListener {
            val fragment = BuscarAmigosFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        // 🔹 Navegar a Lista de Amigos
        binding.botonListaAmigos.setOnClickListener {
            val fragment = ListaAmigosFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
