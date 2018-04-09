package cl.cariola.tsummary.business.entities
import java.util.Date

class RegistroHora
{
    var mCorrelativo: Int = 0
    var mAsunto: String = ""
    //var mHoras: Int = 0
    //var mMinutos:Int = 0
    var mAbogadoId: Int = 0
    var mModificable: Boolean = false
    var mOffLine: Boolean = false
    var mFechaHoraInicio: Date = Date()
    var mId: Int  = 0
    var mEstado: Int  = 0
    var fechaInsert: Date = Date()
    var fechaUpdate: Date = Date()
    var mInicio: Hora = Hora(0, 0)
    var mFin: Hora = Hora(0,0 )
    var mFechaInsert: Date =  Date()

    var mProyectoId: Int = 0
    var mProyecto: Proyecto? = null
}
