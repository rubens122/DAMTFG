package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.AmigoAdapterLista
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
        adapter = AmigoAdapterLista(mutableListOf()) { amigoId ->
            abrirPerfilAmigo(amigoId)
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
        val referenciaAmigos = database.child(uidActual!!).child("amigos")

        referenciaAmigos.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaAmigos = mutableListOf<String>()
                for (amigoSnapshot in snapshot.children) {
                    listaAmigos.add(amigoSnapshot.key!!)
                }
                adapter.actualizarAmigos(listaAmigos)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar amigos", Toast.LENGTH_SHORT).show()
            }
        })
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
