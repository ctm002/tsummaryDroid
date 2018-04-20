package cl.cariola.tsummary.business.entities
import android.database.Cursor
import cl.cariola.tsummary.provider.TSContract
import java.util.Date

class RegistroHora {

    var mCorrelativo: Int = 0
    var mAsunto: String = ""
    var mAbogadoId: Int = 0
    var mModificable: Boolean = false
    var mOffLine: Boolean = false
    var mFechaIng: Date = Date()
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

    constructor(){}

    constructor(cursor: Cursor)
    {

        this.mCorrelativo = cursor.getInt(cursor.getColumnIndex(TSContract.RegistroHora.COL_TIM_CORREL))
        this.mId  = cursor.getInt(cursor.getColumnIndex(TSContract.RegistroHora.COL_ID))

        val cliente = Cliente(0, "", null, "")
        cliente.nombre = cursor.getString(cursor.getColumnIndex(TSContract.Proyecto.COL_CLI_NOM))

        this.mProyecto = Proyecto(0, "", cliente,1, Date())
        this.mProyecto?.nombre = cursor.getString(cursor.getColumnIndex(TSContract.Proyecto.COL_NOMBRE))

        this.mAsunto = cursor.getString(cursor.getColumnIndex(TSContract.RegistroHora.COL_TIM_ASUNTO))


        this.mHoraTotal.horas = cursor.getInt(cursor.getColumnIndex(TSContract.RegistroHora.COL_TIM_HORAS))
        this.mHoraTotal.minutos = cursor.getInt(cursor.getColumnIndex(TSContract.RegistroHora.COL_TIM_MINUTOS))

     }

}
