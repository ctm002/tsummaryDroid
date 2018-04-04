package cl.cariola.tsummary.business.entities
import java.util.Date

data class RegistroHora(
        var mCorrelativo: Int,
        var mAsunto: String,
        var mHoras: Int,
        var mMinutos:Int,
        var mAbogadoId: Int,
        var mModificable: Boolean,
        var mOffLine: Boolean,
        var mFechaHoraInicio: Date,
        var mId: Int,
        var mEstado: Int,
        var mProyectoId: Int,
        var fechaInsert: Date,
        var fechaUpdate: Date)