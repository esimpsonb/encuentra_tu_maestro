package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class FirstFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        dbHelper = DatabaseHelper(requireContext())

        val inputNombre = view.findViewById<EditText>(R.id.inputNombre)
        val inputPrecioHora = view.findViewById<EditText>(R.id.inputPrecioHora)
        val inputNumeroContacto = view.findViewById<EditText>(R.id.inputNumeroContacto)

        val spinnerTipoTrabajo = view.findViewById<Spinner>(R.id.spinnerTipoTrabajo)
        val spinnerZona = view.findViewById<Spinner>(R.id.spinnerZona)

        val opcionesTipoTrabajo = resources.getStringArray(R.array.tipos_trabajo)
        val opcionesZona = resources.getStringArray(R.array.zonas_trabajo)

        spinnerTipoTrabajo.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, opcionesTipoTrabajo)
        spinnerZona.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, opcionesZona)

        val botonAgregar = view.findViewById<Button>(R.id.botonAgregar)
        val botonBorrarTodo = view.findViewById<Button>(R.id.botonBorrarTodo)

        botonAgregar.setOnClickListener {
            val nombre = inputNombre.text.toString()
            val tipoTrabajo = spinnerTipoTrabajo.selectedItem.toString()
            val zona = spinnerZona.selectedItem.toString()
            val precioHora = inputPrecioHora.text.toString()
            val numeroContacto = inputNumeroContacto.text.toString()

            if (nombre.isNotEmpty() && tipoTrabajo.isNotEmpty() && zona.isNotEmpty()
                && precioHora.isNotEmpty() && numeroContacto.isNotEmpty()) {

                val nombreParaFoto = nombre.lowercase().replace(" ", "_")
                var fotoResId = requireContext().resources.getIdentifier(nombreParaFoto, "drawable", requireContext().packageName)

                if (fotoResId == 0) {
                    Toast.makeText(requireContext(), "Imagen no encontrada, usando imagen por defecto", Toast.LENGTH_SHORT).show()
                    fotoResId = R.drawable.trabajador_defecto
                }

                dbHelper.insertarTrabajador(
                    nombre = nombre,
                    zona = zona,
                    tipoTrabajo = tipoTrabajo,
                    precioHora = precioHora,
                    numeroContacto = numeroContacto,
                    fotoResId = fotoResId
                )

                Toast.makeText(requireContext(), "Trabajador agregado exitosamente", Toast.LENGTH_SHORT).show()

                inputNombre.text.clear()
                inputPrecioHora.text.clear()
                inputNumeroContacto.text.clear()
                spinnerTipoTrabajo.setSelection(0)
                spinnerZona.setSelection(0)

            } else {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        botonBorrarTodo.setOnClickListener {
            dbHelper.borrarTodosLosTrabajadores()
            Toast.makeText(requireContext(), "Todos los trabajadores fueron eliminados", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
