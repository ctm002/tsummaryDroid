package cl.cariola.tsummary
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import cl.cariola.tsummary.business.entities.Estados
import cl.cariola.tsummary.business.entities.RegistroHora
import cl.cariola.tsummary.provider.TSContract
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class SchedulerActivity : AppCompatActivity(), AsyncResponse {

    lateinit var recyclerView : RecyclerView

    lateinit var btnAdd: FloatingActionButton
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val TAG = "SchedulerActivity"

    lateinit var startDate : Date
    var idAbogado : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        this.recyclerView = findViewById<RecyclerView>(R.id.recycler_view_horas)
        this.recyclerView.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        val bundle = intent.extras
        this.startDate = dateFormat.parse(bundle.getString("fecha"))
        this.idAbogado = bundle.getInt("idAbogado")!!

        loadItems()

        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            var registro = RegistroHora()
            registro.mId = 0
            registro.mCorrelativo = 0
            registro.mAbogadoId = 20
            registro.mEstado = Estados.NUEVO
            val gson = Gson()
            var intent = Intent(this, RegistrarHoraActivity::class.java)
            intent.putExtra("registro", gson.toJson(registro))
            startActivity(intent)
        }
    }

    private fun loadItems() {
        val selection = "${TSContract.RegistroHora.COL_ABO_ID}=? AND strftime('%Y-%m-%d',${TSContract.RegistroHora.COL_FECHA_ING})=?"
        var selectionArgs  = arrayOf<String>(this.idAbogado.toString(),  this.dateFormat.format(this.startDate))
        val contentResolver = this.contentResolver
        val cursor = this.contentResolver.query(TSContract.RegistroHora.CONTENT_URI, TSContract.RegistroHora.PROJECTION_REGISTRO_HORA_PROYECTO , selection, selectionArgs, "")
        Log.d(TAG, cursor.count.toString())
        val adapter = ListHorasAdapter(cursor, this)
        this.recyclerView.adapter = adapter
    }

    override fun send(data: Any)
    {

    }

}

