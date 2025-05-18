package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.AmigoAdapterBuscar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BuscarAmigosFragment : Fragment() {

    private lateinit var campoBusqueda: EditText
    private lateinit var botonBuscar: Button
    private lateinit var recyclerAmigos: RecyclerView
    private lateinit var adapter: AmigoAdapterBuscar
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buscar_amigos, container, false)

        campoBusqueda = view.findViewById(R.id.campoBusqueda)
        botonBuscar = view.findViewById(R.id.botonBuscar)
        recyclerAmigos = view.findViewById(R.id.recyclerAmigos)

        // 🔄 Inicialización del Adapter
        adapter = AmigoAdapterBuscar(mutableListOf()) { amigoId ->
            enviarSolicitudAmistad(amigoId)
        }

        recyclerAmigos.layoutManager = LinearLayoutManager(context)
        recyclerAmigos.adapter = adapter

        botonBuscar.setOnClickListener {
            val query = campoBusqueda.text.toString()
            if (query.isNotEmpty()) {
                buscarUsuarios(query)
            }
        }

        return view
    }

    /**
     * 🔄 Método para buscar usuarios en Firebase
     */
    private fun buscarUsuarios(nombre: String) {
        database.orderByChild("nombre").equalTo(nombre).get()
            .addOnSuccessListener { snapshot ->
                val listaAmigos = mutableListOf<String>()
                snapshot.children.forEach { usuario ->
                    val uid = usuario.key
                    if (uid != uidActual) {
                        listaAmigos.add(uid!!)
                    }
                }
                adapter.actualizarAmigos(listaAmigos)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al buscar usuarios", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * 🔄 Método para enviar solicitud de amistad
     */
    private fun enviarSolicitudAmistad(amigoId: String) {
        val referenciaAmigos = database.child(uidActual!!).child("solicitudes")
        referenciaAmigos.child(amigoId).setValue("pendiente")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al enviar solicitud", Toast.LENGTH_SHORT).show()
            }
    }
}
