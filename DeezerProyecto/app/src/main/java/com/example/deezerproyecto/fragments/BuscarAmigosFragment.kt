package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezerproyecto.adapters.AmigoAdapterBuscar
import com.example.deezerproyecto.databinding.FragmentBuscarAmigosBinding
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BuscarAmigosFragment : Fragment() {

    private lateinit var binding: FragmentBuscarAmigosBinding
    private lateinit var adapter: AmigoAdapterBuscar
    private lateinit var uidActual: String
    private val databaseRef = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuscarAmigosBinding.inflate(inflater, container, false)
        uidActual = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        adapter = AmigoAdapterBuscar(mutableListOf(), uidActual) { usuario ->
            agregarAmigo(usuario)
        }

        binding.recyclerAmigos.layoutManager = LinearLayoutManager(context)
        binding.recyclerAmigos.adapter = adapter

        cargarSugerenciasIniciales()

        binding.campoBusqueda.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    cargarSugerenciasIniciales()
                } else {
                    buscarPorCorreo(query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return binding.root
    }

    private fun cargarSugerenciasIniciales() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sugerencias = mutableListOf<Usuario>()
                val amigosRef = snapshot.child(uidActual).child("amigos")

                snapshot.children.forEach {
                    val usuario = it.getValue(Usuario::class.java)
                    if (usuario != null && it.key != uidActual && amigosRef.child(it.key!!).value != true) {
                        usuario.uid = it.key!!
                        sugerencias.add(usuario)
                    }
                }

                adapter.actualizarAmigos(sugerencias)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al cargar sugerencias", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun buscarPorCorreo(correo: String) {
        databaseRef.orderByChild("correo").startAt(correo).endAt("$correo\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val encontrados = mutableListOf<Usuario>()
                    snapshot.children.forEach {
                        val usuario = it.getValue(Usuario::class.java)
                        if (usuario != null && it.key != uidActual) {
                            usuario.uid = it.key!!
                            encontrados.add(usuario)
                        }
                    }

                    adapter.actualizarAmigos(encontrados)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al buscar usuarios", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun agregarAmigo(usuario: Usuario) {
        val ref = databaseRef.child(uidActual).child("amigos").child(usuario.uid)
        ref.setValue(true)
            .addOnSuccessListener {
                Toast.makeText(context, "Amigo agregado correctamente", Toast.LENGTH_SHORT).show()
                cargarSugerenciasIniciales()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al agregar amigo", Toast.LENGTH_SHORT).show()
            }
    }
}
