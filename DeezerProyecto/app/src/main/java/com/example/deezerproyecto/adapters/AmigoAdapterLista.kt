package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.databinding.ItemAmigoListaBinding
import com.example.deezerproyecto.models.Usuario

class AmigoAdapterLista(private val amigos: List<Usuario>) :
    RecyclerView.Adapter<AmigoAdapterLista.AmigoViewHolder>() {

    inner class AmigoViewHolder(val binding: ItemAmigoListaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val binding = ItemAmigoListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AmigoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val amigo = amigos[position]
        holder.binding.textoCorreo.text = amigo.correo
        // Puedes cargar imagen si tuviera: Picasso.get().load(amigo.imagenUrl).into(holder.binding.imagenPerfil)
    }

    override fun getItemCount(): Int = amigos.size
}
