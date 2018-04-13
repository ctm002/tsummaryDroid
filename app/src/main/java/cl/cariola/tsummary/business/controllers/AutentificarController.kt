package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler

class AutentificarController(context: Context){

    private val mContext : Context?
    lateinit var mClient: ApiClient
    lateinit var mDB : DataBaseHandler

    init
    {
        this.mContext = context
        this.mDB = DataBaseHandler(this.mContext!!)
        this.mClient = ApiClient()

    }

    fun register(imei: String, loginName: String, password: String): SesionLocal?
    {
        var sesionLocal = this.mDB.getSesionLocalByIMEI(imei)
        if (sesionLocal == null)
        {
            sesionLocal = this.mClient.register(imei, loginName, password)
            this.mDB.insertSesionLocal(sesionLocal!!)
        }
        else
        {
            if (sesionLocal.isExpired())
            {
                sesionLocal = this.mClient.getNewToken(imei)
                this.mDB.updateSesionLocal(sesionLocal!!)
            }
        }
        return sesionLocal
    }
}