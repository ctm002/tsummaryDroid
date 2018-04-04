package cl.cariola.tsummary.business.entities

data class Cuenta (
    var userName: String,
    var password: String,
    var imei: String,
    var usuario: Usuario
)