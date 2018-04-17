package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler

class AutentificarController(context: Context){

    private val mContext : Context?
    lateinit var mClient: ApiClient
    lateinit var mDB : DataBaseHandler
    lateinit var mSincronizador: Sincronizador

    init
    {
        this.mContext = context
        this.mDB = DataBaseHandler(this.mContext!!)
        this.mSincronizador = Sincronizador.getInstance(this.mContext)
    }

    fun register(imei: String, loginName: String, password: String): SesionLocal?
    {
        var sesionLocal = this.mDB.getSesionLocalByIMEI(imei)
        if (sesionLocal == null)
        {
            sesionLocal = ApiClient.register(imei, loginName, password)
            if (sesionLocal != null)
            {
                this.mDB.insertSesionLocal(sesionLocal)
            }
        }
        else
        {
            if (sesionLocal.isExpired())
            {
                sesionLocal = ApiClient.getNewToken(imei)
                this.mDB.updateSesionLocal(sesionLocal!!)
            }
        }
        return sesionLocal
    }
}