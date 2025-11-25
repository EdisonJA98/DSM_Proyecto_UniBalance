package com.example.dsmproyecto.ui.activebreak.stretchingexercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.dsmproyecto.R

class ConfirmExitEstiramientoDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Reutilizamos el layout de botones creado anteriormente
        val view = inflater.inflate(R.layout.dialog_confirm_exit, container, false)

        // Configuramos el estilo de la ventana para que sea transparente
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Botón "Sí" (Confirmar salida y finalizar Activity)
        view.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            dismiss()
            activity?.finish()
        }

        // Botón "No" (Cancelar salida)
        view.findViewById<Button>(R.id.btn_no).setOnClickListener {
            dismiss() // Simplemente cierra el diálogo
        }

        return view
    }
}