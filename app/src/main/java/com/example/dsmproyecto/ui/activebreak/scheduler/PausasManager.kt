package com.example.dsmproyecto.ui.activebreak.scheduler

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

// Modelo de datos compartido
data class AlarmaPausa(
    val id: Int,            // ID único (el mismo usado para el PendingIntent)
    val horaFormato: String, // Ej: "14:30"
    val tipo: String,       // "VISUAL", "ESTIRAMIENTO", "DECIDIR"
    val descripcion: String // Texto amigable para mostrar
)

// Objeto Singleton para manejar el almacenamiento simple
object PausasStorage {
    private const val PREF_NAME = "pausas_activas_prefs"
    private const val KEY_LISTA = "lista_alarmas"

    // Guardar una nueva pausa en la lista
    fun guardarPausa(context: Context, pausa: AlarmaPausa) {
        val listaActual = obtenerPausas(context).toMutableList()
        listaActual.add(pausa)
        guardarListaEnPrefs(context, listaActual)
    }

    // Obtener todas las pausas guardadas
    fun obtenerPausas(context: Context): List<AlarmaPausa> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_LISTA, "[]")
        val lista = ArrayList<AlarmaPausa>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                lista.add(AlarmaPausa(
                    id = obj.getInt("id"),
                    horaFormato = obj.getString("horaFormato"),
                    tipo = obj.getString("tipo"),
                    descripcion = obj.getString("descripcion")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Ordenar por hora (opcional, aquí las mostramos en orden de creación)
        return lista
    }

    // Eliminar una pausa por su ID
    fun eliminarPausa(context: Context, id: Int) {
        val listaActual = obtenerPausas(context).toMutableList()
        listaActual.removeAll { it.id == id }
        guardarListaEnPrefs(context, listaActual)
    }

    // Método interno para escribir en SharedPreferences
    private fun guardarListaEnPrefs(context: Context, lista: List<AlarmaPausa>) {
        val jsonArray = JSONArray()
        for (p in lista) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("horaFormato", p.horaFormato)
            obj.put("tipo", p.tipo)
            obj.put("descripcion", p.descripcion)
            jsonArray.put(obj)
        }

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LISTA, jsonArray.toString())
            .apply()
    }
}