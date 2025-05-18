package com.example.deezerproyecto.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.MainActivity
import com.example.deezerproyecto.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class PerfilFragment : Fragment() {

    private lateinit var imagenUsuario: ImageView
    private lateinit var campoNombre: EditText
    private lateinit var campoCorreo: EditText
    private lateinit var botonGuardar: Button
    private lateinit var botonCerrarSesion: Button
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("usuarios")
    private val usuario = FirebaseAuth.getInstance().currentUser
    private var imagenUri: Uri? = null

    // 🔄 Selector de imágenes
    private val seleccionarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
            imagenUsuario.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        imagenUsuario = view.findViewById(R.id.imagenUsuario)
        campoNombre = view.findViewById(R.id.campoNombre)
        campoCorreo = view.findViewById(R.id.campoCorreo)
        botonGuardar = view.findViewById(R.id.botonGuardar)
        botonCerrarSesion = view.findViewById(R.id.botonCerrarSesion)

        // 🔄 Cargar datos del usuario
        campoCorreo.setText(usuario?.email)
        cargarDatosUsuario()

        // 🔄 Cambiar imagen al hacer click
        imagenUsuario.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        // 🔄 Botón para actualizar datos
        botonGuardar.setOnClickListener {
            actualizarPerfil()
        }

        // 🔄 Botón para cerrar sesión
        botonCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    /**
     * 🔄 Cargar datos del usuario desde Firebase
     */
    private fun cargarDatosUsuario() {
        val usuarioId = usuario?.uid ?: return
        reference.child(usuarioId).get().addOnSuccessListener {
            val nombre = it.child("nombre").value as? String
            val imagenUrl = it.child("imagenPerfil").value as? String

            campoNombre.setText(nombre ?: "")
            if (imagenUrl != null) {
                Picasso.get()
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_user) // Imagen por defecto
                    .fit()
                    .centerCrop()
                    .into(imagenUsuario)
            } else {
                Picasso.get()
                    .load("https://cdn-icons-png.flaticon.com/512/1946/1946429.png")
                    .fit()
                    .centerCrop()
                    .into(imagenUsuario)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cargar los datos del perfil", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 🔄 Actualizar datos del perfil y subir imagen si existe
     */
    private fun actualizarPerfil() {
        val nuevoNombre = campoNombre.text.toString()

        if (usuario != null) {
            if (imagenUri != null) {
                subirImagenAFirebase(imagenUri!!) { url ->
                    reference.child(usuario.uid).child("imagenPerfil").setValue(url)
                    actualizarNombre(nuevoNombre)
                }
            } else {
                actualizarNombre(nuevoNombre)
            }
        }
    }

    /**
     * 🔄 Subir la imagen a Firebase Storage
     */
    private fun subirImagenAFirebase(uri: Uri, onSuccess: (String) -> Unit) {
        val nombreArchivo = "perfil_${usuario!!.uid}.jpg"
        val referenciaStorage = FirebaseStorage.getInstance().reference.child("imagenes_perfil/$nombreArchivo")

        referenciaStorage.putFile(uri)
            .addOnSuccessListener {
                referenciaStorage.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                    Toast.makeText(requireContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * 🔄 Actualizar el nombre en Firebase Realtime Database
     */
    private fun actualizarNombre(nuevoNombre: String) {
        reference.child(usuario!!.uid).child("nombre").setValue(nuevoNombre)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
    }
}
