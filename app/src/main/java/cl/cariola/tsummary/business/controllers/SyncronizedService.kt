package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.business.entities.RegistroHora
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler
import cl.cariola.tsummary.data.DataSend
import cl.cariola.tsummary.data.HoraTS
import java.text.SimpleDateFormat
import java.util.*

class Sincronizador(val _context: Context)
{

    companion object {
        lateinit var mContext : Context
        lateinit var mClient: ApiClient
        lateinit var mDB : DataBaseHandler
    }

    init
    {
        mContext = _context
        mDB = DataBaseHandler(mContext!!)
        mClient = ApiClient()
    }

    public fun prepareSend(registros: List<RegistroHora>): DataSend {
        val registrosTS = registros.map { it ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            HoraTS(it.mCorrelativo, it.mProyectoId,
                    dateFormat.format(it.mFechaHoraStart), it.mAsunto,
                    it.mHoraTotal.horas, it.mHoraTotal.minutos,
                    it.mAbogadoId, it.mOffLine,
                    dateFormat.format(it.mFechaInsert),
                    it.mEstado.value)
        }
        val dataSend = DataSend(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()), registrosTS)
        return dataSend
    }


    fun pull(_idAbogado: Int, _startDate: String, _endDate: String, _token: String)
    {
        val proyectos= mClient.getListProjects(_token)
        mDB.insertListProyectos(proyectos!!)

        val horas= mClient.getListHours(_idAbogado, "2018-04-01", "2018-05-01", _token)
        mDB.insertListRegistroHora(horas!!)
    }

    fun push(_registros: List<RegistroHora>, _token: String) {
        val dataSend = prepareSend(_registros)
        mClient.pushHours(dataSend, _token)
    }
}