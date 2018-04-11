package cl.cariola.tsummary.business.entities

data class Cliente (
    var codigo: Int,
    var nombre: String,
    var proyectos: ArrayList<Proyecto>?,
    var idioma: String
)