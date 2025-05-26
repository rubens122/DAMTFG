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
import com.example.deezerproyecto.HomeActivity
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Track
import com.squareup.picasso.Picasso

class CancionPlaylistAdapter(
    private val canciones: List<Track>,
    private val layout: Int,
    private val onEliminarCancion: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<CancionPlaylistAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenAlbum)
        val titulo: TextView = view.findViewById(R.id.tituloCancion)
        val artista: TextView = view.findViewById(R.id.artistaCancion)
        val botonReproducir: ImageButton = view.findViewById(R.id.botonReproducir)
        val botonEliminar: ImageButton? = view.findViewById(R.id.botonEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = canciones[position]

        holder.titulo.text = track.title
        holder.artista.text = track.artist.name
        Picasso.get().load(track.album.cover).into(holder.imagen)

        val previewUrl = track.preview.trim()
        if (previewUrl.isEmpty()) {
            holder.botonReproducir.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Esta canción no tiene preview disponible.", Toast.LENGTH_SHORT).show()
            }
        } else {
            holder.botonReproducir.setOnClickListener {
                (holder.itemView.context as? HomeActivity)?.iniciarBarra(
                    titulo = track.title,
                    artista = track.artist.name,
                    imagenUrl = track.album.cover,
                    urlCancion = previewUrl
                )
            }
        }

        holder.botonEliminar?.setOnClickListener {
            onEliminarCancion?.invoke(track)
        }

        // Siempre mostramos el icono de play (la barra ya maneja el estado global)
        holder.botonReproducir.setImageResource(android.R.drawable.ic_media_play)
    }

    override fun getItemCount(): Int = canciones.size
}
