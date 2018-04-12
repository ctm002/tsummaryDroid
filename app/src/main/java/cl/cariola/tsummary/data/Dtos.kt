package cl.cariola.tsummary.data

data class DataSend (val Fecha: String, val Lista : List<HoraTS>) {}

data class HoraTS (val  tim_correl : Int, val  pro_id : Int, val  tim_fecha_ing: String, val tim_asunto: String,
    val tim_horas : Int,
    val tim_minutos : Int,
    val abo_id : Int,
    val OffLine : Boolean,
    val FechaInsert : String,
    val Estado : Int )
{}