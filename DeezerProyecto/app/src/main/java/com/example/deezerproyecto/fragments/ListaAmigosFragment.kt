package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezerproyecto.adapters.AmigoAdapterLista
import com.example.deezerproyecto.databinding.FragmentListaAmigosBinding
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaAmigosFragment : Fragment() {

    private var _binding: FragmentListaAmigosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AmigoAdapterLista
    private val listaAmigos = mutableListOf<Usuario>()
    private val database = FirebaseDatabase.getInstance().reference
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaAmigosBinding.inflate(inflater, container, false)

        adapter = AmigoAdapterLista(listaAmigos)
        binding.recyclerAmigos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAmigos.adapter = adapter

        if (uidUsuario != null) {
            cargarAmigos(uidUsuario)
        }

        return binding.root
    }

    private fun cargarAmigos(uid: String) {
        binding.progressBarAmigos.visibility = View.VISIBLE

        database.child("usuarios").child(uid).child("amigos").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idsAmigos = snapshot.children.mapNotNull { it.key }
                listaAmigos.clear()

                if (idsAmigos.isEmpty()) {
                    binding.progressBarAmigos.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    return
                }

                var cargados = 0
                for (id in idsAmigos) {
                    database.child("usuarios").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dataSnapshot.getValue(Usuario::class.java)?.let {
                                it.uid = dataSnapshot.key ?: ""
                                listaAmigos.add(it)
                            }
                            cargados++
                            if (cargados == idsAmigos.size) {
                                adapter.notifyDataSetChanged()
                                binding.progressBarAmigos.visibility = View.GONE
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBarAmigos.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
