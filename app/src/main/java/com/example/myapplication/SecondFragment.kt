package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.MotionEvent


class SecondFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var layoutTrabajadores: LinearLayout
    private lateinit var spinnerFiltroTipo: Spinner
    private lateinit var spinnerFiltroZona: Spinner
    private lateinit var textoNoResultados: TextView
    private lateinit var imagenNoResultados: ImageView

    private lateinit var tiposDisponibles: Array<String>
    private lateinit var zonasDisponibles: Array<String>
    private val tiposSeleccionados = mutableSetOf<String>()
    private val zonasSeleccionadas = mutableSetOf<String>()

    private lateinit var trabajadores: List<Trabajador>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        // --- Views & Data ---
        dbHelper = DatabaseHelper(requireContext())
        trabajadores = dbHelper.obtenerTrabajadores()

        layoutTrabajadores   = view.findViewById(R.id.layoutTrabajadores)
        spinnerFiltroTipo     = view.findViewById(R.id.spinnerFiltroTipo)
        spinnerFiltroZona     = view.findViewById(R.id.spinnerFiltroZona)
        textoNoResultados     = view.findViewById(R.id.textoNoResultados)
        imagenNoResultados    = view.findViewById(R.id.imagenNoResultados)

        tiposDisponibles = resources.getStringArray(R.array.tipos_trabajo)
        zonasDisponibles = resources.getStringArray(R.array.zonas_trabajo)

        // Por defecto seleccionamos todos
        tiposSeleccionados.addAll(tiposDisponibles)
        zonasSeleccionadas.addAll(zonasDisponibles)

        // Inicializa cada Spinner con un único elemento: el resumen de la selección
        actualizarSpinner(spinnerFiltroTipo, resumen(tiposSeleccionados, "Tipos"))
        actualizarSpinner(spinnerFiltroZona, resumen(zonasSeleccionadas, "Zonas"))

        // Intercepta el toque para abrir nuestro diálogo multi-select
        spinnerFiltroTipo.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()  // ⬅️ Llamada para accesibilidad
                mostrarDialogoMulti(
                    "Selecciona Tipo(s)",
                    tiposDisponibles,
                    tiposSeleccionados
                ) {
                    actualizarSpinner(spinnerFiltroTipo, resumen(tiposSeleccionados, "Tipos"))
                    filtrarYMostrar()
                }
            }
            true
        }

        spinnerFiltroZona.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()  // ⬅️ Llamada para accesibilidad
                mostrarDialogoMulti(
                    "Selecciona Zona(s)",
                    zonasDisponibles,
                    zonasSeleccionadas
                ) {
                    actualizarSpinner(spinnerFiltroZona, resumen(zonasSeleccionadas, "Zonas"))
                    filtrarYMostrar()
                }
            }
            true
        }

        filtrarYMostrar()
        return view
    }

    private fun mostrarDialogoMulti(
        titulo: String,
        opciones: Array<String>,
        seleccion: MutableSet<String>,
        onAceptar: () -> Unit
    ) {
        val checked = opciones.map { seleccion.contains(it) }.toBooleanArray()
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMultiChoiceItems(opciones, checked) { _, which, isChecked ->
                if (isChecked) seleccion.add(opciones[which])
                else           seleccion.remove(opciones[which])
            }
            .setPositiveButton("Aceptar") { _, _ -> onAceptar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarSpinner(spinner: Spinner, texto: String) {
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf(texto)
        )
    }

    private fun resumen(seleccion: Set<String>, label: String): String {
        val todas = if (label == "Tipos") tiposDisponibles.toSet() else zonasDisponibles.toSet()
        return if (seleccion == todas) {
            "$label (Todos)"
        } else {
            "$label (${seleccion.joinToString(", ")})"
        }
    }

    private fun filtrarYMostrar() {
        layoutTrabajadores.removeAllViews()
        val filtrados = trabajadores.filter { t ->
            tiposSeleccionados.any   { t.tipoTrabajo.contains(it, ignoreCase = true) } &&
                    zonasSeleccionadas.any   { t.zona.contains(it, ignoreCase = true) }
        }

        if (filtrados.isEmpty()) {
            textoNoResultados.visibility = View.VISIBLE
            imagenNoResultados.visibility = View.VISIBLE
        } else {
            textoNoResultados.visibility = View.GONE
            imagenNoResultados.visibility = View.GONE
            filtrados.forEach { layoutTrabajadores.addView(crearTarjetaTrabajador(it)) }
        }
    }

    // Usa aquí exactamente tu código original para crear la tarjeta y mostrar la foto
    private fun crearTarjetaTrabajador(trabajador: Trabajador): View {
        val cardView = CardView(requireContext()).apply {
            radius = 24f
            cardElevation = 12f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 24) }
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 350
            ).apply { setMargins(16, 16, 16, 8) }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(trabajador.fotoResId)    // <-- NO toca aquí
            clipToOutline = true
        }
        val textView = TextView(requireContext()).apply {
            text = trabajador.nombre
            textSize = 20f
            setPadding(12, 12, 12, 12)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        container.addView(imageView)
        container.addView(textView)
        cardView.addView(container)
        cardView.setOnClickListener {
            WorkerPopupFragment.newInstance(
                trabajador.nombre,
                trabajador.fotoResId,
                trabajador.zona,
                trabajador.tipoTrabajo,
                trabajador.precioHora,
                trabajador.numeroContacto
            ).show(parentFragmentManager, "popup${trabajador.id}")
        }
        return cardView
    }
}
