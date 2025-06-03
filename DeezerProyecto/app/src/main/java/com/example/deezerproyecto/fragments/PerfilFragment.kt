package com.example.deezerproyecto.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.MainActivity
import com.example.deezerproyecto.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class PerfilFragment : Fragment() {

    private lateinit var imagenUsuario: ImageView
    private lateinit var botonCambiarFoto: ImageButton
    private lateinit var campoNombre: EditText
    private lateinit var campoCorreo: EditText
    private lateinit var botonGuardar: Button
    private lateinit var botonCerrarSesion: Button
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("usuarios")
    private val usuario = FirebaseAuth.getInstance().currentUser
    private var imagenUri: Uri? = null

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
        botonCambiarFoto = view.findViewById(R.id.botonCambiarFoto)
        campoNombre = view.findViewById(R.id.campoNombre)
        campoCorreo = view.findViewById(R.id.campoCorreo)
        botonGuardar = view.findViewById(R.id.botonGuardar)
        botonCerrarSesion = view.findViewById(R.id.botonCerrarSesion)

        campoCorreo.setText(usuario?.email)
        cargarDatosUsuario()

        botonCambiarFoto.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        botonGuardar.setOnClickListener {
            actualizarPerfil()
        }

        botonCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            requireActivity().finish()
        }

        return view
    }

    private fun cargarDatosUsuario() {
        val usuarioId = usuario?.uid ?: return
        reference.child(usuarioId).get().addOnSuccessListener {
            campoNombre.setText(it.child("nombre").value as? String ?: "")
            val imagenBase64 = it.child("imagenPerfilBase64").value as? String

            if (!imagenBase64.isNullOrEmpty()) {
                val decodedBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                imagenUsuario.setImageBitmap(bitmap)
            } else {
                Picasso.get().load("https://cdn-icons-png.flaticon.com/512/1946/1946429.png")
                    .fit().centerCrop().into(imagenUsuario)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarPerfil() {
        val nuevoNombre = campoNombre.text.toString()
        if (usuario != null) {
            reference.child(usuario.uid).child("nombre").setValue(nuevoNombre)

            val drawable = imagenUsuario.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                val bytes = stream.toByteArray()
                val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)

                reference.child(usuario.uid).child("imagenPerfilBase64").setValue(base64String)
            }

            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}
