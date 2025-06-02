package com.example.deezerproyecto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.deezerproyecto.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Recuperar preferencia de tema antes de super.onCreate
        prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
        val modoOscuro = prefs.getBoolean("modoOscuro", false)
        AppCompatDelegate.setDefaultNightMode(
            if (modoOscuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // ✅ Establecer icono según el modo actual
        binding.botonModoTema.setImageResource(
            if (modoOscuro) R.drawable.ic_luna else R.drawable.ic_sol
        )

        // ✅ Cambiar el modo al pulsar el botón
        binding.botonModoTema.setOnClickListener {
            val nuevoModo = if (modoOscuro) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(nuevoModo)
            prefs.edit().putBoolean("modoOscuro", nuevoModo == AppCompatDelegate.MODE_NIGHT_YES).apply()
            recreate() // 🔄 Refrescar actividad para aplicar cambios visuales
        }

        binding.botonIniciarSesion.setOnClickListener {
            val correo = binding.campoCorreo.text.toString().trim()
            val contrasena = binding.campoContrasena.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.textoIrARegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
