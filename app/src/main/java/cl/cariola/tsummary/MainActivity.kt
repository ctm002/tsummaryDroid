package cl.cariola.tsummary
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import cl.cariola.tsummary.business.controllers.ProyectoController
import cl.cariola.tsummary.business.entities.RegistroHora
import java.util.Date

class MainActivity : AppCompatActivity(), AsyncResponse {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_horas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val proyectoController = ProyectoController(this)


        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse("2018-05-02")
        val items = proyectoController.getListHorasByCodigoAndFecha(20, date)

        val adapter = ListHorasAdapter(items)
        recyclerView.adapter = adapter


        //listView.adapter = MyCustomAdapter(this)
        //val client = ApiClient()
        //client.asyncResponse = this
        //client.registrar("863166032574597", "Carlos_Tapia", "Car.2711")

        val policy =  StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //val controller = AutentificarController(this)
        //controller.registrar("863166032574597", "Carlos_Tapia", "Car.2711")
    }


    override fun send(data: Any)
    {

    }
}

