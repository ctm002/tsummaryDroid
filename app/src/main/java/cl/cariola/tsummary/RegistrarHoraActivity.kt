package cl.cariola.tsummary
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import cl.cariola.tsummary.business.controllers.ProyectoController
import cl.cariola.tsummary.business.entities.Proyecto
import kotlinx.android.synthetic.main.activity_registrar_hora.*
import java.text.SimpleDateFormat
import java.util.*

class RegistrarHoraActivity : AppCompatActivity() {
    var startDate : String = "2018-05-03"
    lateinit var editTxtHours : EditText
    lateinit var editTxtMinutes : EditText
    lateinit var editTxtStartHours : EditText
    lateinit var editTxtStartMinutes : EditText
    lateinit var editTxtNotas : EditText
    lateinit var projects : List<Proyecto>
    lateinit var itemSelected : Proyecto

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_hora)
        actionBar?.setDisplayShowHomeEnabled(true)

        setTitleBarTools()
        loadProjets()
        initialize()
        btnDeleteSetOnClickListener()
        btnSaveSetOnClickListener()
    }

    private fun setTitleBarTools() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse(startDate)
        setTitle(dateFormat.format(date))
    }

    private fun initialize()
    {
        this.editTxtHours = findViewById(R.id.editTxtTrabHoras)
        this.editTxtMinutes = findViewById(R.id.editTxtTrabMinutos)
        this.editTxtStartHours = findViewById(R.id.editTxtInicioHoras)
        this.editTxtStartMinutes = findViewById(R.id.editTxtInicioMinutos)
        this.editTxtNotas = findViewById(R.id.editTxtNotas)

        var actv = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val aListProyectos = this.projects.map { p -> "${p.cliente.nombre} ${p.nombre}" } as ArrayList<String>
        actv.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, aListProyectos))
        actv.threshold = 2
        actv.setOnItemClickListener { parent, view, position, id ->   this.itemSelected = this.projects.get(position)  }
    }

    fun loadProjets()
    {
        val controller = ProyectoController(this)
        this.projects = controller.getListProyectos()
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        return super.onOptionsItemSelected(item)
    }

    private fun btnSaveSetOnClickListener()
    {
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            val proyectoId: Int = this.itemSelected.id
            val asunto: String = txtViewAsunto.text.toString()
            val hours: String = editTxtHours.text.toString()
            val minutes: String = editTxtMinutes.text.toString()
            val startHours : String = editTxtStartHours.text.toString()
            val startMinutes : String = editTxtStartMinutes.text.toString()
            val id = 0
            val correlativo = 0
            val abogadoId = 0

            val controller = ProyectoController(this)
            controller.save(id, correlativo, proyectoId, abogadoId, asunto, startDate, hours.toInt(), minutes.toInt(), startHours.toInt(), startMinutes.toInt())

            val intent = Intent(this, SchedulerActivity:: class.java)
            val bundle = Bundle()
            bundle.putString("fecha", this.startDate)
            intent.putExtras(bundle)
            this.startActivity(intent)
        }
    }

    private fun btnDeleteSetOnClickListener()
    {
        val btnEliminar = findViewById<Button>(R.id.btnResetData)
        btnEliminar.setOnClickListener {
            val intent = Intent(this, SchedulerActivity::class.java)
            val bundle = Bundle()
            bundle.putString("fecha", this.startDate)
            this.startActivity(intent)
        }
    }

}
