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

class CancionAmigoAdapter(
    private val canciones: MutableList<Track>,
    private val onAnadir: (Track) -> Unit
) : RecyclerView.Adapter<CancionAmigoAdapter.CancionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cancion, parent, false)
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

    inner class CancionViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {

        private val imagenAlbum: ImageView = itemView.findViewById(R.id.imagenAlbum)
        private val tituloCancion: TextView = itemView.findViewById(R.id.tituloCancion)
        private val artistaCancion: TextView = itemView.findViewById(R.id.artistaCancion)
        private val botonReproducir: ImageButton = itemView.findViewById(R.id.botonReproducir)
        private val botonAnadir: ImageButton = itemView.findViewById(R.id.botonAnadir)

        fun bind(track: Track) {
            tituloCancion.text = track.title
            artistaCancion.text = track.artist.name
            Picasso.get().load(track.album.cover).into(imagenAlbum)

            // Reproducir usando la barra global
            botonReproducir.setOnClickListener {
                if (track.preview.isNullOrEmpty()) {
                    Toast.makeText(context, "Esta canción no tiene preview.", Toast.LENGTH_SHORT).show()
                } else {
                    (context as? HomeActivity)?.iniciarBarra(
                        titulo = track.title,
                        artista = track.artist.name,
                        imagenUrl = track.album.cover,
                        urlCancion = track.preview
                    )
                }
            }

            // Añadir a playlist
            botonAnadir.setOnClickListener {
                onAnadir(track)
            }
        }
    }
}
