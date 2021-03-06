package cl.cariola.tsummary.data
import cl.cariola.tsummary.business.entities.Estados
import cl.cariola.tsummary.business.entities.Hora
import cl.cariola.tsummary.business.entities.RegistroHora
import org.json.JSONObject
import java.text.SimpleDateFormat

class RegistroHoraParser
{
    companion object {
        fun parse(item: JSONObject): RegistroHora
        {
            val dateFormat1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

            val registro = RegistroHora()
            registro.mCorrelativo = item.getInt("tim_correl")
            registro.mProyectoId = item.getInt("pro_id")
            registro.mFechaIng = dateFormat1.parse(item.getString("fechaInicio"))
            registro.mInicio = Hora(item.getString("fechaInicio"))
            registro.mAsunto = item.getString("tim_asunto")
            registro.mFechaInsert = dateFormat1.parse(item.getString("tim_fecha_insert"))
            registro.mFechaUpdate = dateFormat1.parse(item.getString("tim_fecha_insert"))
            registro.mEstado = Estados.ANTIGUO
            registro.mModificable = item.getInt("nro_folio") == 0
            registro.mOffLine = false
            registro.mAbogadoId = item.getInt("abo_id")
            registro.mHoraTotal.horas = item.getInt("tim_horas")
            registro.mHoraTotal.minutos = item.getInt("tim_minutos")
            return registro
        }
    }
}