package cl.cariola.tsummary.business.entities
import java.util.Date

class RegistroHora
{
    var mCorrelativo: Int = 0
    var mAsunto: String = ""
    var mAbogadoId: Int = 0
    var mModificable: Boolean = false
    var mOffLine: Boolean = false
    var mFechaHoraInicio: Date = Date()
    var mId: Int  = 0
    var mEstado: Estados  = Estados.ANTIGUO
    var mInicio: Hora = Hora(0, 0)
    var mFin: Hora = Hora(0,0 )
    var mProyectoId: Int = 0
    var mProyecto: Proyecto? = null
    var mHoraTotal = Hora(0,0)

    var mFechaInsert: Date =  Date()
    var mFechaUpdate: Date = Date()

    fun getNombreCliente() : String?
    {
        return this.mProyecto?.cliente?.nombre
    }

    fun getNombreProyecto() : String?
    {
        return this.mProyecto?.nombre
    }

    fun getHoraTotal(): String {
        return "${ String.format("%02d",mHoraTotal.horas)}:${String.format("%02d",mHoraTotal.minutos)}"
    }

}
