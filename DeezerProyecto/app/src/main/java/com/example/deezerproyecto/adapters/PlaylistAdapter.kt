package com.example.deezerproyecto.adapters

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PlaylistAdapter(
    private val listaPlaylists: MutableList<Playlist>,
    private val soloContador: Boolean = false,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.nombrePlaylist)
        val textoPrivacidad: TextView = itemView.findViewById(R.id.privacidadPlaylist)
        val botonLike: ImageView = itemView.findViewById(R.id.botonLike)
        val textoLikes: TextView = itemView.findViewById(R.id.textoLikes)
        val imagenPlaylist: ImageView = itemView.findViewById(R.id.imagenPlaylist)
        val botonEliminar: ImageView = itemView.findViewById(R.id.botonEliminarPlaylist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = listaPlaylists[position]
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val referenciaLikes = FirebaseDatabase.getInstance().getReference("likes")
        val referenciaDB = FirebaseDatabase.getInstance().getReference("usuarios").child(uid).child("playlists")

        holder.textoNombre.text = playlist.nombre
        holder.textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        if (playlist.rutaFoto.isNotEmpty()) {
            if (playlist.rutaFoto.startsWith("/9j/")) {
                try {
                    val bytes = Base64.decode(playlist.rutaFoto, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.imagenPlaylist.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    holder.imagenPlaylist.setImageResource(R.drawable.ic_user)
                }
            } else {
                Picasso.get()
                    .load(playlist.rutaFoto)
                    .placeholder(R.drawable.ic_user)
                    .fit()
                    .centerCrop()
                    .into(holder.imagenPlaylist)
            }
        } else {
            holder.imagenPlaylist.setImageResource(R.drawable.ic_user)
        }

        if (soloContador) {
            holder.botonLike.visibility = View.GONE
            holder.botonEliminar.visibility = View.GONE
        } else {
            holder.botonLike.visibility = View.VISIBLE
            holder.botonEliminar.visibility = View.VISIBLE

            val refLike = referenciaLikes.child(playlist.id).child(uid)
            refLike.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.botonLike.setImageResource(
                        if (snapshot.exists()) R.drawable.ic_corazon_lleno else R.drawable.ic_corazon_vacio
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            holder.botonLike.setOnClickListener {
                refLike.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        refLike.removeValue()
                    } else {
                        refLike.setValue(true)
                    }
                }
            }

            holder.botonEliminar.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Eliminar playlist")
                    .setMessage("¿Seguro que deseas eliminar la playlist \"${playlist.nombre}\"?")
                    .setPositiveButton("Sí") { _, _ ->
                        referenciaDB.child(playlist.id).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Playlist eliminada", Toast.LENGTH_SHORT).show()
                                listaPlaylists.removeAt(holder.adapterPosition)
                                notifyItemRemoved(holder.adapterPosition)
                            }
                            .addOnFailureListener {
                                Toast.makeText(holder.itemView.context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

        referenciaLikes.child(playlist.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cantidad = snapshot.childrenCount
                    holder.textoLikes.text = "$cantidad me gusta"
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        holder.itemView.setOnClickListener {
            onItemClick(playlist)
        }
    }

    override fun getItemCount(): Int = listaPlaylists.size

    fun actualizarPlaylists(nuevaLista: List<Playlist>) {
        listaPlaylists.clear()
        listaPlaylists.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
