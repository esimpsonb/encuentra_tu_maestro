package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val sharedPref = requireActivity().getSharedPreferences("MyPrefs", 0)
        val nombreUsuario = sharedPref.getString("usuario_actual", null)

        if (nombreUsuario != null) {
            binding.textoHola.text = "¡Bienvenido $nombreUsuario!"
            binding.botonCerrarSesion.visibility = View.VISIBLE
        } else {
            binding.botonCerrarSesion.visibility = View.GONE
        }

        binding.miBoton.setOnClickListener {
            val popup = PopupFragment()
            popup.show(parentFragmentManager, "PopupFragment")
        }

        binding.botonCerrarSesion.setOnClickListener {
            // Cerrar sesión: borrar el usuario guardado
            with(sharedPref.edit()) {
                remove("usuario_actual")
                apply()
            }
            // Reiniciar Home
            binding.textoHola.text = getString(R.string.nombre_app) // o el texto que quieras por defecto
            binding.botonCerrarSesion.visibility = View.GONE
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}