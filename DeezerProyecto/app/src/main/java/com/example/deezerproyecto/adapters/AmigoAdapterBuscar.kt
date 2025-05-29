package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.database.*

class AmigoAdapterBuscar(
    private var lista: MutableList<Usuario>,
    private val uidActual: String
) : RecyclerView.Adapter<AmigoAdapterBuscar.AmigoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amigo_buscar, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarAmigos(nuevaLista: List<Usuario>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre = itemView.findViewById<TextView>(R.id.nombreUsuario)
        private val boton = itemView.findViewById<Button>(R.id.botonAgregar)

        fun bind(usuario: Usuario) {
            nombre.text = usuario.correo

            val referenciaAmigo = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uidActual)
                .child("amigos")
                .child(usuario.uid)

            referenciaAmigo.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val esAmigo = snapshot.exists()
                    boton.text = if (esAmigo) "Agregado" else "Agregar"
                    boton.isEnabled = !esAmigo
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            boton.setOnClickListener {
                referenciaAmigo.setValue(true)
            }
        }
    }
}
