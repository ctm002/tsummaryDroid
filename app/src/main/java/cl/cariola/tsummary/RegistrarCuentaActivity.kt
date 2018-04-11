package cl.cariola.tsummary

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import cl.cariola.tsummary.business.controllers.AutentificarController
import cl.cariola.tsummary.business.controllers.ProyectoController

class RegistrarCuentaActivity: AppCompatActivity()
{
    lateinit var editTxtUserName : EditText
    lateinit var editTxtPassword : EditText
    lateinit var btnRegistrar : Button
    lateinit var btnResetData : Button
    lateinit var _context: Context

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cuenta)

        editTxtUserName = findViewById(R.id.editTxtUserName)
        editTxtPassword = findViewById(R.id.editTxtPassword)
        editTxtUserName.setText("Carlos_Tapia")
        editTxtPassword.setText("Car.2711")

        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnResetData = findViewById(R.id.btnResetData)

        this._context = this

        btnResetData.setOnClickListener{
            this.resetData()
        }

        btnRegistrar.setOnClickListener {
            this.initial()
        }
    }

    fun initial()
    {
        var task = RegistrarCuenta()
        task.messages ="Descargando datos..."
        task.actions = Acciones.INITIAL
        task.execute()
    }

    fun resetData()
    {
        var task = RegistrarCuenta()
        task.messages ="Eliminando datos..."
        task.actions = Acciones.ELIMINAR_TODO
        task.execute()
    }

    enum class Acciones(val value : Int)
    {
        ELIMINAR_TODO(0),
        INITIAL(1);

        companion object {
            fun from(findValue: Int): Acciones = Acciones.values().first { it.value == findValue }
        }
    }

    internal inner class RegistrarCuenta(): AsyncTask<Void, Void, String>()
    {

        lateinit var actions : Acciones
        lateinit var messages : String
        lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(_context)
            progressDialog.setTitle(messages)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Void?): String {
            if (NetWorkStatus.isNetworkAvailable(_context))
            {
                if (actions == Acciones.INITIAL)
                {
                    val autentificar = AutentificarController(_context)
                    autentificar.registrar("863166032574597", editTxtUserName.text.toString(), editTxtPassword.text.toString())
                    return "PASS"
                }
                else if (actions == Acciones.ELIMINAR_TODO)
                {
                    val controller = ProyectoController(_context)
                    controller.resetData()
                    return "NO"
                }
                return ""
            }
            else
            {
                return "SIN INTERNET"
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result != null && result == "PASS")
            {
                val intent = Intent(_context, SchedulerActivity:: class.java)
                _context.startActivity(intent)
            }
        }
    }
}