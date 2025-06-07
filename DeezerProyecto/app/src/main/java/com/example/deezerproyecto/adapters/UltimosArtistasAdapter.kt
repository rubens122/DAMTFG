package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.squareup.picasso.Picasso

class UltimosArtistasAdapter(
    private val listaArtistas: List<Pair<String, String>>
) : RecyclerView.Adapter<UltimosArtistasAdapter.ArtistaViewHolder>() {

    class ArtistaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenArtista)
        val nombre: TextView = view.findViewById(R.id.nombreArtista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artista_escuchado, parent, false)
        return ArtistaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistaViewHolder, position: Int) {
        val (nombre, urlImagen) = listaArtistas[position]
        holder.nombre.text = nombre

        if (urlImagen.isNotEmpty()) {
            Picasso.get().load(urlImagen)
                .placeholder(R.drawable.ic_user)
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(R.drawable.ic_user)
        }
    }

    override fun getItemCount(): Int = listaArtistas.size
}
