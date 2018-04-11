package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.AsyncResponse
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler

class AutentificarController(context: Context) : AsyncResponse  {

    private val mContext : Context?

    init {
        this.mContext = context
    }

    fun registrar(imei: String, userName: String, password: String){

        val client = ApiClient()
        client.asyncResponse = this
        client.registrar(imei, userName, password)
    }

    override fun send(sesion: Any) {
        if (sesion is SesionLocal)
        {
            var db = DataBaseHandler(this.mContext!!)
            db.insertSesionLocal(sesion)
            //val sesionDB = db.getSesionLocalById(sesion.usuario!!.id)

            val client = ApiClient()
            client.asyncResponse = this

            val proyectos =  client.getProyectos(sesion)
            db.insertProyectos(proyectos!!)

            val horas = client.getHoras(sesion)
            db.insertHoras(horas!!)
        }
    }

}