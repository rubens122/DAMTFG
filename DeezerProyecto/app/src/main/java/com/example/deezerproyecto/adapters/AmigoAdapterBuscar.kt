package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Usuario

class AmigoAdapterBuscar(
    private var listaAmigos: MutableList<Usuario>,
    private val onEnviarSolicitud: (Usuario) -> Unit
) : RecyclerView.Adapter<AmigoAdapterBuscar.AmigoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_amigo_buscar, parent, false)
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
        private val textoCorreo: TextView = itemView.findViewById(R.id.textoCorreo)
        private val botonAgregar: Button = itemView.findViewById(R.id.botonAgregarAmigo)

        fun bind(usuario: Usuario) {
            textoCorreo.text = usuario.correo
            botonAgregar.setOnClickListener {
                onEnviarSolicitud(usuario)
            }
        }
    }
}
