package com.example.deezerproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.deezerproyecto.databinding.ActivityRegisterBinding
import com.example.deezerproyecto.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.botonRegistrar.setOnClickListener {
            val correo = binding.campoCorreo.text.toString().trim()
            val contrasena = binding.campoContrasena.text.toString().trim()
            val repetirContrasena = binding.campoRepetirContrasena.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty() || repetirContrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else if (contrasena != repetirContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val usuarioFirebase = auth.currentUser
                            val uid = usuarioFirebase?.uid ?: return@addOnCompleteListener

                            val nuevoUsuario = Usuario(
                                uid = uid,
                                nombre = "Usuario Nuevo",
                                correo = correo,
                                imagenPerfil = "https://cdn-icons-png.flaticon.com/512/1946/1946429.png"
                            )

                            // 🔐 Guardar usando UID como clave
                            database.child(uid).setValue(nuevoUsuario)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
