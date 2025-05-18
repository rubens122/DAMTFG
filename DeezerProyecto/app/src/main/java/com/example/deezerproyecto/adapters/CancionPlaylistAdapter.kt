package com.example.deezerproyecto.adapters

import android.app.AlertDialog
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
    private val canciones: MutableList<Track>,
    private val layout: Int,
    private val onEliminarCancion: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<CancionPlaylistAdapter.CancionViewHolder>() {

    private var posicionReproduciendo: Int = -1
    private var exoPlayer: ExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(parent.context).build()
        }
        return CancionViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: CancionViewHolder, position: Int) {
        holder.bind(canciones[position], position)
    }

    override fun getItemCount(): Int = canciones.size

    /**
     * 🔄 Método para actualizar las canciones
     */
    fun actualizarCanciones(nuevasCanciones: List<Track>) {
        canciones.clear()
        canciones.addAll(nuevasCanciones)
        notifyDataSetChanged()
    }

    /**
     * 🔄 Método para eliminar una canción del adapter y actualizar la vista
     */
    fun eliminarCancion(track: Track) {
        val posicion = canciones.indexOfFirst { it.id == track.id }
        if (posicion != -1) {
            canciones.removeAt(posicion)
            notifyItemRemoved(posicion)

            // 🔄 Si estaba sonando, pausamos el reproductor
            if (posicion == posicionReproduciendo) {
                exoPlayer?.stop()
                posicionReproduciendo = -1
            }
        }
    }

    inner class CancionViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {

        private val imagenAlbum: ImageView = itemView.findViewById(R.id.imagenAlbum)
        private val tituloCancion: TextView = itemView.findViewById(R.id.tituloCancion)
        private val artistaCancion: TextView = itemView.findViewById(R.id.artistaCancion)
        private val botonReproducir: ImageButton? = itemView.findViewById(R.id.botonReproducir)
        private val botonEliminar: ImageButton? = itemView.findViewById(R.id.botonEliminar)

        fun bind(track: Track, position: Int) {
            tituloCancion.text = track.title
            artistaCancion.text = track.artist.name

            Picasso.get()
                .load(track.album.cover)
                .into(imagenAlbum)

            // 🔄 Sincronización del botón con el estado
            if (position == posicionReproduciendo) {
                botonReproducir?.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                botonReproducir?.setImageResource(android.R.drawable.ic_media_play)
            }

            // 🔥 Lógica de reproducción
            botonReproducir?.setOnClickListener {
                val url = track.preview

                // 🔎 Verificamos que el URL no esté vacío
                if (url.isNullOrEmpty()) {
                    Toast.makeText(context, "No se puede reproducir esta canción.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 🔄 Si está reproduciendo, pausamos
                if (position == posicionReproduciendo) {
                    exoPlayer?.pause()
                    botonReproducir.setImageResource(android.R.drawable.ic_media_play)
                    posicionReproduciendo = -1
                } else {
                    // 🔄 Si hay otra canción en reproducción, la paramos
                    if (posicionReproduciendo != -1) {
                        notifyItemChanged(posicionReproduciendo)
                    }
                    posicionReproduciendo = position
                    iniciarReproduccion(url, context)
                    botonReproducir.setImageResource(android.R.drawable.ic_media_pause)
                }
            }

            // 🔥 Lógica para eliminar
            botonEliminar?.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Eliminar canción")
                    .setMessage("¿Estás seguro de que quieres eliminar esta canción de la playlist?")
                    .setPositiveButton("Sí") { _, _ ->
                        onEliminarCancion?.invoke(track)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }

    /**
     * 🔥 Método para iniciar la reproducción
     */
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
            Log.d("ExoPlayer", "🎵 Reproduciendo: $url")
        } catch (e: Exception) {
            Toast.makeText(context, "Error al reproducir la canción.", Toast.LENGTH_SHORT).show()
            Log.e("ExoPlayer", "❌ Error al reproducir: ${e.message}")
        }
    }
}
