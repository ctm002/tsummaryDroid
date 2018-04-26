package cl.cariola.tsummary

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MenuItem
import android.widget.*
import cl.cariola.tsummary.business.controllers.ProyectoController
import cl.cariola.tsummary.business.entities.Proyecto
import cl.cariola.tsummary.business.entities.RegistroHora
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class RegistrarHoraActivity : AppCompatActivity() {
    var startDate: String = ""
    var idAbogado: Int = 0
    var correlativo: Int = 0
    var id: Int = 0
    var imei: String = ""

    lateinit var editTxtHorasIni: EditText
    lateinit var editTxtMinIni: EditText
    lateinit var editTxtHorasTrab: EditText
    lateinit var editTxtMinTrab: EditText
    lateinit var editTxtAsunto: EditText
    lateinit var projects: List<Proyecto>
    lateinit var itemSelected: Proyecto
    lateinit var textAutocomplete: AutoCompleteTextView
    private val TAG = "RegistrarHoraActivity"
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_hora)
        actionBar?.setDisplayShowHomeEnabled(true)

        loadProjets()
        initialize()
        btnDeleteSetOnClickListener()
        btnSaveSetOnClickListener()

        val bundle = intent.extras
        val regJSON = bundle!!.getString("registro")
        val gson = Gson()
        var registro = gson.fromJson(regJSON, RegistroHora::class.java)
        loadData(registro)
        setTitleBarTools()
        this.mContext = this
    }

    private fun loadData(registro: RegistroHora) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        this.startDate = dateFormat.format(registro.mFechaIng)
        setTitle(this.startDate)

        this.id = registro.mId
        this.idAbogado = registro.mAbogadoId
        this.correlativo = registro.mCorrelativo

        this.editTxtHorasIni.setText(registro.mInicio.horas.toString())
        this.editTxtMinIni.setText(registro.mInicio.minutos.toString())

        this.editTxtHorasTrab.setText(registro.mHoraTotal.horas.toString())
        this.editTxtMinTrab.setText(registro.mHoraTotal.minutos.toString())
        this.editTxtAsunto.setText(registro.mAsunto)

        if (registro.mProyecto != null) {
            this.itemSelected = registro.mProyecto!!
            this.textAutocomplete.setText(registro.mProyecto?.nombre)
        }
    }

    private fun setTitleBarTools() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse(startDate)
        setTitle(dateFormat.format(date))
    }

    private fun initialize() {
        this.editTxtHorasIni = findViewById(R.id.editTxtInicioHoras)
        this.editTxtMinIni = findViewById(R.id.editTxtInicioMinutos)
        this.editTxtHorasTrab = findViewById(R.id.editTxtTrabHoras)
        this.editTxtMinTrab = findViewById(R.id.editTxtTrabMinutos)
        this.editTxtAsunto = findViewById(R.id.editTxtNotas)

        textAutocomplete = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val aListProyectos = this.projects.map { p -> "${p.cliente.nombre} ${p.nombre}" } as ArrayList<String>
        textAutocomplete.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, aListProyectos))
        textAutocomplete.threshold = 2
        textAutocomplete.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            Log.d(TAG, selected)
            val index = aListProyectos.indexOf(selected)
            Log.d(TAG, index.toString())
            this.itemSelected = this.projects.get(index)
            Log.d(TAG, itemSelected.id.toString())
        }

        setHorasTrabajos()
        setHorasInicio()
    }

    private fun setHorasInicio() {
        var btnIncHorasIni = findViewById<Button>(R.id.btnIncHorasIni)
        btnIncHorasIni.setOnClickListener {
            setValueHoras(editTxtHorasIni, +1)
        }

        var btnDecHorasIni = findViewById<Button>(R.id.btnDecHorasIni)
        btnDecHorasIni.setOnClickListener {
            setValueHoras(editTxtHorasIni, -1)
        }

        var btnIncMinIni = findViewById<Button>(R.id.btnIncMinIni)
        btnIncMinIni.setOnClickListener {
            var suma: Int = if (editTxtMinIni.text.isNullOrEmpty()) 0 else Integer.parseInt(editTxtMinIni.text.toString())
            suma += 1
            suma = if (suma < 0) 59 else suma
            suma = if (suma > 59) 0 else suma
            editTxtMinIni.setText(suma.toString())
        }

        var btnDecMinIni = findViewById<Button>(R.id.btnDecMinIni)
        btnDecMinIni.setOnClickListener {
            var suma: Int = if (editTxtMinIni.text.isNullOrEmpty()) 0 else Integer.parseInt(editTxtMinIni.text.toString())
            suma += -1
            suma = if (suma < 0) 59 else suma
            suma = if (suma > 59) 0 else suma
            editTxtMinIni.setText(suma.toString())
        }
    }

    private fun setHorasTrabajos() {
        var btnIncHorasTrab = findViewById<Button>(R.id.btnIncHorasTrab)
        btnIncHorasTrab.setOnClickListener {
            setValueHoras(editTxtHorasTrab, +1)
        }

        var btnDecHorasTrab = findViewById<Button>(R.id.btnDecHorasTrab)
        btnDecHorasTrab.setOnClickListener {
            setValueHoras(editTxtHorasTrab, -1)
        }

        var btnIncMinTrab = findViewById<Button>(R.id.btnIncMinTrab)
        btnIncMinTrab.setOnClickListener {
            var suma: Int = if (editTxtMinTrab.text.isNullOrEmpty()) 0 else Integer.parseInt(editTxtMinTrab.text.toString())
            suma += 15
            suma = if (suma < 0) 45 else suma
            suma = if (suma >= 60) 0 else suma
            editTxtMinTrab.setText(suma.toString())
        }

        var btnDecMinTrab = findViewById<Button>(R.id.btnDecMinTrab)
        btnDecMinTrab.setOnClickListener {
            var suma: Int = if (editTxtMinTrab.text.isNullOrEmpty()) 0 else Integer.parseInt(editTxtMinTrab.text.toString())
            suma += -15
            suma = if (suma < 0) 45 else suma
            suma = if (suma >= 60) 0 else suma
            editTxtMinTrab.setText(suma.toString())

        }
    }

    fun setValueHoras(_EditText: EditText, value: Int) {
        var suma: Int = if (_EditText.text.isNullOrEmpty()) 0 else Integer.parseInt(_EditText.text.toString())
        suma += value
        suma = if (suma < 0) 23 else suma
        suma = if (suma > 23) 0 else suma
        _EditText.setText(suma.toString())
    }

    fun loadProjets() {
        val controller = ProyectoController(this)
        this.projects = controller.getListProyectos()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent = Intent(this, SchedulerActivity::class.java)
        intent.putExtra("fecha", this.startDate)
        intent.putExtra("idAbogado", this.idAbogado)
        startActivity(intent)
        return true
    }

    private fun btnSaveSetOnClickListener() {
        try {
            val btnGuardar = findViewById<Button>(R.id.btnGuardar)
            btnGuardar.setOnClickListener {
                registrarHora()
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun registrarHora() {
        val registrar = RegistrarTask()
        registrar.execute()
    }

    private fun btnDeleteSetOnClickListener() {
        val btnEliminar = findViewById<Button>(R.id.btnResetData)
        btnEliminar.setOnClickListener {
            val task = EliminarTask()
            task.execute(this.id.toString())
        }
    }

    inner class RegistrarTask() : AsyncTask<Void, Void, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String {
            val proyectoId: Int = itemSelected.id
            val asunto: String = editTxtAsunto.text.toString()
            val startHours: String = if (editTxtHorasIni.text.isNullOrEmpty()) "0" else editTxtHorasIni.text.toString()
            val startMinutes: String = if (editTxtMinIni.text.isNullOrEmpty()) "0" else editTxtMinIni.text.toString()

            val hours: String = if (editTxtHorasTrab.text.isNullOrBlank()) "0" else editTxtHorasTrab.text.toString()
            val minutes: String = if (editTxtMinTrab.text.isNullOrBlank()) "0" else editTxtMinTrab.text.toString()

            val abogadoId = idAbogado
            val controller = ProyectoController(mContext)
            controller.save(id, correlativo, proyectoId, abogadoId, asunto, startDate, hours.toInt(), minutes.toInt(), startHours.toInt(), startMinutes.toInt())

            val intent = Intent()
            val bundle = Bundle()
            bundle.putString("fecha", startDate)
            intent.putExtras(bundle)
            setResult(0, intent)
            finish()
            return "OK"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }

    inner class EliminarTask() : AsyncTask<String, Void, String>()
    {
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val intent = Intent(mContext, SchedulerActivity::class.java)
            val bundle = Bundle()
            bundle.putString("fecha", startDate)
            intent.putExtras(bundle)
            setResult(0, intent)
            finish()
        }

        override fun doInBackground(vararg params: String?): String {
            val id = params.get(0)!!.toInt()
            val controller = ProyectoController(mContext)
            controller.delete(id)
            return "OK"
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

    }
}


