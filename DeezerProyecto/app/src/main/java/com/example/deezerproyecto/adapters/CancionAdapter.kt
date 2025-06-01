package com.example.deezerproyecto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.HomeActivity
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Track
import com.squareup.picasso.Picasso

class CancionAdapter(
    private val canciones: MutableList<Track>,
    private val layout: Int,
    private val onClickAdd: ((Track) -> Unit)? = null,
    private val onClickPlay: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<CancionAdapter.CancionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return CancionViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: CancionViewHolder, position: Int) {
        holder.bind(canciones[position])
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
        private val botonReproducir: ImageButton? = itemView.findViewById(R.id.botonReproducir)
        private val botonAnadir: ImageButton? = itemView.findViewById(R.id.botonAnadir)

        fun bind(track: Track) {
            tituloCancion.text = track.title
            artistaCancion.text = track.artist.name

            Picasso.get()
                .load(track.album.cover)
                .into(imagenAlbum)

            botonReproducir?.setImageResource(android.R.drawable.ic_media_play)

            botonReproducir?.setOnClickListener {
                onClickPlay?.invoke(track)  // <--- aquí disparamos el callback
                (context as? HomeActivity)?.iniciarBarra(
                    titulo = track.title,
                    artista = track.artist.name,
                    imagenUrl = track.album.cover,
                    urlCancion = track.preview
                )
            }

            botonAnadir?.setOnClickListener {
                onClickAdd?.invoke(track)
            }
        }
    }
}

