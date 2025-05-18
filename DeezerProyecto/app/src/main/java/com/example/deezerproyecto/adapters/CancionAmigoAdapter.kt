package com.example.deezerproyecto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Track
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.squareup.picasso.Picasso

class CancionAmigoAdapter(
    private val canciones: MutableList<Track>
) : RecyclerView.Adapter<CancionAmigoAdapter.CancionViewHolder>() {

    private var posicionReproduciendo: Int = -1
    private var exoPlayer: ExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cancion, parent, false)
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(parent.context).build()
        }
        return CancionViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: CancionViewHolder, position: Int) {
        holder.bind(canciones[position], position)
    }

    override fun getItemCount(): Int = canciones.size

    fun actualizarCanciones(nuevasCanciones: List<Track>) {
        canciones.clear()
        canciones.addAll(nuevasCanciones)
        notifyDataSetChanged()
    }

    inner class CancionViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        private val imagenAlbum: ImageView = itemView.findViewById(R.id.imagenAlbum)
        private val tituloCancion: TextView = itemView.findViewById(R.id.tituloCancion)
        private val artistaCancion: TextView = itemView.findViewById(R.id.artistaCancion)
        private val botonReproducir: ImageButton = itemView.findViewById(R.id.botonReproducir)

        fun bind(track: Track, position: Int) {
            tituloCancion.text = track.title
            artistaCancion.text = track.artist.name

            Picasso.get()
                .load(track.album.cover)
                .into(imagenAlbum)

            // 🔥 Lógica de reproducción
            botonReproducir.setOnClickListener {
                val url = track.preview
                if (url.isNullOrEmpty()) {
                    Toast.makeText(context, "No se puede reproducir esta canción.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (position == posicionReproduciendo) {
                    exoPlayer?.pause()
                    botonReproducir.setImageResource(android.R.drawable.ic_media_play)
                    posicionReproduciendo = -1
                } else {
                    if (posicionReproduciendo != -1) {
                        notifyItemChanged(posicionReproduciendo)
                    }
                    posicionReproduciendo = position
                    iniciarReproduccion(url, context)
                    botonReproducir.setImageResource(android.R.drawable.ic_media_pause)
                }
            }
        }
    }

    private fun iniciarReproduccion(url: String, context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }

        try {
            exoPlayer?.stop()
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al reproducir la canción.", Toast.LENGTH_SHORT).show()
        }
    }
}
