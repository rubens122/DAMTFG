package com.example.deezerproyecto.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.squareup.picasso.Picasso

class PlaylistSeleccionAdapter(
    private val playlists: List<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistSeleccionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenPlaylist)
        val nombre: TextView = view.findViewById(R.id.nombrePlaylist)
        val subtitulo: TextView = view.findViewById(R.id.textoCanciones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_dialog, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]

        holder.nombre.text = playlist.nombre
        holder.subtitulo.text = "${playlist.canciones?.size ?: 0} canciones"

        if (playlist.rutaFoto.isNotEmpty()) {
            if (playlist.rutaFoto.startsWith("/9j/")) {
                try {
                    val bytes = Base64.decode(playlist.rutaFoto, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.imagen.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    holder.imagen.setImageResource(R.drawable.placeholder_image)
                }
            } else {
                Picasso.get()
                    .load(playlist.rutaFoto)
                    .placeholder(R.drawable.placeholder_image)
                    .fit()
                    .centerCrop()
                    .into(holder.imagen)
            }
        } else {
            holder.imagen.setImageResource(R.drawable.placeholder_image)
        }

        holder.itemView.setOnClickListener {
            onClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size
}
