package cl.cariola.tsummary.business.controllers
import android.content.Context
import cl.cariola.tsummary.business.entities.*
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
        val strDate =  dateFormat.format(fecha)
        return db.getListHorasByCodigoAndFecha(codigo, strDate)
    }

    fun getListProyectos(): List<Proyecto>
    {
        var db = DataBaseHandler(this.mContext!!)
        return db.getListProyectos()
    }

    fun save(id: Int, correlativo: Int, proyectoId: Int, abogadoId: Int, asunto: String, startDate: String, hours: Int, minutes: Int, startHours: Int, startMinutes: Int)
    {
        val registro = RegistroHora()
        registro.mId = id
        registro.mCorrelativo = correlativo
        registro.mAbogadoId = abogadoId
        registro.mProyectoId = proyectoId
        registro.mAsunto = asunto
        registro.mHoraTotal = Hora(hours, minutes)
        registro.mOffLine = true

        val sFormat = SimpleDateFormat()
        val dtFecha= sFormat.parse(startDate)
        dtFecha.hours = startHours
        dtFecha.minutes = startMinutes

        registro.mFechaHoraInicio = dtFecha
        registro.mFechaInsert = Date()
        registro.mFechaUpdate = Date()

        registro.mEstado = (if (correlativo == 0)  Estados.NUEVO else Estados.EDITADO)
        var db = DataBaseHandler(this.mContext!!)
        db.insertHora(registro)

    }

    fun delete(id: Int, correlativo: Int)
    {
        var db = DataBaseHandler(this.mContext!!)
        var registro = db.getRegistroHoraById(id)
        registro?.mEstado = Estados.ELIMINADO
        registro?.mFechaInsert = Date()
        db.deleteRegistro(registro)
    }

    fun resetData()
    {
        var db = DataBaseHandler(this.mContext!!)
        db.resetTables()
    }

}