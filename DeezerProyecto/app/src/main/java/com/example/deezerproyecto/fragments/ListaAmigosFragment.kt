package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.AmigoAdapterLista
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaAmigosFragment : Fragment() {

    private lateinit var recyclerAmigos: RecyclerView
    private lateinit var adapter: AmigoAdapterLista
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_amigos, container, false)

        recyclerAmigos = view.findViewById(R.id.recyclerAmigos)

        // 🔄 Inicialización del Adapter con la lista vacía
        adapter = AmigoAdapterLista(mutableListOf()) { amigo ->
            abrirPerfilAmigo(amigo.uid)
        }

        recyclerAmigos.layoutManager = LinearLayoutManager(context)
        recyclerAmigos.adapter = adapter

        cargarAmigos()

        return view
    }

    /**
     * 🔄 Cargar los amigos del usuario actual
     */
    private fun cargarAmigos() {
        if (uidActual != null) {
            val referenciaAmigos = database.child(uidActual).child("amigos")
            Log.d("ListaAmigosFragment", "📌 Cargando amigos del usuario: $uidActual")

            referenciaAmigos.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaAmigos = mutableListOf<Usuario>()
                    for (amigoSnapshot in snapshot.children) {
                        val amigoId = amigoSnapshot.key
                        if (amigoId != null) {
                            database.child(amigoId).get()
                                .addOnSuccessListener { dataSnapshot ->
                                    val nombre = dataSnapshot.child("nombre").value as? String ?: "Sin Nombre"
                                    val correo = dataSnapshot.child("correo").value as? String ?: "Sin Correo"
                                    val fotoUrl = dataSnapshot.child("imagenPerfil").value as? String
                                        ?: "https://cdn-icons-png.flaticon.com/512/1946/1946429.png"

                                    val usuario = Usuario(amigoId, nombre, correo, fotoUrl)
                                    listaAmigos.add(usuario)

                                    // 🔄 Actualizar adaptador
                                    adapter.actualizarAmigos(listaAmigos)
                                }
                                .addOnFailureListener {
                                    Log.e("ListaAmigosFragment", "Error al cargar datos del amigo: $amigoId")
                                }
                        }
                    }
                    Log.d("ListaAmigosFragment", "📌 Total de amigos encontrados: ${listaAmigos.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar amigos", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /**
     * 🔄 Abrir el perfil del amigo seleccionado
     */
    private fun abrirPerfilAmigo(amigoId: String) {
        val fragment = PerfilAmigoFragment(amigoId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
