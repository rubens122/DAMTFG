package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Usuario
import com.squareup.picasso.Picasso

class AmigoAdapterLista(
    private var listaAmigos: MutableList<Usuario>,
    private val onAmigoClick: (Usuario) -> Unit
) : RecyclerView.Adapter<AmigoAdapterLista.AmigoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_amigo_lista, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        holder.bind(listaAmigos[position])
    }

    override fun getItemCount(): Int = listaAmigos.size

    fun actualizarAmigos(nuevaLista: List<Usuario>) {
        listaAmigos.clear()
        listaAmigos.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenPerfil: ImageView = itemView.findViewById(R.id.imagenPerfilAmigo)
        private val correoAmigo: TextView = itemView.findViewById(R.id.textoCorreoAmigo)

        fun bind(usuario: Usuario) {
            correoAmigo.text = usuario.correo
            if (usuario.imagenPerfil.isNotEmpty()) {
                Picasso.get().load(usuario.imagenPerfil).into(imagenPerfil)
            } else {
                Picasso.get().load("https://cdn-icons-png.flaticon.com/512/1946/1946429.png").into(imagenPerfil)
            }
            itemView.setOnClickListener {
                onAmigoClick(usuario)
            }
        }
    }
}
