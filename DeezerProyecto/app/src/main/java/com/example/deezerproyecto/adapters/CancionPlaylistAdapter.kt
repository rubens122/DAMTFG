package com.example.deezerproyecto.adapters

import android.content.Context
import android.util.Log
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

class CancionPlaylistAdapter(
    private val canciones: List<Track>,
    private val layout: Int,
    private val onEliminarCancion: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<CancionPlaylistAdapter.ViewHolder>() {

    private var exoPlayer: ExoPlayer? = null
    private var posicionReproduciendo: Int = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenAlbum)
        val titulo: TextView = view.findViewById(R.id.tituloCancion)
        val artista: TextView = view.findViewById(R.id.artistaCancion)
        val botonReproducir: ImageButton = view.findViewById(R.id.botonReproducir)
        val botonEliminar: ImageButton? = view.findViewById(R.id.botonEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(parent.context).build()
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = canciones[position]

        holder.titulo.text = track.title
        holder.artista.text = track.artist.name
        Picasso.get().load(track.album.cover).into(holder.imagen)

        val previewUrl = track.preview.trim()
        Log.d("Reproducir", "Preview URL: '$previewUrl'")

        holder.botonReproducir.setOnClickListener {
            if (previewUrl.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Esta canción no tiene preview disponible.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (position == posicionReproduciendo) {
                exoPlayer?.pause()
                holder.botonReproducir.setImageResource(android.R.drawable.ic_media_play)
                posicionReproduciendo = -1
            } else {
                if (posicionReproduciendo != -1) {
                    notifyItemChanged(posicionReproduciendo)
                }
                posicionReproduciendo = position
                iniciarReproduccion(previewUrl, holder.itemView.context)
                holder.botonReproducir.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        holder.botonEliminar?.setOnClickListener {
            onEliminarCancion?.invoke(track)
        }

        holder.botonReproducir.setImageResource(
            if (position == posicionReproduciendo)
                android.R.drawable.ic_media_pause
            else
                android.R.drawable.ic_media_play
        )
    }

    override fun getItemCount(): Int = canciones.size

    private fun iniciarReproduccion(url: String, context: Context) {
        try {
            exoPlayer?.stop()
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
            Log.d("ExoPlayer", "🎵 Reproduciendo: $url")
        } catch (e: Exception) {
            Toast.makeText(context, "No se puede reproducir esta canción.", Toast.LENGTH_SHORT).show()
            Log.e("ExoPlayer", "❌ Error al reproducir: ${e.message}")
        }
    }
}
