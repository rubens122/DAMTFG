package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist

class TopPlaylistAdapter(
    private val lista: List<Triple<Playlist, String, Int>>, // Playlist, correoAutor, likes
    private val onClick: (playlist: Playlist, uidAutor: String) -> Unit
) : RecyclerView.Adapter<TopPlaylistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.textoNombrePlaylist)
        val autor: TextView = view.findViewById(R.id.textoAutor)
        val likes: TextView = view.findViewById(R.id.textoLikes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_playlist, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (playlist, correoAutor, cantidadLikes) = lista[position]

        holder.nombre.text = playlist.nombre
        holder.autor.text = "Autor: $correoAutor"
        holder.likes.text = "$cantidadLikes me gusta"

        holder.itemView.setOnClickListener {
            val uidAutor = playlist.idUsuario // ✅ usamos idUsuario que ya tienes en el modelo
            onClick(playlist, uidAutor)
        }
    }

    override fun getItemCount(): Int = lista.size
}
