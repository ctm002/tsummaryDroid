package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.AsyncResponse
import cl.cariola.tsummary.business.entities.Cuenta
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler

class Autentificar(context: Context) : AsyncResponse  {

    private val mContext : Context?

    init {
        this.mContext = context
    }

    override fun send(response: Any) {
        if (response is Cuenta)
        {
            var db = DataBaseHandler(this.mContext!!)
            db.insertData(response)
        }
    }

    fun registrar(imei: String, userName: String, password: String){

        val client = ApiClient()
        client.asyncResponse = this
        client.registrar(imei, userName, password)
    }
}