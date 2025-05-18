package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.CancionAmigoAdapter
import com.example.deezerproyecto.models.Playlist

class DetallePlaylistAmigoFragment(private val playlist: Playlist) : Fragment() {

    private lateinit var recyclerCanciones: RecyclerView
    private lateinit var adapter: CancionAmigoAdapter
    private lateinit var nombrePlaylist: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_playlist_amigo, container, false)

        recyclerCanciones = view.findViewById(R.id.recyclerCanciones)
        nombrePlaylist = view.findViewById(R.id.nombrePlaylist)

        nombrePlaylist.text = playlist.nombre

        adapter = CancionAmigoAdapter(playlist.canciones)
        recyclerCanciones.layoutManager = LinearLayoutManager(context)
        recyclerCanciones.adapter = adapter

        return view
    }
}
