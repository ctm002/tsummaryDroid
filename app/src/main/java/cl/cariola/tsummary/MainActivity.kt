package cl.cariola.tsummary
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cl.cariola.tsummary.business.controllers.AutentificarController
import cl.cariola.tsummary.business.controllers.ProyectoController


class MainActivity : AppCompatActivity(), AsyncResponse {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //loadData()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_horas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val proyectoController = ProyectoController(this)


        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse("2018-05-02")
        val items = proyectoController.getListHorasByCodigoAndFecha(20, date)

        val adapter = ListHorasAdapter(items, this)
        recyclerView.adapter = adapter
    }

    override fun send(data: Any)
    {

    }

    fun loadData(){
        val policy =  StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val autentificar = AutentificarController(this)
        autentificar.registrar("863166032574597", "Carlos_Tapia", "Car.2711")
    }
}

