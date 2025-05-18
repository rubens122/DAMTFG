package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.databinding.ItemPlaylistBinding
import com.example.deezerproyecto.models.Playlist
import com.squareup.picasso.Picasso

class PlaylistAdapter(
    private val playlists: MutableList<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPlaylistBinding.bind(itemView)

        fun bind(playlist: Playlist) {
            binding.nombrePlaylist.text = playlist.nombre
            binding.privacidadPlaylist.text = if (playlist.esPrivada) "Privada" else "Pública"

            if (playlist.rutaFoto.isNotEmpty()) {
                Picasso.get()
                    .load(playlist.rutaFoto)
                    //.placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.imagenPlaylist)
            }

            itemView.setOnClickListener {
                onItemClick(playlist)
            }
        }
    }
}
