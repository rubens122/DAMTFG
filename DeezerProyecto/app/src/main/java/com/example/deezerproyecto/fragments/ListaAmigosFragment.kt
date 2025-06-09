package com.example.deezerproyecto.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deezerproyecto.R
import com.example.deezerproyecto.adapters.AmigoAdapterLista
import com.example.deezerproyecto.adapters.ActividadAdapter
import com.example.deezerproyecto.databinding.FragmentListaAmigosBinding
import com.example.deezerproyecto.models.ActividadUsuario
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaAmigosFragment : Fragment() {

    private var _binding: FragmentListaAmigosBinding? = null
    private val binding get() = _binding!!

    private val listaAmigos = mutableListOf<Usuario>()
    private val listaActividad = mutableListOf<ActividadUsuario>()
    private val database = FirebaseDatabase.getInstance().reference
    private val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var adapter: AmigoAdapterLista
    private lateinit var adapterActividad: ActividadAdapter
    private var amigosListener: ValueEventListener? = null
    private var actividadYaCargada = false
    private var idsAmigosGlobal = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaAmigosBinding.inflate(inflater, container, false)

        adapter = AmigoAdapterLista(
            amigos = listaAmigos,
            uidActual = uidUsuario ?: "",
            onClick = { amigo ->
                val fragment = PerfilAmigoFragment().apply {
                    arguments = Bundle().apply {
                        putString("amigoId", amigo.uid)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragment, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        adapterActividad = ActividadAdapter(listaActividad)

        binding.recyclerAmigos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAmigos.adapter = adapter

        binding.recyclerActividadAmigos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerActividadAmigos.adapter = adapterActividad

        if (uidUsuario != null) {
            observarAmigosTiempoReal(uidUsuario)
        }

        binding.botonVerAmigos.setOnClickListener { mostrarAmigos() }
        binding.botonVerActividad.setOnClickListener { mostrarActividad() }

        return binding.root
    }

    private fun observarAmigosTiempoReal(uid: String) {
        binding.progressBarAmigos.visibility = View.VISIBLE

        amigosListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idsAmigos = snapshot.children.mapNotNull { it.key }
                idsAmigosGlobal = idsAmigos
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

                            override fun onCancelled(error: DatabaseError) {
                                cargados++
                                if (cargados == idsAmigos.size) {
                                    binding.progressBarAmigos.visibility = View.GONE
                                }
                            }
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

    private fun cargarActividadAmigos(idsAmigos: List<String>) {
        binding.progressBarActividad.visibility = View.VISIBLE
        listaActividad.clear()

        var amigosCargados = 0
        for (id in idsAmigos) {
            database.child("actividadUsuarios").child(id)
                .limitToLast(5)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (actividadSnap in snapshot.children) {
                            val actividad = actividadSnap.getValue(ActividadUsuario::class.java)
                            if (actividad != null) {
                                listaActividad.add(actividad)
                            }
                        }
                        amigosCargados++
                        if (amigosCargados == idsAmigos.size) {
                            listaActividad.sortByDescending { it.fecha }
                            adapterActividad.notifyDataSetChanged()
                            binding.progressBarActividad.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        amigosCargados++
                        if (amigosCargados == idsAmigos.size) {
                            binding.progressBarActividad.visibility = View.GONE
                        }
                    }
                })
        }
    }

    private fun mostrarAmigos() {
        binding.layoutAmigos.visibility = View.VISIBLE
        binding.layoutActividad.visibility = View.GONE

        binding.botonVerAmigos.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorVerdeOscuro))
        binding.botonVerActividad.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorVerdeSpotify))
    }

    private fun mostrarActividad() {
        binding.layoutAmigos.visibility = View.GONE
        binding.layoutActividad.visibility = View.VISIBLE

        binding.botonVerActividad.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorVerdeOscuro))
        binding.botonVerAmigos.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorVerdeSpotify))

        if (!actividadYaCargada && idsAmigosGlobal.isNotEmpty()) {
            cargarActividadAmigos(idsAmigosGlobal)
            actividadYaCargada = true
        }
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
