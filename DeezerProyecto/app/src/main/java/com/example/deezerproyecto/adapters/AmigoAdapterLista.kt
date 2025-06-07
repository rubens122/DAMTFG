package com.example.deezerproyecto.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.databinding.ItemAmigoListaBinding
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class AmigoAdapterLista(
    private val amigos: List<Usuario>,
    private val uidActual: String,
    private val onClick: (Usuario) -> Unit
) : RecyclerView.Adapter<AmigoAdapterLista.AmigoViewHolder>() {

    inner class AmigoViewHolder(val binding: ItemAmigoListaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val binding = ItemAmigoListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AmigoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val amigo = amigos[position]

        holder.binding.textoCorreo.text = amigo.correo
        val ref = FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(amigo.uid)
        ref.get().addOnSuccessListener { snapshot ->
            val base64 = snapshot.child("imagenPerfilBase64").value as? String
            val url = snapshot.child("imagenPerfil").value as? String

            when {
                !base64.isNullOrEmpty() -> {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.binding.imagenPerfil.setImageBitmap(bitmap)
                }

                !url.isNullOrEmpty() -> {
                    Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.ic_user)
                        .fit()
                        .centerCrop()
                        .into(holder.binding.imagenPerfil)
                }

                else -> {
                    holder.binding.imagenPerfil.setImageResource(R.drawable.ic_user)
                }
            }
        }

        holder.binding.root.setOnClickListener {
            onClick(amigo)
        }

        holder.binding.botonEliminar.setOnClickListener {
            android.app.AlertDialog.Builder(holder.binding.root.context)
                .setTitle("Eliminar amigo")
                .setMessage("¿Seguro que deseas eliminar a ${amigo.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    val refEliminar = FirebaseDatabase.getInstance().getReference("usuarios")
                    refEliminar.child(uidActual).child("amigos").child(amigo.uid).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(holder.binding.root.context, "Amigo eliminado", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(holder.binding.root.context, "Error al eliminar amigo", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun getItemCount(): Int = amigos.size
}
