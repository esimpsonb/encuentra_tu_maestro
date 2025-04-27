package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton

class WorkerPopupFragment : DialogFragment() {

    private var nombre: String? = null
    private var imagenResId: Int = 0
    private var zona: String? = null
    private var tipoTrabajo: String? = null
    private var precioHora: String? = null
    private var numeroContacto: String? = null

    companion object {
        fun newInstance(
            nombre: String,
            imagenResId: Int,
            zona: String,
            tipoTrabajo: String,
            precioHora: String,
            numeroContacto: String
        ): WorkerPopupFragment {
            val fragment = WorkerPopupFragment()
            val args = Bundle().apply {
                putString("nombre", nombre)
                putInt("imagenResId", imagenResId)
                putString("zona", zona)
                putString("tipoTrabajo", tipoTrabajo)
                putString("precioHora", precioHora)
                putString("numeroContacto", numeroContacto)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aplica tu estilo de diálogo aquí
        setStyle(STYLE_NORMAL, R.style.Theme_EncuentraTuMaestro_Dialog)

        arguments?.let {
            nombre = it.getString("nombre")
            imagenResId = it.getInt("imagenResId")
            zona = it.getString("zona")
            tipoTrabajo = it.getString("tipoTrabajo")
            precioHora = it.getString("precioHora")
            numeroContacto = it.getString("numeroContacto")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_worker_popup, container, false)

        // Encuentra las views
        val imagen            = view.findViewById<ImageView>(R.id.imagenWorker)
        val nombreView        = view.findViewById<TextView>(R.id.nombreWorker)
        val zonaView          = view.findViewById<TextView>(R.id.zonaWorker)
        val tipoTrabajoView   = view.findViewById<TextView>(R.id.tipoTrabajoWorker)
        val precioHoraView    = view.findViewById<TextView>(R.id.precioHoraWorker)
        val numeroContactoView= view.findViewById<TextView>(R.id.numeroContactoWorker)
        // Cambiamos aquí a MaterialButton
        val botonContactar    = view.findViewById<MaterialButton>(R.id.botonContactar)

        // Asigna datos
        imagen.setImageResource(imagenResId)
        nombreView.text       = nombre
        zonaView.text         = "Zona: $zona"
        tipoTrabajoView.text  = "Tipo de trabajos: $tipoTrabajo"
        precioHoraView.text   = "Precio/hora: $precioHora"
        numeroContactoView.text = "Número de contacto: $numeroContacto"

        // Listener de WhatsApp
        botonContactar.setOnClickListener {
            numeroContacto?.let { num ->
                val numeroLimpio = num.replace(" ", "").replace("+", "")
                val mensaje = "Hola $nombre, estoy interesado en tus servicios."
                val uri = "https://wa.me/$numeroLimpio?text=${Uri.encode(mensaje)}"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }
}
