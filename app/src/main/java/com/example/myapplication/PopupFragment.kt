package com.example.myapplication

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment

class PopupFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)

        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)
        val editTextNombre = view.findViewById<EditText>(R.id.editTextNombre)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val botonCrearCuenta = view.findViewById<Button>(R.id.botonCrearCuenta)
        val botonIniciarSesion = view.findViewById<Button>(R.id.botonIniciarSesion)

        val dbHelper = DatabaseHelper(requireContext())

        closeButton.setOnClickListener {
            dismiss()
        }

        botonCrearCuenta.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val password = editTextPassword.text.toString()
            if (nombre.isNotEmpty() && password.isNotEmpty()) {
                val success = dbHelper.insertUser(nombre, password)
                if (success) {
                    Toast.makeText(requireContext(), "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor llena ambos campos", Toast.LENGTH_SHORT).show()
            }
        }

        botonIniciarSesion.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val password = editTextPassword.text.toString()
            if (nombre.isNotEmpty() && password.isNotEmpty()) {
                val success = dbHelper.checkUser(nombre, password)
                if (success) {
                    Toast.makeText(requireContext(), "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show()
                    val sharedPref = requireActivity().getSharedPreferences("MyPrefs", 0)
                    with (sharedPref.edit()) {
                        putString("usuario_actual", nombre)
                        apply()
                    }
                    requireActivity().invalidateOptionsMenu()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor llena ambos campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}