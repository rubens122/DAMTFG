package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Track
import com.squareup.picasso.Picasso

class CancionHomeAdapter(
    private var canciones: MutableList<Track>,
    private val onClickCancion: (Track) -> Unit
) : RecyclerView.Adapter<CancionHomeAdapter.CancionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cancion_home, parent, false)
        return CancionViewHolder(view)
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

    inner class CancionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenCancion: ImageView = itemView.findViewById(R.id.imagenCancion)
        private val tituloCancion: TextView = itemView.findViewById(R.id.tituloCancion)

        fun bind(track: Track) {
            tituloCancion.text = track.title

            // 🔄 Validación de la URL
            if (track.album.cover.isNotEmpty()) {
                Picasso.get()
                    .load(track.album.cover)
                    .fit()
                    .centerCrop()
                    .into(imagenCancion)
            }

            itemView.setOnClickListener {
                onClickCancion(track)
            }
        }
    }
}
