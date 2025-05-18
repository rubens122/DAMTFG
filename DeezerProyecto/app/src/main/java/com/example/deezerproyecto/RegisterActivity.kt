package com.example.deezerproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.deezerproyecto.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

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
                            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        // binding.enlaceLogin.setOnClickListener {
        //    startActivity(Intent(this, MainActivity::class.java))
        //    finish()
        //}
    }
}