package com.example.deezerproyecto

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.example.deezerproyecto.databinding.ActivityHomeBinding
import com.example.deezerproyecto.databinding.BarraReproduccionBinding
import com.example.deezerproyecto.fragments.*
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bindingBarra: BarraReproduccionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindingBarra = BarraReproduccionBinding.bind(findViewById(R.id.barraReproduccion))

        bindingBarra.root.visibility = View.GONE

        bindingBarra.botonPausa.setOnClickListener {
            if (Reproductor.estaReproduciendo) {
                Reproductor.pausar()
                bindingBarra.botonPausa.setImageResource(android.R.drawable.ic_media_play)
            } else {
                Reproductor.continuar()
                bindingBarra.botonPausa.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        if (savedInstanceState == null) {
            cambiarFragment(HomeFragment())
        }

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

    fun iniciarBarra(
        titulo: String,
        artista: String,
        imagenUrl: String,
        urlCancion: String
    ) {
        bindingBarra.root.visibility = View.VISIBLE
        bindingBarra.textoCancion.text = titulo
        bindingBarra.textoArtista.text = artista

        if (imagenUrl.isNotBlank()) {
            Picasso.get()
                .load(imagenUrl)
                .fit()
                .centerCrop()
                .into(bindingBarra.imagenAlbum, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        val drawable = bindingBarra.imagenAlbum.drawable
                        if (drawable is BitmapDrawable) {
                            val bitmap = drawable.bitmap
                            Palette.from(bitmap).generate { palette ->
                                val colorDominante = palette?.getDominantColor(0xCC000000.toInt())
                                bindingBarra.root.setBackgroundColor(colorDominante ?: 0xCC000000.toInt())
                            }
                        }
                    }

                    override fun onError(e: Exception?) {
                        bindingBarra.root.setBackgroundColor(0xCC000000.toInt())
                    }
                })
        } else {
            bindingBarra.imagenAlbum.setImageResource(R.drawable.ic_launcher_foreground)
            bindingBarra.root.setBackgroundColor(0xCC000000.toInt())
        }

        Reproductor.reproducir(this, urlCancion) {
            bindingBarra.root.visibility = View.GONE
        }

        bindingBarra.botonPausa.setImageResource(android.R.drawable.ic_media_pause)
    }

    private fun cambiarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragment, fragment)
            .commit()
    }
}
