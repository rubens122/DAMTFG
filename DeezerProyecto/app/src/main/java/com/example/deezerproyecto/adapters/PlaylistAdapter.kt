package com.example.deezerproyecto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PlaylistAdapter(
    private val listaPlaylists: MutableList<Playlist>,
    private val soloContador: Boolean = false,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre: TextView = itemView.findViewById(R.id.nombrePlaylist)
        val textoPrivacidad: TextView = itemView.findViewById(R.id.privacidadPlaylist)
        val botonLike: ImageView = itemView.findViewById(R.id.botonLike)
        val textoLikes: TextView = itemView.findViewById(R.id.textoLikes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = listaPlaylists[position]
        holder.textoNombre.text = playlist.nombre
        holder.textoPrivacidad.text = if (playlist.esPrivada) "Privada" else "Pública"

        val referenciaLikes = FirebaseDatabase.getInstance().getReference("likes")
        val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (soloContador) {
            holder.botonLike.visibility = View.GONE
        } else {
            holder.botonLike.visibility = View.VISIBLE

            val refLike = referenciaLikes.child(playlist.id).child(usuarioActual)

            // 🔁 Escucha en tiempo real para cambiar el icono del corazón
            refLike.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leDioLike = snapshot.exists()
                    holder.botonLike.setImageResource(
                        if (leDioLike) R.drawable.ic_corazon_lleno else R.drawable.ic_corazon_vacio
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            // 🖱 Acción al pulsar el botón
            holder.botonLike.setOnClickListener {
                refLike.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        refLike.removeValue()
                    } else {
                        refLike.setValue(true)
                    }
                }
            }
        }

        // 🔢 Mostrar contador total de likes (siempre visible)
        referenciaLikes.child(playlist.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cantidad = snapshot.childrenCount
                    holder.textoLikes.text = "$cantidad me gusta"
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        // 📦 Acción al hacer clic en la tarjeta completa
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
