package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.ActividadUsuario

class ActividadAdapter(private val lista: List<ActividadUsuario>) :
    RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    inner class ActividadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoDetalle: TextView = itemView.findViewById(R.id.textoDetalle)
        val textoCorreo: TextView = itemView.findViewById(R.id.textoCorreo)
        val textoFecha: TextView = itemView.findViewById(R.id.textoFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_actividad, parent, false)
        return ActividadViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = lista[position]
        holder.textoDetalle.text = actividad.detalle
        holder.textoCorreo.text = actividad.correo
        holder.textoFecha.text = actividad.fecha
    }

    override fun getItemCount(): Int = lista.size
}
