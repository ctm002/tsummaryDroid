package cl.cariola.tsummary.business.entities

data class ClienteProyecto(
        var cliente: Cliente,
        var estado: Boolean,
        var id: Int,
        var nombre: String
)
