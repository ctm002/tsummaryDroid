package cl.cariola.tsummary
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import cl.cariola.tsummary.business.controllers.Autentificar
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.business.entities.RegistroHora

class MainActivity : AppCompatActivity(), AsyncResponse {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.main_listview)
        //listView.adapter = MyCustomAdapter(this)
        //val client = ApiClient()
        //client.asyncResponse = this
        //client.registrar("863166032574597", "Carlos_Tapia", "Car.2711")

        val policy =  StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val controller = Autentificar(this)
        controller.registrar("863166032574597", "Carlos_Tapia", "Car.2711")
    }


    override fun recive(data: Any)
    {

    }

    private class MyCustomAdapter(context: Context): BaseAdapter()
    {
        private val mContext: Context
        private val mRegistroHoras = arrayListOf<RegistroHora>()

        init
        {
            this.mContext = context
        }

        override fun getCount(): Int {
            return 5
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_main, parent, false)

            val tVCliente = rowMain.findViewById<TextView>(R.id.clienteTV)
            val TVProyecto = rowMain.findViewById<TextView>(R.id.proyectoTV)

            return rowMain
        }

        override fun getItem(position: Int): Any {
            return "Test de string"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }
}

