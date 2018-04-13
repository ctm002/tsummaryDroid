package cl.cariola.tsummary.business.controllers
import android.content.Context
import cl.cariola.tsummary.business.entities.*
import cl.cariola.tsummary.data.ApiClient
import cl.cariola.tsummary.data.DataBaseHandler
import java.text.SimpleDateFormat
import java.util.*

class ProyectoController(context: Context) {

    private val mContext: Context?
    lateinit var mSincronizador: Sincronizador
    lateinit var mDB: DataBaseHandler

    init {
        this.mContext = context
        this.mSincronizador = Sincronizador(this.mContext)
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

    fun save(id: Int, correlativo: Int, proyectoId: Int, abogadoId: Int, asunto: String, startDate: String, hours: Int, minutes: Int, startHours: Int, startMinutes: Int) {
        var registro: RegistroHora
        if (id != 0) {
            registro = this.mDB.getRegistroHoraById(id)!!
        } else {
            registro = RegistroHora()
            registro.mId = id
        }

        registro.mAbogadoId = abogadoId
        registro.mProyectoId = proyectoId
        registro.mAsunto = asunto
        registro.mHoraTotal = Hora(hours, minutes)
        registro.mOffLine = true

        val sFormat = SimpleDateFormat()
        val dtFecha = sFormat.parse(startDate)
        dtFecha.hours = startHours
        dtFecha.minutes = startMinutes
        registro.mFechaHoraStart = dtFecha

        if (registro.mId == 0) {
            registro.mFechaInsert = Date()
            registro.mFechaUpdate = Date()
            registro.mEstado = Estados.NUEVO
            this.mDB.insertRegistroHora(registro)
        } else {
            registro.mFechaUpdate = Date()
            registro.mEstado = Estados.EDITADO
            this.mDB.updateRegistroHora(registro)
        }

        val sesionLocal = this.mDB.getSesionLocalById(abogadoId)!!
        if (!sesionLocal.isExpired()) {
            val apiClient = ApiClient()
            apiClient.save(registro, sesionLocal.token)
            registro.mEstado = Estados.ANTIGUO
            this.mDB.updateRegistroHora(registro)
        }
    }

    fun delete(id: Int) {
        var registro = this.mDB.getRegistroHoraById(id)
        if (registro != null) {
            val sesionLocal = this.mDB.getSesionLocalById(registro.mAbogadoId)
            if (sesionLocal != null && sesionLocal.isExpired()) {

                registro.mOffLine = true
                registro.mEstado = Estados.ELIMINADO
                registro.mFechaUpdate = Date()

                val apiClient = ApiClient()
                apiClient.delete(registro, sesionLocal.token)

                registro.mEstado = Estados.ANTIGUO
                this.mDB.deleteRegistro(registro)
            }
        }
    }

    fun resetData() {
        this.mDB.resetTables()
    }

    /*
    fun sincronizar(imei: String, startDate: String, endDate: String) {
        var sesionLocal = this.mDB.getSesionLocalByIMEI(imei)!!
        if (!sesionLocal.isExpired())
        {
            val idAbogado = sesionLocal.getIdAbogado()
            val idUsuario = sesionLocal.getIdUsuario()
            val token = sesionLocal.token

            val registros = this.mDB.getListRegistroHoraByIdAbogadoAndEstadoOffline(idAbogado)
            if (registros != null)
            {

                if (registros != null)
                {
                    this.mSincronizador.push(registros, token)
                    this.mSincronizador.pull(idAbogado, startDate, endDate, token)
                }
                else
                {
                    this.mSincronizador.pull(idAbogado, startDate, endDate, token)
                }
            }
            else
            {
                this.mSincronizador.pull(idAbogado, startDate, endDate, token)
            }
        }
    }
    */
}