package cl.cariola.tsummary.data

class TbUsuario {

    companion object
    {
        val TABlE_NAME = "Usuarios"
        val COL_IMEI = "IMEI"
        val COL_TOKEN = "Token"
        val COL_ID = "Id"

        val createTable  = """
            CREATE TABLE IF NOT EXISTS $TABlE_NAME(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_IMEI VARCHAR(100),
                $COL_TOKEN VARCHAR(500));"""
    }
}