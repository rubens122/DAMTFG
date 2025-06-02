package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.database.*

class AmigoAdapterBuscar(
    private var lista: MutableList<Usuario>,
    private val uidActual: String,
    private val onAgregarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<AmigoAdapterBuscar.AmigoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amigo_buscar, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val amigo = lista[position]
        holder.bind(amigo)

        // ✅ Animación fade-in
        val animacion = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        holder.itemView.startAnimation(animacion)

        // ✅ Click para agregar amigo
        holder.botonAgregar.setOnClickListener {
            onAgregarClick(amigo)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarAmigos(nuevaLista: List<Usuario>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreUsuario: TextView = itemView.findViewById(R.id.nombreUsuario)
        val botonAgregar: Button = itemView.findViewById(R.id.botonAgregar)

        fun bind(usuario: Usuario) {
            nombreUsuario.text = usuario.correo

            val referenciaAmigo = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uidActual)
                .child("amigos")
                .child(usuario.uid)

            referenciaAmigo.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val esAmigo = snapshot.exists()
                    botonAgregar.text = if (esAmigo) "Agregado" else "Agregar"
                    botonAgregar.isEnabled = !esAmigo
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
