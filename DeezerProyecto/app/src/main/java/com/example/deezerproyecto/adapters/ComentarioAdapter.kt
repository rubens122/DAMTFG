package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Comentario
import java.text.SimpleDateFormat
import java.util.*

class ComentarioAdapter(private val comentarios: List<Comentario>) :
    RecyclerView.Adapter<ComentarioAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val autor: TextView = view.findViewById(R.id.autorComentario)
        val texto: TextView = view.findViewById(R.id.textoComentario)
        val fecha: TextView = view.findViewById(R.id.fechaComentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.autor.text = comentario.autor
        holder.texto.text = comentario.texto

        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaLegible = formatoFecha.format(Date(comentario.timestamp))
        holder.fecha.text = fechaLegible
    }

    override fun getItemCount(): Int = comentarios.size
}
