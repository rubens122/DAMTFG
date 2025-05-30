package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.squareup.picasso.Picasso

class PlaylistAdapter(
    private val playlists: MutableList<Playlist>,
    private val onClickPlaylist: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nombre: TextView = view.findViewById(R.id.nombrePlaylist)
        private val canciones: TextView = view.findViewById(R.id.textoCanciones)
        private val imagen: ImageView = view.findViewById(R.id.imagenPlaylist)

        fun bind(playlist: Playlist) {
            nombre.text = playlist.nombre
            canciones.text = "${playlist.canciones?.size ?: 0} canciones"

            if (playlist.rutaFoto.isNotEmpty()) {
                Picasso.get().load(playlist.rutaFoto).into(imagen)
            } else {
                imagen.setImageResource(R.drawable.placeholder_image)
            }

            itemView.setOnClickListener {
                onClickPlaylist(playlist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_dialog, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun actualizarPlaylists(nuevas: List<Playlist>) {
        playlists.clear()
        playlists.addAll(nuevas)
        notifyDataSetChanged()
    }
}
