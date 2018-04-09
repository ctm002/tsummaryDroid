package cl.cariola.tsummary.data

class TbProyecto {
    companion object {
        val TABlE_NAME = "Proyectos"
        val COL_ID = "pro_id"
        val COL_CLI_NOM = "cli_nom"
        val COL_NOMBRE = "pro_nombre"
        val COL_IDIOMA = "pro_idioma"
        val COL_ESTADO = "estado"

        val createTable = """
                CREATE TABLE IF NOT EXISTS $TABlE_NAME(
                    $COL_ID INTEGER PRIMARY KEY,
                    $COL_NOMBRE VARCHAR(200),
                    $COL_CLI_NOM VARCHAR(200),
                    $COL_IDIOMA VARCHAR(10),
                    $COL_ESTADO INTEGER);
                    """
    }
}