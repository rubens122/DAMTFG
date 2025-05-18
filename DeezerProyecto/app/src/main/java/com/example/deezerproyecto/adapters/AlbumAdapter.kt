package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Album
import com.squareup.picasso.Picasso

class AlbumAdapter(
    private var albums: MutableList<Album>,
    private val onClickAlbum: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount(): Int = albums.size

    fun actualizarAlbums(nuevosAlbums: List<Album>) {
        albums.clear()
        albums.addAll(nuevosAlbums)
        notifyDataSetChanged()
    }

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenAlbum: ImageView = itemView.findViewById(R.id.imagenAlbum)
        private val tituloAlbum: TextView = itemView.findViewById(R.id.tituloAlbum)

        fun bind(album: Album) {
            tituloAlbum.text = album.title

            // 🔄 URL de alta calidad para el álbum
            val urlAltaCalidad = album.cover.replace("/image", "/image?size=1000x1000")

            // 🔄 Carga con Picasso en alta resolución
            Picasso.get()
                .load(urlAltaCalidad)
                .fit()
                .centerCrop()
                .into(imagenAlbum)

            itemView.setOnClickListener {
                onClickAlbum(album)
            }
        }
    }
}
