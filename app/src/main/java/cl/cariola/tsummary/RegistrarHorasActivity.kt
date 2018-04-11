package cl.cariola.tsummary

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import cl.cariola.tsummary.business.controllers.ProyectoController
import java.text.SimpleDateFormat
import java.util.*

class RegistrarHorasActivity : AppCompatActivity() {
    var fecha: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_horas)

        val date = Date()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
        setTitle(dateFormat.format(date))

        actionBar?.setDisplayShowHomeEnabled(true)
        val list = getListProyectos()
        var actv = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        actv.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list!!))
        actv.threshold = 2


        val btnEliminar = findViewById<Button>(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            val intent = Intent(this, MainActivity:: class.java)
            this.startActivity(intent)
        }
    }

    fun getListProyectos(): ArrayList<String>? {
        val controller = ProyectoController(this)
        val list = controller.getListProyectos().map { p -> "${p.cliente.nombre}\n${p.nombre }" } as ArrayList<String>
        return list
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

}
