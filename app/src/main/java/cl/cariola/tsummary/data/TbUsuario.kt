package cl.cariola.tsummary.data

class TbUsuario {

    companion object
    {

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

        val createTable  = """
                CREATE TABLE IF NOT EXISTS $TABlE_NAME(
                    $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
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
                    $COL_EMAIL VARCHAR(25));"""
    }
}