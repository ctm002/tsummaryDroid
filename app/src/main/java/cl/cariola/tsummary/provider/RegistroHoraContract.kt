package cl.cariola.tsummary.provider

import android.net.Uri
import android.provider.BaseColumns
import android.content.ContentResolver

object RegistroHoraContract {

    val AUTHORITY = "cl.cariola.tsummary.provider.RegistroHoraProvider"
    val BASE_CONTENT_URI_1 = Uri.parse("content://$AUTHORITY")
    val BASE_CONTENT_URI_2 = Uri.parse("content://$AUTHORITY")
    private val PATH_HORAS = "horas"
    private val PATH_PROYECTOS = "proyectos"

    internal  class Proyecto : BaseColumns {

        companion object {

            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/proyectos"
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/proyectos"
            val CONTENT_URI = BASE_CONTENT_URI_2.buildUpon().appendPath(PATH_PROYECTOS).build()

            val TABlE_NAME = "Proyectos"
            val COL_ID = "pro_id"
            val COL_NOMBRE = "pro_nombre"
            val COL_CLI_COD = "cli_cod"
            val COL_CLI_NOM = "cli_nom"
            val COL_ESTADO = "estado"
            val COL_IDIOMA = "pro_idioma"
            val COL_FECHA_ULT_MOD = "fecha_ult_mod"

            val CREATE_TABLE = """
                CREATE TABLE IF NOT EXISTS $TABlE_NAME(
                    $COL_ID INTEGER PRIMARY KEY,
                    $COL_NOMBRE VARCHAR(200),
                    $COL_CLI_COD INTEGER,
                    $COL_CLI_NOM VARCHAR(200),
                    $COL_IDIOMA VARCHAR(10),
                    $COL_ESTADO INTEGER,
                    $COL_FECHA_ULT_MOD VARCHAR(20))
                """
        }
    }

    internal  class RegistroHora : BaseColumns {

        companion object {

            val CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/horas"
            val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/horas"
            val CONTENT_URI = BASE_CONTENT_URI_1.buildUpon().appendPath(PATH_HORAS).build()

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
            val COL_FECHA_INSERT = "fecha_ult_mod"
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

