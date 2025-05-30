package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezerproyecto.adapters.AmigoAdapterLista
import com.example.deezerproyecto.databinding.FragmentListaAmigosBinding
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.deezerproyecto.R


class ListaAmigosFragment : Fragment() {

    private var _binding: FragmentListaAmigosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AmigoAdapterLista
    private val listaAmigos = mutableListOf<Usuario>()
    private val database = FirebaseDatabase.getInstance().reference
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid
    private var amigosListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaAmigosBinding.inflate(inflater, container, false)

        adapter = AmigoAdapterLista(
            amigos = listaAmigos,
            uidActual = uidUsuario ?: "",
            onClick = { amigo ->
                val fragment = PerfilAmigoFragment()
                fragment.arguments = Bundle().apply {
                    putString("amigoId", amigo.uid)
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.recyclerAmigos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAmigos.adapter = adapter

        if (uidUsuario != null) {
            observarAmigosTiempoReal(uidUsuario)
        }

        return binding.root
    }

    private fun observarAmigosTiempoReal(uid: String) {
        binding.progressBarAmigos.visibility = View.VISIBLE

        amigosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idsAmigos = snapshot.children.mapNotNull { it.key }
                listaAmigos.clear()

                if (idsAmigos.isEmpty()) {
                    adapter.notifyDataSetChanged()
                    binding.progressBarAmigos.visibility = View.GONE
                    return
                }

                var cargados = 0
                for (id in idsAmigos) {
                    database.child("usuarios").child(id)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
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
        }

        database.child("usuarios").child(uid).child("amigos")
            .addValueEventListener(amigosListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (uidUsuario != null && amigosListener != null) {
            database.child("usuarios").child(uidUsuario).child("amigos")
                .removeEventListener(amigosListener!!)
        }
        _binding = null
    }
}
