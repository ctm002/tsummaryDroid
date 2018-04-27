package cl.cariola.tsummary

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.CalendarView
import android.widget.Toast
import cl.cariola.tsummary.business.entities.Estados
import cl.cariola.tsummary.business.entities.Hora
import cl.cariola.tsummary.business.entities.RegistroHora
import cl.cariola.tsummary.provider.TSContract
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class SchedulerActivity : AppCompatActivity(), AsyncResponse {

    lateinit var recyclerView: RecyclerView
    lateinit var btnAdd: FloatingActionButton
    lateinit var startDate: Date
    var idAbogado: Int = 0
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val TAG = "SchedulerActivity"

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scheduler, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        this.recyclerView = findViewById<RecyclerView>(R.id.recycler_view_horas)
        this.recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        val bundle = intent.extras
        this.startDate = dateFormat.parse(bundle.getString("fecha"))
        this.idAbogado = bundle.getInt("idAbogado")!!

        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            var registro = RegistroHora()
            registro.mId = 0
            registro.mCorrelativo = 0
            registro.mAbogadoId = this.idAbogado
            registro.mEstado = Estados.NUEVO
            registro.mFechaIng = this.startDate

            val date = Date()
            registro.mInicio = Hora(date.hours, date.minutes)

            val gson = Gson()
            var intent = Intent(this, RegistrarHoraActivity::class.java)
            intent.putExtra("registro", gson.toJson(registro))
            startActivityForResult(intent, 0)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        loadItems(dateFormat.format(startDate))
    }

    private fun loadItems(_fecha: String) {
        val selection = "${TSContract.RegistroHora.COL_ABO_ID}=? AND r.${TSContract.RegistroHora.COL_ESTADO}!=? " +
                "AND strftime('%Y-%m-%d',${TSContract.RegistroHora.COL_FECHA_ING})=?"
        var selectionArgs = arrayOf<String>(this.idAbogado.toString(), Estados.ELIMINADO.value.toString(), _fecha)
        val contentResolver = this.contentResolver
        val cursor = this.contentResolver.query(TSContract.RegistroHora.CONTENT_URI,
                TSContract.RegistroHora.PROJECTION_REGISTRO_HORA_PROYECTO,
                selection,
                selectionArgs,
                "${TSContract.RegistroHora.COL_FECHA_HORA_INICIO} ASC, p.${TSContract.RegistroHora.COL_PRO_ID} ASC")
        val adapter = ListHorasAdapter(cursor, this)
        this.recyclerView.adapter = adapter
    }

    override fun send(data: Any) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var dpDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
        { view, year, month, day ->
            val monthTemp = month + 1
            val strFecha = "${year}-${String.format("%02d", monthTemp)}-${String.format("%02d", day)}"
            this.loadItems(strFecha)
            this.startDate = dateFormat.parse(strFecha)
        }, year, month, day)
        dpDialog.show()

        return true

    }
}

