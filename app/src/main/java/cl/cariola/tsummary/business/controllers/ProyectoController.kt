package cl.cariola.tsummary.business.controllers

import android.content.Context
import cl.cariola.tsummary.business.entities.RegistroHora
import cl.cariola.tsummary.data.DataBaseHandler
import java.text.SimpleDateFormat
import java.util.*

class ProyectoController(context: Context) {

    private val mContext : Context?

    init {
        this.mContext = context
    }

    fun getListHorasByCodigoAndFecha(codigo: Int, fecha: Date) : List<RegistroHora>
    {
        var db = DataBaseHandler(this.mContext!!)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val strDate =   dateFormat.format(fecha)
        return db.getListHorasByCodigoAndFecha(codigo, strDate)
    }
}