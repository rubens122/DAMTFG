package com.example.deezerproyecto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.deezerproyecto.databinding.ActivityHomeBinding
import com.example.deezerproyecto.fragments.*


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar el HomeFragment por defecto
        if (savedInstanceState == null) {
            cambiarFragment(HomeFragment())
        }

        // Configuración del BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> cambiarFragment(HomeFragment())
                R.id.nav_buscar -> cambiarFragment(BuscarFragment())
                R.id.nav_radio -> cambiarFragment(PlaylistsPublicasFragment())
                R.id.nav_biblioteca -> cambiarFragment(BibliotecaFragment())
                else -> false
            }
            true
        }
    }

    // Función para cambiar de fragment
    private fun cambiarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .commit()
    }

}
