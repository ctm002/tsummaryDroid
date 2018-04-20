package cl.cariola.tsummary.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import cl.cariola.tsummary.business.entities.*
import cl.cariola.tsummary.provider.TSContract
import java.text.SimpleDateFormat


val DATABASE_NAME = "tsummary.db"
val DATABASE_VERSION = 1

class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TbUsuario.createTable)
        db?.execSQL(TSContract.Proyecto.CREATE_TABLE)
        db?.execSQL(TSContract.RegistroHora.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun insertSesionLocal(sesion: SesionLocal): Long {
        val db = this.writableDatabase
        val sql =
                """
                INSERT OR IGNORE INTO ${TbUsuario.TABlE_NAME} (${TbUsuario.COL_IMEI}, ${TbUsuario.COL_TOKEN})
                VALUES ('${sesion.getIMEI()}', '${sesion.authToken}')
            """.trimIndent()
        db.execSQL(sql)
        db.close()
        return 1
    }

    fun updateSesionLocal(sesion: SesionLocal): Int {
        val db = this.writableDatabase
        var cv = ContentValues()
        val returnValue = db.update(TbUsuario.TABlE_NAME, cv, "${TbUsuario.COL_IMEI}=?", arrayOf(sesion.getIMEI()))
        db.close()
        return returnValue
    }

    fun getSesionLocalById(id: Int): SesionLocal?
    {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val db = this.readableDatabase
        val query = "SELECT * FROM ${TbUsuario.TABlE_NAME} WHERE ${TbUsuario.COL_ID}=$id"
        val cursor = db.rawQuery(query, null)

        var sesionLocal: SesionLocal? = null
        if (cursor.moveToFirst()) {
            val imei = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_IMEI))
            val token = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_TOKEN))
            val id = cursor.getInt(cursor.getColumnIndex(TbUsuario.COL_ID))
            sesionLocal = SesionLocal(imei, token, id)
        }
        db.close()
        return sesionLocal
    }

    fun getSesionLocalByIMEI(imei: String): SesionLocal?
    {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val db = this.readableDatabase
        val query = "SELECT * FROM ${TbUsuario.TABlE_NAME} WHERE ${TbUsuario.COL_TOKEN}=$imei"
        val cursor = db.rawQuery(query, null)

        var sesionLocal: SesionLocal? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(TbUsuario.COL_ID))
            val imei = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_IMEI))
            val token = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_TOKEN))
            sesionLocal = SesionLocal(imei, token, id)
        }
        db.close()
        return sesionLocal
    }

    fun insertListProyectos(proyectos: List<Proyecto>): Boolean
    {
        val db = this.writableDatabase
        for (item in proyectos) {
            val sql = """INSERT OR IGNORE INTO ${TbProyecto.TABlE_NAME}
            (${TbProyecto.COL_ID}, ${TbProyecto.COL_NOMBRE}, ${TbProyecto.COL_CLI_COD}, ${TbProyecto.COL_CLI_NOM}, ${TbProyecto.COL_IDIOMA}, ${TbProyecto.COL_ESTADO})
            VALUES(${item.id},
            "${item.nombre.replace("'", "\'").replace("\"", "'")}",
            "${item.cliente.codigo}",
            "${item.cliente.nombre.replace("'", "\'").replace("\"", "'")}",
            "${item.cliente.idioma}",
            ${item.estado})  """.trimIndent()

            Log.d("INSERT", sql)
            db.execSQL(sql)
        }
        db.close()
        return true
    }

    fun insertListRegistroHora(registros: List<RegistroHora>): Boolean
    {
        val db = this.writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        for (item in registros) {
            var values = ContentValues()
            values.put(TbHora.COL_TIM_CORREL, item.mCorrelativo)
            values.put(TbHora.COL_PRO_ID, item.mProyectoId)
            values.put(TbHora.COL_TIM_ASUNTO, item.mAsunto.replace("'", "\'").replace("\"", "'"))
            values.put(TbHora.COL_TIM_HORAS, item.mHoraTotal.horas)
            values.put(TbHora.COL_TIM_MINUTOS, item.mHoraTotal.minutos)
            values.put(TbHora.COL_ABO_ID, item.mAbogadoId)
            values.put(TbHora.COL_MODIFICABLE, item.mModificable)
            values.put(TbHora.COL_OFFLINE, item.mOffLine)
            values.put(TbHora.COL_FECHA_ING, dateFormat.format(item.mFechaInsert))
            values.put(TbHora.COL_ESTADO, item.mEstado.value)
            values.put(TbHora.COL_FECHA_HORA_INICIO, dateFormat.format(item.mFechaIng))
            db.insert(TbHora.TABlE_NAME, null, values)
        }
        db.close()
        return true
    }

    fun updateRegistroHora(registro: RegistroHora): Boolean
    {
        val db = this.writableDatabase
        var values = ContentValues()
        values.put(TbHora.COL_TIM_CORREL, registro.mCorrelativo)
        values.put(TbHora.COL_PRO_ID, registro.mProyectoId)
        values.put(TbHora.COL_TIM_ASUNTO, registro.mAsunto.replace("'", "\'").replace("\"", "'"))
        values.put(TbHora.COL_TIM_HORAS, registro.mHoraTotal.horas)
        values.put(TbHora.COL_TIM_MINUTOS, registro.mHoraTotal.minutos)
        values.put(TbHora.COL_ABO_ID, registro.mAbogadoId)
        values.put(TbHora.COL_MODIFICABLE, registro.mModificable)
        values.put(TbHora.COL_OFFLINE, registro.mOffLine)
        values.put(TbHora.COL_ESTADO, registro.mEstado.value)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        values.put(TbHora.COL_FECHA_ULT_MOD, dateFormat.format(registro.mFechaUpdate))
        values.put(TbHora.COL_FECHA_HORA_INICIO, dateFormat.format(registro.mFechaIng))

        val args = arrayOf<String>(registro.mId.toString())
        db.update(TbHora.TABlE_NAME, values, "${TbHora.COL_ID}=?", args)
        db.close()
        return true
    }

    fun insertRegistroHora(registro: RegistroHora): Boolean
    {
        val db = this.writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var values = ContentValues()
        values.put(TbHora.COL_TIM_CORREL, registro.mCorrelativo)
        values.put(TbHora.COL_PRO_ID, registro.mProyectoId)
        values.put(TbHora.COL_TIM_ASUNTO, registro.mAsunto.replace("'", "\'").replace("\"", "'"))
        values.put(TbHora.COL_TIM_HORAS, registro.mHoraTotal.horas)
        values.put(TbHora.COL_TIM_MINUTOS, registro.mHoraTotal.minutos)
        values.put(TbHora.COL_ABO_ID, registro.mAbogadoId)
        values.put(TbHora.COL_MODIFICABLE, registro.mModificable)
        values.put(TbHora.COL_OFFLINE, registro.mOffLine)
        values.put(TbHora.COL_ESTADO, registro.mEstado.value)
        values.put(TbHora.COL_FECHA_ING, dateFormat.format(registro.mFechaInsert))
        values.put(TbHora.COL_FECHA_ULT_MOD, dateFormat.format(registro.mFechaUpdate))
        values.put(TbHora.COL_FECHA_HORA_INICIO, dateFormat.format(registro.mFechaIng))
        db.insert(TbHora.TABlE_NAME, null, values)
        db.close()
        return true
    }

    fun getListRegistroHoraByCodigoAndFecha(codigo: Int, fecha: String): List<RegistroHora>
    {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val db = this.readableDatabase
        val query = " SELECT " +
                "   h.${TbHora.COL_ID} ," +
                "   h.${TbHora.COL_TIM_CORREL}," +
                "   h.${TbHora.COL_TIM_ASUNTO}," +
                "   h.${TbHora.COL_TIM_HORAS}," +
                "   h.${TbHora.COL_TIM_MINUTOS}," +
                "   h.${TbHora.COL_ABO_ID}," +
                "   h.${TbHora.COL_MODIFICABLE}," +
                "   h.${TbHora.COL_OFFLINE}," +
                "   h.${TbHora.COL_FECHA_ING}," +
                "   p.${TbProyecto.COL_ID}," +
                "   p.${TbProyecto.COL_NOMBRE}," +
                "   p.${TbProyecto.COL_CLI_NOM}," +
                "   h.${TbHora.COL_ESTADO}," +
                "   h.${TbHora.COL_FECHA_HORA_INICIO}" +
                "   FROM ${TbHora.TABlE_NAME} h INNER JOIN ${TbProyecto.TABlE_NAME} p " +
                "       ON p.${TbProyecto.COL_ID}=h.${TbHora.COL_PRO_ID} " +
                "   WHERE h.${TbHora.COL_ABO_ID}=$codigo " +
                "   AND strftime('%Y-%m-%d', h.${TbHora.COL_FECHA_HORA_INICIO})='$fecha'" +
                "   ORDER BY h.${TbHora.COL_FECHA_HORA_INICIO} ASC"


        Log.d("SQL", query)

        val cursor = db.rawQuery(query, null)
        val items = ArrayList<RegistroHora>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val hora = RegistroHora()
            hora.mId = cursor.getInt(cursor.getColumnIndex(TbHora.COL_ID))
            hora.mCorrelativo = cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_CORREL))
            hora.mAsunto = cursor.getString(cursor.getColumnIndex(TbHora.COL_TIM_ASUNTO))
            hora.mHoraTotal = Hora(
                    cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_HORAS)),
                    cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_MINUTOS)))

            hora.mEstado = Estados.from(cursor.getInt(cursor.getColumnIndex(TbHora.COL_ESTADO)))
            hora.mOffLine = (cursor.getInt(cursor.getColumnIndex(TbHora.COL_OFFLINE)) == 0)
            hora.mFechaIng = formatter.parse(cursor.getString(cursor.getColumnIndex(TbHora.COL_FECHA_HORA_INICIO)))
            hora.mFechaInsert = formatter.parse(cursor.getString(cursor.getColumnIndex(TbHora.COL_FECHA_ING)))

            val cliente = Cliente(0, cursor.getString(cursor.getColumnIndex(TbProyecto.COL_CLI_NOM)), null, "")
            hora.mProyecto = Proyecto(
                    cursor.getInt(cursor.getColumnIndex(TbProyecto.COL_ID)),
                    cursor.getString(cursor.getColumnIndex(TbProyecto.COL_NOMBRE)),
                    cliente, 0)
            items.add(hora)
            cursor.moveToNext()
        }
        db.close()
        return items
    }

    fun getListRegistroHoraByIdAbogadoAndEstadoOffline(codigo: Int): List<RegistroHora>?
    {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val db = this.readableDatabase
        val query = " SELECT " +
                "   h.${TbHora.COL_ID} ," +
                "   h.${TbHora.COL_TIM_CORREL}," +
                "   h.${TbHora.COL_TIM_ASUNTO}," +
                "   h.${TbHora.COL_TIM_HORAS}," +
                "   h.${TbHora.COL_TIM_MINUTOS}," +
                "   h.${TbHora.COL_ABO_ID}," +
                "   h.${TbHora.COL_MODIFICABLE}," +
                "   h.${TbHora.COL_OFFLINE}," +
                "   h.${TbHora.COL_FECHA_ING}," +
                "   p.${TbProyecto.COL_ID}," +
                "   p.${TbProyecto.COL_NOMBRE}," +
                "   p.${TbProyecto.COL_CLI_NOM}," +
                "   h.${TbHora.COL_ESTADO}," +
                "   h.${TbHora.COL_FECHA_HORA_INICIO}" +
                "   FROM ${TbHora.TABlE_NAME} h INNER JOIN ${TbProyecto.TABlE_NAME} p " +
                "       ON p.${TbProyecto.COL_ID}=h.${TbHora.COL_PRO_ID} " +
                "   WHERE h.${TbHora.COL_ABO_ID}=$codigo AND h.${TbHora.COL_ESTADO}=1"

        Log.d("SQL", query)

        val cursor = db.rawQuery(query, null)
        val items = ArrayList<RegistroHora>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val hora = RegistroHora()
            hora.mId = cursor.getInt(cursor.getColumnIndex(TbHora.COL_ID))
            hora.mCorrelativo = cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_CORREL))
            hora.mAsunto = cursor.getString(cursor.getColumnIndex(TbHora.COL_TIM_ASUNTO))
            hora.mHoraTotal = Hora(
                    cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_HORAS)),
                    cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_MINUTOS)))

            hora.mEstado = Estados.from(cursor.getInt(cursor.getColumnIndex(TbHora.COL_ESTADO)))
            hora.mOffLine = (cursor.getInt(cursor.getColumnIndex(TbHora.COL_OFFLINE)) == 0)
            hora.mFechaIng = formatter.parse(cursor.getString(cursor.getColumnIndex(TbHora.COL_FECHA_HORA_INICIO)))
            hora.mFechaInsert = formatter.parse(cursor.getString(cursor.getColumnIndex(TbHora.COL_FECHA_ING)))

            val cliente = Cliente(0, cursor.getString(cursor.getColumnIndex(TbProyecto.COL_CLI_NOM)), null, "")
            hora.mProyecto = Proyecto(
                    cursor.getInt(cursor.getColumnIndex(TbProyecto.COL_ID)),
                    cursor.getString(cursor.getColumnIndex(TbProyecto.COL_NOMBRE)),
                    cliente, 0)
            items.add(hora)
            cursor.moveToNext()
        }
        db.close()
        return items
    }

    fun getListProyectos(): List<Proyecto>
    {
        val db = this.writableDatabase
        val query = "SELECT * FROM ${TbProyecto.TABlE_NAME} "
        val cursor = db.rawQuery(query, null)

        var proyectos = ArrayList<Proyecto>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val id = cursor.getInt(cursor.getColumnIndex(TbProyecto.COL_ID))
            val nombre = cursor.getString(cursor.getColumnIndex(TbProyecto.COL_NOMBRE))
            val estado = cursor.getInt(cursor.getColumnIndex(TbProyecto.COL_ESTADO))

            val idCliente = cursor.getInt(cursor.getColumnIndex(TbProyecto.COL_CLI_COD))
            val nombreCliente = cursor.getString(cursor.getColumnIndex(TbProyecto.COL_CLI_NOM))
            val idioma = cursor.getString(cursor.getColumnIndex(TbProyecto.COL_IDIOMA))
            val cliente = Cliente(idCliente, nombreCliente, null, idioma)
            val proyecto = Proyecto(id, nombre, cliente, estado)
            proyectos.add(proyecto)
            cursor.moveToNext()
        }
        db.close()
        return proyectos
    }

    fun getRegistroHoraById(id: Int): RegistroHora?
    {
        val db = this.writableDatabase
        val query = "SELECT * FROM ${TbHora.TABlE_NAME} WHERE ${TbHora.COL_ID}=$id"
        val cursor = db.rawQuery(query, null)

        var registro: RegistroHora? = null
        if (!cursor.moveToFirst()) {
            registro = RegistroHora()
            registro.mCorrelativo = cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_CORREL))
            registro.mId = cursor.getInt(cursor.getColumnIndex(TbHora.COL_ID))
            registro.mEstado = Estados.from(cursor.getInt(cursor.getColumnIndex(TbHora.COL_ESTADO)))
        }
        db.close()
        return registro
    }

    fun deleteRegistro(registro: RegistroHora?)
    {
        val db = this.writableDatabase
        var strFecha = SimpleDateFormat().format(registro?.mFechaUpdate).format("yyyy-MM-dd HH:mm:ss")
        val query = "UPDATE ${TbHora.TABlE_NAME} " +
                "   SET ${TbHora.COL_ESTADO}=${registro?.mEstado?.value}, ${TbHora.COL_FECHA_ULT_MOD}=$strFecha" +
                "   WHERE ${TbHora.COL_ID}=${registro?.mId}"
        db.execSQL(query)
        db.close()
    }

    fun resetTables()
    {
        val db = this.writableDatabase
        val sql = """
                DELETE FROM ${TbHora.TABlE_NAME};
                DELETE FROM ${TbProyecto.TABlE_NAME};
                DELETE FROM ${TbUsuario.TABlE_NAME};
            """
        db.execSQL(sql)
        db.close()
    }

    fun deleteListRegistroHora()
    {
        val db = this.writableDatabase
        val sql = "DELETE FROM ${TbHora.TABlE_NAME}"
        db.execSQL(sql)
        db.close()
    }

    fun deleteListProyectos()
    {
        val db = this.writableDatabase
        val sql = "DELETE FROM ${TbProyecto.TABlE_NAME}"
        db.execSQL(sql)
        db.close()
    }

}