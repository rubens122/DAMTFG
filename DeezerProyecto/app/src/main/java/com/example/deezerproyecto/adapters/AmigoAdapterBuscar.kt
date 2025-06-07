package com.example.deezerproyecto.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

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

        val animacion = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        holder.itemView.startAnimation(animacion)

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
        private val imagenPerfil: ImageView = itemView.findViewById(R.id.imagenPerfil)

        fun bind(usuario: Usuario) {
            nombreUsuario.text = usuario.correo

            val refUsuario = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(usuario.uid)

            refUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val base64 = snapshot.child("imagenPerfilBase64").value as? String
                    val url = snapshot.child("imagenPerfil").value as? String

                    when {
                        !base64.isNullOrEmpty() -> {
                            val bytes = Base64.decode(base64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            imagenPerfil.setImageBitmap(bitmap)
                        }
                        !url.isNullOrEmpty() -> {
                            Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.ic_user)
                                .fit()
                                .centerCrop()
                                .into(imagenPerfil)
                        }
                        else -> {
                            imagenPerfil.setImageResource(R.drawable.ic_user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val refAmigo = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uidActual)
                .child("amigos")
                .child(usuario.uid)

            refAmigo.addValueEventListener(object : ValueEventListener {
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
