package cl.cariola.tsummary.business.entities

data class Cliente (
    var nombre: String,
    var codigo: Int,
    var proyectos: ArrayList<Proyecto>?,
    var idioma: String
)