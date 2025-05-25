package com.example.deezerproyecto.adapters

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.squareup.picasso.Picasso

class BibliotecaAdapter(
    private val listaInterna: MutableList<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<BibliotecaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.imagenPlaylist)
        val nombre: TextView = view.findViewById(R.id.nombrePlaylist)
        val subtitulo: TextView = view.findViewById(R.id.subtituloPlaylist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_biblioteca, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount(): Int = listaInterna.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = listaInterna[position]
        holder.nombre.text = playlist.nombre
        holder.subtitulo.text = "${playlist.canciones.size} canciones"

        if (playlist.rutaFoto.isNotEmpty()) {
            Picasso.get().load(playlist.rutaFoto).into(holder.imagen)
        } else {

        }

        holder.itemView.setOnClickListener {
            onClick(playlist)
        }
    }

    fun actualizar(nuevaLista: List<Playlist>) {
        listaInterna.clear()
        listaInterna.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
