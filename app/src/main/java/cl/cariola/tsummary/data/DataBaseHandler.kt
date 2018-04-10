package cl.cariola.tsummary.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import cl.cariola.tsummary.business.entities.*
import java.text.SimpleDateFormat
import java.util.*

val DATABASE_NAME = "tsummary.db"
val DATABASE_VERSION = 1

class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TbUsuario.createTable)
        db?.execSQL(TbProyecto.createTable)
        db?.execSQL(TbHora.createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {

    }

    fun insertSesionLocal(sesion: SesionLocal): Long
    {
        val db = this.writableDatabase
        val cuenta = sesion.cuenta!!
        val usuario = sesion.usuario!!

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val sql = """
            INSERT OR IGNORE INTO ${TbUsuario.TABlE_NAME} (
                ${TbUsuario.COL_ID}, ${TbUsuario.COL_NOMBRE}, ${TbUsuario.COL_EMAIL}, ${TbUsuario.COL_GRUPO}, ${TbUsuario.COL_PERFIL},
                ${TbUsuario.COL_LOGINNAME}, ${TbUsuario.COL_PASSWORD}, ${TbUsuario.COL_IMEI}, ${TbUsuario.COL_EXPIRESAT}, ${TbUsuario.COL_TOKEN})
            VALUES (
                ${usuario.id}, '${usuario.nombre}', '${usuario.email}', '${usuario.grupo}', '${usuario.perfil}',
                '${cuenta.loginName}', '${cuenta.password}', '${cuenta.imei}',
                '${dateFormat.format(sesion.expiresAt)}', '${sesion.token}')
            """.trimIndent()
        db.execSQL(sql)
        db.close()
        return 1
    }

    fun update(sesion: SesionLocal): Int
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        val returnValue = db.update(TbUsuario.TABlE_NAME, cv, "${TbUsuario.COL_ID}=?", arrayOf(sesion.usuario!!.id.toString()))
        db.close()
        return returnValue
    }

    fun getById(id: Int): SesionLocal?
    {
        val db = this.readableDatabase
        val query = "SELECT * FROM ${TbUsuario.TABlE_NAME} WHERE ${TbUsuario.COL_ID}=$id"
        val cursor = db.rawQuery(query, null)

        var sesionLocal : SesionLocal? = null
        if (cursor.moveToFirst())
        {
            val id = cursor.getInt(cursor.getColumnIndex(TbUsuario.COL_ID))
            val idUsuario = cursor.getInt(cursor.getColumnIndex(TbUsuario.COL_ID_USUARIO))
            val nombre = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_NOMBRE))
            val perfil = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_PERFIL))
            val email = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_EMAIL))
            val grupo = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_GRUPO))

            val imei = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_IMEI))
            val loginName = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_LOGINNAME))
            val password = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_PASSWORD))
            val cuenta = Cuenta(loginName, password, imei)
            val usuario = Usuario(id, nombre, perfil, grupo, email,idUsuario, cuenta)

            val token = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_TOKEN))
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val expiresAt = cursor.getString(cursor.getColumnIndex(TbUsuario.COL_EXPIRESAT))
            val date = formatter.parse(expiresAt)
            sesionLocal  = SesionLocal(usuario, token, date)
        }
        db.close()
        return sesionLocal
    }

    fun insertProyectos(proyectos: List<Proyecto>): Boolean
    {
        val db = this.writableDatabase
        for (item in proyectos)
        {
            val sql = """INSERT OR IGNORE INTO ${TbProyecto.TABlE_NAME}
                (${TbProyecto.COL_ID}, ${TbProyecto.COL_NOMBRE}, ${TbProyecto.COL_CLI_NOM}, ${TbProyecto.COL_IDIOMA}, ${TbProyecto.COL_ESTADO})
                VALUES(${item.id},
                "${item.nombre.replace("'", "\'").replace("\"","'" ) }",
                "${item.cliente.nombre.replace("'", "\'").replace("\"", "'") }",
                "${item.cliente.idioma}", ${item.estado})  """.trimIndent()

            Log.d("INSERT", sql)
            db.execSQL(sql)
        }
        db.close()
        return true
    }

    fun insertHoras(registros: List<RegistroHora>): Boolean
    {
        val db = this.writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        for (item in registros)
        {
            var cv = ContentValues()
            //cv.put(TbHora.COL_ID, item.mId)
            cv.put(TbHora.COL_TIM_CORREL, item.mCorrelativo)
            cv.put(TbHora.COL_PRO_ID, item.mProyectoId)
            cv.put(TbHora.COL_TIM_ASUNTO, item.mAsunto.replace("'", "\'").replace("\"", "'"))
            cv.put(TbHora.COL_TIM_HORAS, item.mHoraTotal.horas)
            cv.put(TbHora.COL_TIM_MINUTOS, item.mHoraTotal.minutos)
            cv.put(TbHora.COL_ABO_ID, item.mAbogadoId)
            cv.put(TbHora.COL_MODIFICABLE, item.mModificable)
            cv.put(TbHora.COL_OFFLINE, item.mOffLine)
            cv.put(TbHora.COL_FECHA_ING, dateFormat.format(item.mFechaInsert))
            cv.put(TbHora.COL_ESTADO, item.mEstado)
            cv.put(TbHora.COL_FECHA_HORA_INICIO, dateFormat.format(item.mFechaHoraInicio))
            db.insert(TbHora.TABlE_NAME, null, cv)
        }
        db.close()
        return true
    }

    fun getListHorasByCodigoAndFecha(codigo: Int, fecha: String) : List<RegistroHora>
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
        while (!cursor.isAfterLast)
        {
            val hora = RegistroHora()
            hora.mId = cursor.getInt(cursor.getColumnIndex(TbHora.COL_ID))
            hora.mCorrelativo  = cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_CORREL))
            hora.mAsunto = cursor.getString(cursor.getColumnIndex(TbHora.COL_TIM_ASUNTO))
            hora.mHoraTotal = Hora(
                            cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_HORAS)),
                            cursor.getInt(cursor.getColumnIndex(TbHora.COL_TIM_MINUTOS)))

            hora.mEstado = cursor.getInt(cursor.getColumnIndex(TbHora.COL_ESTADO))
            hora.mOffLine = (cursor.getInt(cursor.getColumnIndex(TbHora.COL_OFFLINE)) == 0)
            hora.mFechaHoraInicio = formatter.parse(cursor.getString(cursor.getColumnIndex(TbHora.COL_FECHA_HORA_INICIO)))
            hora.mFechaInsert = formatter.parse(cursor.getString(cursor.getColumnIndex(TbHora.COL_FECHA_ING)))

            val cliente = Cliente(cursor.getString(cursor.getColumnIndex(TbProyecto.COL_CLI_NOM)), 0, null, "")
            hora.mProyecto  = Proyecto(
                    cursor.getInt(cursor.getColumnIndex(TbProyecto.COL_ID)),
                    cursor.getString(cursor.getColumnIndex(TbProyecto.COL_NOMBRE)),
                    cliente, 0)
            items.add(hora)
            cursor.moveToNext()
        }
        db.close()
        return items
    }
}