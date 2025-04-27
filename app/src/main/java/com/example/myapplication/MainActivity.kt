package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Oculta solo el título de la ActionBar, mantiene el overflow menu
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val nombreUsuario = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getString("usuario_actual", null)
        menu?.findItem(R.id.action_workers)?.isVisible = nombreUsuario != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val nombreUsuario = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            .getString("usuario_actual", null)

        val fragment = when (item.itemId) {
            R.id.action_home   -> HomeFragment()
            R.id.action_about  -> FirstFragment()
            R.id.action_workers ->
                if (nombreUsuario != null) SecondFragment()
                else {
                    showLoginRequiredDialog()
                    null
                }
            else -> null
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .addToBackStack(null)
                .commit()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoginRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("Iniciar sesión")
            .setMessage("Debes iniciar sesión para acceder a esta sección.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
