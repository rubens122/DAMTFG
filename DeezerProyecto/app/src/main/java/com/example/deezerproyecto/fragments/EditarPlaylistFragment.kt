package com.example.deezerproyecto.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.R
import com.example.deezerproyecto.models.Playlist
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class EditarPlaylistFragment(private val playlist: Playlist) : Fragment() {

    private lateinit var campoNombre: EditText
    private lateinit var switchPrivacidad: Switch
    private lateinit var botonGuardar: Button
    private lateinit var botonSeleccionarFoto: ImageButton  // 🔄 CAMBIADO a ImageButton
    private lateinit var imagenPlaylist: ImageView
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("playlists")
    private var imagenUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editar_playlist, container, false)

        campoNombre = view.findViewById(R.id.campoNombrePlaylist)
        switchPrivacidad = view.findViewById(R.id.switchPrivacidad)
        botonGuardar = view.findViewById(R.id.botonGuardarCambios)
        botonSeleccionarFoto = view.findViewById(R.id.botonSeleccionarFoto) // ✅ AHORA SE CASTEA BIEN
        imagenPlaylist = view.findViewById(R.id.imagenPlaylist)

        // 🔄 Valores actuales de la Playlist
        campoNombre.setText(playlist.nombre)
        switchPrivacidad.isChecked = !playlist.esPrivada

        if (playlist.rutaFoto.isNotEmpty()) {
            Picasso.get().load(playlist.rutaFoto).into(imagenPlaylist)
        }

        // 🔄 Seleccionar imagen desde galería
        botonSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1000)
        }

        // 🔄 Guardar cambios
        botonGuardar.setOnClickListener {
            actualizarPlaylist()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            imagenUri = selectedImage.toString()
            imagenPlaylist.setImageURI(selectedImage)
        }
    }

    /**
     * 🔄 Actualización en Firebase
     */
    private fun actualizarPlaylist() {
        playlist.nombre = campoNombre.text.toString()
        playlist.esPrivada = !switchPrivacidad.isChecked
        if (imagenUri != null) {
            playlist.rutaFoto = imagenUri!!
        }

        reference.child(playlist.id).setValue(playlist)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Playlist actualizada correctamente", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al actualizar la playlist", Toast.LENGTH_SHORT).show()
            }
    }
}
