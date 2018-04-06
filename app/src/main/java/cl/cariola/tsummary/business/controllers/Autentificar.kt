package cl.cariola.tsummary.business.controllers

import android.content.Context
import android.util.Log
import cl.cariola.tsummary.AsyncResponse
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler

class Autentificar(context: Context) : AsyncResponse  {

    private val mContext : Context?

    init {
        this.mContext = context
    }

    override fun send(response: Any) {
        if (response is SesionLocal)
        {
            var db = DataBaseHandler(this.mContext!!)
            //val sesionDB = db.getById(response.usuario!!.id)
            //db.insert(response)

            val client = ApiClient()
            client.asyncResponse = this
            //val clientes =  client.pullClientes(response)
            //val horas = client.pullHoras(response)
        }
    }

    fun registrar(imei: String, userName: String, password: String){

        val client = ApiClient()
        //client.asyncResponse = this
        //client.registrar(imei, userName, password)
    }
}