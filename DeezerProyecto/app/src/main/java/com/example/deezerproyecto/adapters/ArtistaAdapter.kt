package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Artist
import com.squareup.picasso.Picasso

class ArtistaAdapter(
    private var artistas: MutableList<Artist>,
    private val onClickArtista: (Artist) -> Unit
) : RecyclerView.Adapter<ArtistaAdapter.ArtistaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artista, parent, false)
        return ArtistaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistaViewHolder, position: Int) {
        holder.bind(artistas[position])
    }

    override fun getItemCount(): Int = artistas.size

    fun actualizarArtistas(nuevosArtistas: List<Artist>) {
        artistas.clear()
        artistas.addAll(nuevosArtistas)
        notifyDataSetChanged()
    }

    inner class ArtistaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenArtista: ImageView = itemView.findViewById(R.id.imagenArtista)
        private val nombreArtista: TextView = itemView.findViewById(R.id.nombreArtista)

        fun bind(artist: Artist) {
            nombreArtista.text = artist.name
            val urlAltaCalidad = artist.picture_xl.ifEmpty { artist.picture }
            Picasso.get()
                .load(urlAltaCalidad)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(imagenArtista)

            itemView.setOnClickListener {
                onClickArtista(artist)
            }
        }
    }
}
