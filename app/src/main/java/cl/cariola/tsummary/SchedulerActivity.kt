package cl.cariola.tsummary

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
import android.widget.Toast
import cl.cariola.tsummary.business.entities.Estados
import cl.cariola.tsummary.business.entities.Hora
import cl.cariola.tsummary.business.entities.RegistroHora
import cl.cariola.tsummary.provider.TSContract
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class SchedulerActivity : AppCompatActivity(), AsyncResponse {
    override fun onStart() {
        super.onStart()
        loadItems()
    }

    lateinit var recyclerView: RecyclerView

    lateinit var btnAdd: FloatingActionButton
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val TAG = "SchedulerActivity"

    lateinit var startDate: Date
    var idAbogado: Int = 0


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

        //loadItems()

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
            //startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun loadItems() {
        val selection = "${TSContract.RegistroHora.COL_ABO_ID}=? AND r.${TSContract.RegistroHora.COL_ESTADO}!=?"
        var selectionArgs = arrayOf<String>(this.idAbogado.toString(), Estados.ELIMINADO.value.toString())
        val contentResolver = this.contentResolver
        val cursor = this.contentResolver.query(TSContract.RegistroHora.CONTENT_URI,
                TSContract.RegistroHora.PROJECTION_REGISTRO_HORA_PROYECTO,
                selection,
                selectionArgs,
                " p.${TSContract.RegistroHora.COL_PRO_ID} ASC, ${TSContract.RegistroHora.COL_FECHA_HORA_INICIO} ASC")
        Log.d(TAG, cursor.count.toString())
        val adapter = ListHorasAdapter(cursor, this)
        this.recyclerView.adapter = adapter
    }

    override fun send(data: Any) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Toast.makeText(this, item?.itemId.toString(), Toast.LENGTH_LONG).show()
        return true
    }

}

