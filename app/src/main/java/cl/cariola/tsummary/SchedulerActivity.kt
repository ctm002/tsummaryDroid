package cl.cariola.tsummary
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cl.cariola.tsummary.business.controllers.ProyectoController
import cl.cariola.tsummary.business.entities.Estados
import cl.cariola.tsummary.business.entities.RegistroHora
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*


class SchedulerActivity : AppCompatActivity(), AsyncResponse {


    lateinit var recyclerView : RecyclerView
    lateinit var startDate : Date
    lateinit var btnAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)

        this.recyclerView = findViewById<RecyclerView>(R.id.recycler_view_horas)
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        this.startDate = dateFormat.parse("2018-05-02")
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
        val proyectoController = ProyectoController(this)
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        //val date = dateFormat.parse("2018-05-02")
        val items = proyectoController.getListHorasByCodigoAndFecha(20, startDate)
        val adapter = ListHorasAdapter(items, this)
        this.recyclerView.adapter = adapter
    }

    override fun send(data: Any)
    {

    }

}

