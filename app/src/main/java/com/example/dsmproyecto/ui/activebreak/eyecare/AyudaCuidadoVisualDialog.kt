import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.dsmproyecto.R

class AyudaCuidadoVisualDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del diálogo
        val view = inflater.inflate(R.layout.dialog_help_cuidado_visual, container, false)

        // Configuramos el botón de cerrar
        view.findViewById<View>(R.id.btn_close).setOnClickListener {
            dismiss() // Cierra el diálogo
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Esto es crucial para quitar el fondo del diálogo predeterminado y usar solo nuestro fondo redondeado
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}