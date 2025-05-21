package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

        adapter = AmigoAdapterBuscar(mutableListOf()) { amigo ->
            enviarSolicitudAmistad(amigo.uid)
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

    private fun buscarUsuarios(correo: String) {
        Log.d("BuscarAmigosFragment", "🔍 Buscando usuarios con correo: $correo")
        database.orderByChild("correo").equalTo(correo).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val listaAmigos = mutableListOf<Usuario>()
                    snapshot.children.forEach { usuarioSnapshot ->
                        val usuario = usuarioSnapshot.getValue(Usuario::class.java)
                        if (usuario != null && usuarioSnapshot.key != uidActual) {
                            val usuarioConUid = usuario.copy(uid = usuarioSnapshot.key!!)
                            listaAmigos.add(usuarioConUid)
                        }
                    }
                    adapter.actualizarAmigos(listaAmigos)
                } else {
                    Log.w("BuscarAmigosFragment", "⚠️ No se encontraron usuarios con ese correo")
                    Toast.makeText(requireContext(), "No se encontraron usuarios con ese correo", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Log.e("BuscarAmigosFragment", "❌ Error al buscar usuarios: ${it.message}")
                Toast.makeText(requireContext(), "Error al buscar usuarios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enviarSolicitudAmistad(amigoId: String) {
        val uid = uidActual
        if (uid == null) {
            Log.e("Amigos", "❌ uidActual es null")
            return
        }

        Log.d("Amigos", "📤 Agregando amigo $amigoId a $uid")

        val usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios")
        val referenciaAmigos = usuariosRef.child(uid).child("amigos")

        referenciaAmigos.get().addOnSuccessListener { snapshot ->
            val nuevosDatos = mutableMapOf<String, Any?>()

            snapshot.children.forEach {
                val key = it.key
                val value = it.value
                if (key != null && value != null) {
                    nuevosDatos[key] = value
                }
            }

            nuevosDatos[amigoId] = true

            referenciaAmigos.setValue(nuevosDatos)
                .addOnSuccessListener {
                    Log.d("Amigos", "✅ Amigo agregado correctamente")
                    Toast.makeText(requireContext(), "Solicitud enviada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.e("Amigos", "❌ Error al agregar amigo: ${it.message}")
                    Toast.makeText(requireContext(), "Error al enviar solicitud", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Log.e("Amigos", "❌ Error al leer nodo amigos: ${it.message}")
            Toast.makeText(requireContext(), "Error al preparar la solicitud", Toast.LENGTH_SHORT).show()
        }
    }
}