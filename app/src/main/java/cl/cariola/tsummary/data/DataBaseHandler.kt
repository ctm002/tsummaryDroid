package cl.cariola.tsummary.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.cariola.tsummary.business.entities.Cuenta

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
val COL_EXPIRATEAT = "ExpirateAt"
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
                    $COL_EXPIRATEAT VARCHAR(15),
                    $COL_ID_USUARIO INTEGER,
                    $COL_IMAGE VARCHAR,
                    [$COL_DEFAULT] INTEGER,
                    $COL_EMAIL VARCHAR(25))"""

        db?.execSQL(createTbUsuarios)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }


    fun insertData(cuenta: Cuenta)
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID, cuenta.usuario.id)
        cv.put(COL_NOMBRE, cuenta.usuario.nombre)
        cv.put(COL_PASSWORD, cuenta.password)
        var result = db.insert(TABlE_NAME, null, cv)
    }
}