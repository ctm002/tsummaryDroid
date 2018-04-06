package cl.cariola.tsummary.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.cariola.tsummary.business.entities.Cuenta
import cl.cariola.tsummary.business.entities.SesionLocal
import cl.cariola.tsummary.business.entities.Usuario
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.SimpleFormatter

val DATABASE_NAME = "tsummary.db"
val TABlE_NAME = "Usuarios"
val COL_NOMBRE = "Nombre"
val COL_GRUPO = "Grupo"
val COL_PERFIL = "Perfil"
val COL_TOKEN = "Token"
val COL_ID = "Id"
val COL_IMEI = "IMEI"
val COL_PASSWORD = "Password"
val COL_LOGINNAME = "LoginName"
val COL_EMAIL = "Email"
val COL_DEFAULT = "Default"
val COL_EXPIRESAT = "ExpiresAt"
val COL_IMAGE = "Image"
val COL_ID_USUARIO = "IdUsuario"



class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,1) {

    override fun onCreate(db: SQLiteDatabase?) {

        val createTbUsuarios  = """
                create table $TABlE_NAME (
                    $COL_ID INTEGER PRIMARY KEY,
                    $COL_NOMBRE VARCHAR(100),
                    $COL_GRUPO VARCHAR(100),
                    $COL_LOGINNAME VARCHAR(250),
                    $COL_PASSWORD VARCHAR(20),
                    $COL_IMEI VARCHAR(50),
                    $COL_PERFIL VARCHAR(20),
                    $COL_TOKEN VARCHAR(500),
                    $COL_EXPIRESAT VARCHAR(15),
                    $COL_ID_USUARIO INTEGER,
                    $COL_IMAGE VARCHAR,
                    [$COL_DEFAULT] INTEGER,
                    $COL_EMAIL VARCHAR(25))"""

        db?.execSQL(createTbUsuarios)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {

    }

    fun insert(sesion: SesionLocal): Long
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        val cuenta = sesion.cuenta!!
        val usuario = sesion.usuario!!
        cv.put(COL_ID, usuario.id)
        cv.put(COL_NOMBRE, usuario.nombre)
        cv.put(COL_EMAIL, usuario.email)
        cv.put(COL_GRUPO, usuario.grupo)
        cv.put(COL_PERFIL, usuario.perfil)
        cv.put(COL_LOGINNAME, cuenta.loginName)
        cv.put(COL_PASSWORD, cuenta.password)
        cv.put(COL_IMEI, cuenta.imei)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val strDate = format.format(sesion.expiresAt)
        cv.put(COL_EXPIRESAT, strDate)
        cv.put(COL_TOKEN, sesion.token)
        val returnValue =  db.insert(TABlE_NAME, null, cv)
        db.close()
        return returnValue
    }

    fun update(sesion: SesionLocal): Int
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        val returnValue = db.update(TABlE_NAME, cv, "Id=?", arrayOf(sesion.usuario!!.id.toString()))
        db.close()
        return returnValue
    }


    fun getById(id: Int): SesionLocal?
    {
        val query = "SELECT * FROM $TABlE_NAME WHERE $COL_ID=$id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var sesionLocal : SesionLocal? = null
        if (cursor.moveToFirst())
        {
            val id = cursor.getInt(cursor.getColumnIndex(COL_ID))
            val idUsuario = cursor.getInt(cursor.getColumnIndex(COL_ID_USUARIO))
            val nombre = cursor.getString(cursor.getColumnIndex(COL_NOMBRE))
            val perfil = cursor.getString(cursor.getColumnIndex(COL_PERFIL))
            val email = cursor.getString(cursor.getColumnIndex(COL_EMAIL))
            val grupo = cursor.getString(cursor.getColumnIndex(COL_GRUPO))

            val imei = cursor.getString(cursor.getColumnIndex(COL_IMEI))
            val loginName = cursor.getString(cursor.getColumnIndex(COL_LOGINNAME))
            val password = cursor.getString(cursor.getColumnIndex(COL_PASSWORD))
            val cuenta = Cuenta(loginName, password, imei)
            val usuario = Usuario(id, nombre, perfil, grupo, email,idUsuario, cuenta)

            val token = cursor.getString(cursor.getColumnIndex(COL_TOKEN))
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val expiresAt = cursor.getString(cursor.getColumnIndex(COL_EXPIRESAT))
            val date = formatter.parse(expiresAt)
            sesionLocal  = SesionLocal(usuario, token, date)
        }

        return sesionLocal
    }
}