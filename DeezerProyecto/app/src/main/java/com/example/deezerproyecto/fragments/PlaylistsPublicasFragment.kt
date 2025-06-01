package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.R
import com.example.deezerproyecto.databinding.FragmentPlaylistsPublicasBinding

class PlaylistsPublicasFragment : Fragment() {

    private var _binding: FragmentPlaylistsPublicasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsPublicasBinding.inflate(inflater, container, false)

        binding.botonBuscarAmigos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, BuscarAmigosFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.botonMisAmigos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, ListaAmigosFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.botonTopPlaylists.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragment, TopPlaylistsFragment())
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
