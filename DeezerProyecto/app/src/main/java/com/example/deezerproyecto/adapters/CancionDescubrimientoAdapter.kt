package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Track
import java.util.concurrent.TimeUnit

class CancionDescubrimientoAdapter(
    private val canciones: List<Track>,
    private val onItemClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<CancionDescubrimientoAdapter.CancionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cancion_descubrimiento_album, parent, false)
        return CancionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CancionViewHolder, position: Int) {
        holder.bind(canciones[position], position + 1)
    }

    override fun getItemCount(): Int = canciones.size

    inner class CancionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numeroCancion: TextView = itemView.findViewById(R.id.numeroCancion)
        private val tituloCancion: TextView = itemView.findViewById(R.id.tituloCancion)
        private val artistaCancion: TextView = itemView.findViewById(R.id.artistaCancion)
        private val duracionCancion: TextView = itemView.findViewById(R.id.duracionCancion)

        fun bind(track: Track, numero: Int) {
            numeroCancion.text = numero.toString()
            tituloCancion.text = track.title
            artistaCancion.text = track.artist.name
            duracionCancion.text = formatearDuracion(track.duration)

            itemView.setOnClickListener {
                onItemClick?.invoke(track)
            }
        }

        private fun formatearDuracion(segundos: Int): String {
            val minutos = TimeUnit.SECONDS.toMinutes(segundos.toLong())
            val segRestantes = segundos % 60
            return String.format("%d:%02d", minutos, segRestantes)
        }
    }
}
