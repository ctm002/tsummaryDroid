package cl.cariola.tsummary.business.controllers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import cl.cariola.tsummary.business.entities.*
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler
import java.text.SimpleDateFormat
import java.util.*
import android.telephony.TelephonyManager


class ProyectoController(context: Context) {

    private val mContext: Context?
    lateinit var mSincronizador: Sincronizador
    lateinit var mDB: DataBaseHandler

    init {
        this.mContext = context
        this.mSincronizador = Sincronizador.getInstance(this.mContext)
        this.mDB = DataBaseHandler(this.mContext)
    }

    fun getListHorasByCodigoAndFecha(codigo: Int, fecha: Date): List<RegistroHora> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val strDate = dateFormat.format(fecha)
        return this.mDB.getListRegistroHoraByCodigoAndFecha(codigo, strDate)
    }

    fun getListProyectos(): List<Proyecto> {
        return this.mDB.getListProyectos()
    }

    fun save(id: Int, correlativo: Int, proyectoId: Int, abogadoId: Int, asunto: String,
             startDate: String, hours: Int, minutes: Int, startHours: Int, startMinutes: Int) {
        var registro: RegistroHora
        if (id != 0) {
            registro = this.mDB.getRegistroHoraById(id)!!
        } else {
            registro = RegistroHora()
        }

        registro.mAbogadoId = abogadoId
        registro.mProyectoId = proyectoId
        registro.mAsunto = asunto
        registro.mHoraTotal = Hora(hours, minutes)
        registro.mOffLine = true

        val sFormat = SimpleDateFormat("yyyy-MM-dd")
        val dtFecha = sFormat.parse(startDate)
        registro.mFechaIng = dtFecha
        registro.mFechaUpdate = Date()
        registro.mInicio = Hora(startHours, startMinutes)

        if (registro.mId == 0) {
            registro.mFechaInsert = Date()
            registro.mEstado = Estados.NUEVO
            val newId = this.mDB.insertRegistroHora(registro)
            registro.mId = newId.toInt()
        } else {
            registro.mFechaUpdate = Date()
            registro.mEstado = if (registro.mCorrelativo == 0) Estados.NUEVO else Estados.EDITADO
            this.mDB.updateRegistroHora(registro)
        }

        val sesionLocal = this.mDB.getSesionLocalByIdAbogado(abogadoId)!!
        //if (!sesionLocal.isExpired()) {
        val returnCorrelativo = ApiClient.save(registro, sesionLocal.authToken)
        registro.mEstado = Estados.ANTIGUO
        registro.mOffLine = false
        registro.mCorrelativo = returnCorrelativo
        this.mDB.updateRegistroHora(registro)
        //}
    }

    fun delete(id: Int) {
        var registro = this.mDB.getRegistroHoraById(id)
        if (registro != null) {
            val sesionLocal = this.mDB.getSesionLocalByIdAbogado(registro.mAbogadoId)
            if (sesionLocal != null && !sesionLocal.isExpired()) {
                registro.mOffLine = true
                registro.mEstado = Estados.ELIMINADO
                registro.mFechaUpdate = Date()
                ApiClient.delete(registro, sesionLocal.authToken)
                this.mDB.deleteRegistro(registro)
            }
        }
    }

    fun resetData() {
        this.mDB.resetTables()
    }

}