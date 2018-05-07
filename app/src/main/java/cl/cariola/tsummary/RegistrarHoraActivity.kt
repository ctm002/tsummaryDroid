package cl.cariola.tsummary

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import cl.cariola.tsummary.business.controllers.ProyectoController
import cl.cariola.tsummary.business.entities.Proyecto
import cl.cariola.tsummary.business.entities.RegistroHora
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_registrar_hora.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity



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
    lateinit var editTxtProyectos: AutoCompleteTextView
    lateinit var editTxtFechaIng: TextView

    private val TAG = "RegistrarHoraActivity"
    lateinit var mContext: Context


    override fun onStart() {
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_hora)
        loadProjets()
        initialize()
        btnDeleteSetOnClickListener()
        btnSaveSetOnClickListener()

        val bundle = intent.extras
        val regJSON = bundle!!.getString("registro")
        val gson = Gson()
        var registro = gson.fromJson(regJSON, RegistroHora::class.java)
        loadData(registro)
        this.mContext = this

        val toolBar =  findViewById<Toolbar>(R.id.tool_bar_registrar_hora)
        toolBar.setTitle("Scheduler")
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Hide:
        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        //Show
        //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)

        if (editTxtProyectos.text.toString().isEmpty())
            showSoftKeyboard(editTxtProyectos)
    }

    fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_registrar_hora, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun loadData(registro: RegistroHora) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        this.startDate = dateFormat.format(registro.mFechaIng)
        //setTitle(this.startDate)

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
            this.editTxtProyectos.setText("${registro.getNombreCliente()} ${registro.mProyecto?.nombre}")
            this.autoCompleteTextView.setSelection(this.editTxtProyectos.text.length)
        }

        val aListProyectos = this.projects.map { p -> "${p.cliente.nombre} ${p.nombre}" } as ArrayList<String>
        this.editTxtProyectos.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, aListProyectos))
        this.editTxtProyectos.threshold = 2
        this.editTxtProyectos.setOnItemClickListener { parent, view, position, id ->
            val selected = parent.getItemAtPosition(position) as String
            Log.d(TAG, selected)
            val index = aListProyectos.indexOf(selected)
            Log.d(TAG, index.toString())
            this.itemSelected = this.projects.get(index)
            Log.d(TAG, itemSelected.id.toString())
        }

        this.editTxtFechaIng.setOnClickListener {  view ->
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            var dpDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
            { view, year, month, day ->
                val monthTemp = month + 1
                val strFecha: String = "${year}-${String.format("%02d", monthTemp)}-${String.format("%02d", day)}"
                this.startDate = strFecha
                val date = dateFormat.parse(startDate)
                var styleFormat = SimpleDateFormat("E, d MMMM")
                this.editTxtFechaIng.setText(styleFormat.format(date))
            }, year, month, day)
            dpDialog.show()
        }


        val date = dateFormat.parse(startDate)
        var styleFormat = SimpleDateFormat("E, d MMMM")
        editTxtFechaIng.setText(styleFormat.format(date))
    }

    /*
    private fun setTitleBarTools() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse(startDate)

        var styleFormat = SimpleDateFormat("E, d MMMM")
        setTitle(styleFormat.format(date))
    }
    */

    private fun initialize() {
        this.editTxtHorasIni = findViewById(R.id.editTxtInicioHoras)
        this.editTxtMinIni = findViewById(R.id.editTxtInicioMinutos)
        this.editTxtHorasTrab = findViewById(R.id.editTxtTrabHoras)
        this.editTxtMinTrab = findViewById(R.id.editTxtTrabMinutos)
        this.editTxtAsunto = findViewById(R.id.editTxtNotas)
        this.editTxtProyectos = findViewById(R.id.autoCompleteTextView)
        this.editTxtFechaIng = findViewById(R.id.tvFechaIng)
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
        /*
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var dpDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
        { view, year, month, day ->
            val monthTemp = month + 1
            val strFecha: String = "${year}-${String.format("%02d", monthTemp)}-${String.format("%02d", day)}"
            this.startDate = strFecha
            //setTitleBarTools()
        }, year, month, day)
        dpDialog.show()
        */
        when(item?.itemId) {
            R.id.action_eliminar -> {
                val task = EliminarTask()
                task.execute(this.id.toString())
            }
            R.id.action_guardar -> {
                registrarHora()
            }
            else ->
            {
                val intent = Intent()
                val bundle = Bundle()
                bundle.putString("fecha", startDate)
                intent.putExtras(bundle)
                setResult(0, intent)
                finish()
            }
        }
        return true
    }

    private fun btnSaveSetOnClickListener() {
        try {
            /*
            val btnGuardar = findViewById<Button>(R.id.btnGuardar)
            btnGuardar.setOnClickListener {
                registrarHora()
            }*/
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun registrarHora() {
        val registrar = RegistrarTask()
        registrar.execute()
    }

    private fun btnDeleteSetOnClickListener() {
        /*
        val btnEliminar = findViewById<Button>(R.id.btnResetData)
        btnEliminar.setOnClickListener {
            val task = EliminarTask()
            task.execute(this.id.toString())
        }
        */
    }

    fun isValid(): Boolean {
        val hours: String = editTxtHorasTrab.text.toString()
        val minutes: String = editTxtMinTrab.text.toString()

        if (hours == "0" && minutes == "0") return false

        if (hours.isNullOrBlank() && minutes.isNullOrBlank()) return false

        if (hours.isNullOrEmpty() && minutes.isNullOrEmpty()) return false

        return true


    }

    inner class RegistrarTask() : AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                if (!(::itemSelected.isInitialized)) throw Exception("Proyecto no ingresado")

                if (!isValid()) throw Exception("Horas de trabajo no ingresadas")

                val proyectoId: Int = itemSelected.id
                val hours: String = editTxtHorasTrab.text.toString()
                val minutes: String = editTxtMinTrab.text.toString()
                val startHours: String = if (editTxtHorasIni.text.isNullOrEmpty()) "0" else editTxtHorasIni.text.toString()
                val startMinutes: String = if (editTxtMinIni.text.isNullOrEmpty()) "0" else editTxtMinIni.text.toString()
                val asunto: String = editTxtAsunto.text.toString()
                if (asunto.isNullOrEmpty() || asunto.isNullOrBlank()) throw Exception("Asunto no ingresado")

                val abogadoId = idAbogado

                val controller = ProyectoController(mContext)
                controller.save(id, correlativo, proyectoId, abogadoId, asunto, startDate, hours.toInt(), minutes.toInt(), startHours.toInt(), startMinutes.toInt())
                return "OK"
            } catch (ex: Exception) {
                return ex.message!!
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result.equals("OK")) {
                val intent = Intent()
                val bundle = Bundle()
                bundle.putString("fecha", startDate)
                intent.putExtras(bundle)
                setResult(0, intent)
                finish()
            } else {
                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show()
            }
        }

    }

    inner class EliminarTask() : AsyncTask<String, Void, String>() {
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


