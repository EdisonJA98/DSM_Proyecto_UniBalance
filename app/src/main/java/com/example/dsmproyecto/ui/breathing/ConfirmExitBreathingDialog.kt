package com.example.dsmproyecto.ui.breathing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.dsmproyecto.R

class ConfirmExitBreathingDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_confirm_exit, container, false)

        // Configuramos el estilo de la ventana para que sea transparente y veamos el fondo
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Botón "Sí" (Confirmar salida)
        view.findViewById<Button>(R.id.btn_yes).setOnClickListener {
            // Cierra el diálogo y finaliza la Activity (vuelve a MainActivity)
            dismiss()
            activity?.finish()
        }

        // Botón "No" (Cancelar salida y continuar la pausa)
        view.findViewById<Button>(R.id.btn_no).setOnClickListener {
            dismiss() // Simplemente cierra el diálogo
        }

        return view
    }
}