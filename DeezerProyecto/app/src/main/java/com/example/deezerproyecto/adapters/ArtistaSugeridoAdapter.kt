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

class ArtistaSugeridoAdapter(
    private val artistas: List<Artist>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ArtistaSugeridoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenArtista)
        val nombre: TextView = view.findViewById(R.id.nombreArtista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artista_sugerido, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artista = artistas[position]
        holder.nombre.text = artista.name

        Picasso.get()
            .load(artista.picture_xl) // ✅ calidad alta
            .resize(300, 300)         // Ajusta según el tamaño del ImageView
            .centerCrop()
            .into(holder.imagen)


        holder.itemView.setOnClickListener {
            onClick(artista.name)
        }
    }

    override fun getItemCount(): Int = artistas.size
}
