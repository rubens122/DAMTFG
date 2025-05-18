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
    private var playlists: MutableList<Playlist>,
    private val onClickPlaylist: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    /**
     * 🔄 Método para actualizar la lista de playlists
     */
    fun actualizarPlaylists(nuevasPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(nuevasPlaylists)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombrePlaylist: TextView = itemView.findViewById(R.id.nombrePlaylist)
        private val imagenPlaylist: ImageView = itemView.findViewById(R.id.imagenPlaylist)

        fun bind(playlist: Playlist) {
            nombrePlaylist.text = playlist.nombre
            if (!playlist.rutaFoto.isNullOrEmpty()) {
                Picasso.get().load(playlist.rutaFoto).into(imagenPlaylist)
            } else {
                imagenPlaylist.setImageResource(R.drawable.placeholder_image) // Imagen por defecto
            }

            itemView.setOnClickListener {
                onClickPlaylist(playlist)
            }
        }
    }
}
