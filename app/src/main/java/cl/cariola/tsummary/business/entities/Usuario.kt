package cl.cariola.tsummary.business.entities

data class Usuario (
    var id: Int,
    var nombre: String,
    var perfil: String,
    var grupo: String,
    var email: String,
    var idUsuario: Int,
    var cuenta: Cuenta?
)