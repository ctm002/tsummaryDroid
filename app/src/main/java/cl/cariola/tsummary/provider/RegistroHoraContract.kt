package cl.cariola.tsummary.provider

import android.net.Uri
import android.provider.BaseColumns
import android.content.ContentResolver

object RegistroHoraContract {

    val AUTHORITY = "cl.cariola.tsummary"
    val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")
    private val PATH_ENTRIES = "entries"


    internal  class Entry : BaseColumns {

        companion object {

            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.basicsyncadapter.entries"
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.basicsyncadapter.entry"
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build()

            val TABlE_NAME = "Horas"
            val COL_ID =  "hora_id"
            val COL_TIM_CORREL = "tim_correl"
            val COL_PRO_ID = "pro_id"
            val COL_TIM_ASUNTO  = "tim_asunto"
            val COL_TIM_HORAS = "tim_horas"
            val COL_TIM_MINUTOS = "tim_minutos"
            val COL_ABO_ID = "abo_id"
            val COL_MODIFICABLE = "modificable"
            val COL_OFFLINE = "offline"
            val COL_FECHA_ING =  "tim_fecha_ing"
            val COL_ESTADO = "estado"
            val COL_FECHA_ULT_MOD = "fecha_ult_mod"
            val COL_FECHA_HORA_INICIO = "fecha_hora_inicio"

            val CREATE_TABLE = """
                CREATE TABLE IF NOT EXISTS ${TABlE_NAME}(
                    ${COL_ID} INTEGER PRIMARY KEY,
                    ${COL_TIM_CORREL} INTEGER,
                    ${COL_PRO_ID} INTEGER,
                    ${COL_TIM_ASUNTO} VARCHAR(500),
                    ${COL_TIM_HORAS} INTEGER,
                    ${COL_TIM_MINUTOS} INTEGER,
                    ${COL_ABO_ID} INTEGER,
                    ${COL_MODIFICABLE} INTEGER,
                    ${COL_OFFLINE} INTEGER,
                    ${COL_FECHA_ING} VARCHAR(20),
                    ${COL_ESTADO} INTEGER,
                    ${COL_FECHA_ULT_MOD} VARCHAR(20),
                    ${COL_FECHA_HORA_INICIO} VARCHAR(20));
                """
        }
    }
}

