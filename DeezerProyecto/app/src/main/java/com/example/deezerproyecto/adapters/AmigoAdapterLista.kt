package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R

class AmigoAdapterLista(
    private var amigos: MutableList<String>,
    private val onClickAmigo: (String) -> Unit
) : RecyclerView.Adapter<AmigoAdapterLista.AmigoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_amigo_lista, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        holder.bind(amigos[position])
    }

    override fun getItemCount(): Int = amigos.size

    fun actualizarAmigos(nuevosAmigos: List<String>) {
        amigos.clear()
        amigos.addAll(nuevosAmigos)
        notifyDataSetChanged()
    }

    inner class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreAmigo: TextView = itemView.findViewById(R.id.nombreAmigoLista)

        fun bind(amigoId: String) {
            nombreAmigo.text = amigoId
            itemView.setOnClickListener {
                onClickAmigo(amigoId)
            }
        }
    }
}
