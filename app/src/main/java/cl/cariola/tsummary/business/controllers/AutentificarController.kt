package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.AsyncResponse
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

    fun registrar(imei: String, loginName: String, password: String)
    {
        var sesionLocal = this.mDB.getSesionLocalByIMEI(imei)
        if (sesionLocal == null)
        {
            sesionLocal = this.mClient.register(imei, loginName, password)
            this.mDB.insertSesionLocal(sesionLocal!!)
            pull(sesionLocal)
        }
        else
        {
            if (sesionLocal.isExpired())
            {
                sesionLocal = this.mClient.getNewToken(imei)
                this.mDB.updateSesionLocal(sesionLocal!!)
            }

            val registros = this.mDB.getListRegistroHoraByIdAndEstadoOffline(sesionLocal.getIdAbogado())
        }
    }

    fun pull(sesionLocal: SesionLocal)
    {
        val proyectos= this.mClient.getListProjects(sesionLocal.token)
        this.mDB.insertListProyectos(proyectos!!)

        val horas =  this.mClient.getListHours(sesionLocal.getIdAbogado(), "2018-04-01", "2018-05-01", sesionLocal.token)
        this.mDB.insertListRegistroHora(horas!!)
    }
}