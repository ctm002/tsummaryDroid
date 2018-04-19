package cl.cariola.tsummary.business.entities

import java.util.*

data class Proyecto(var id: Int, var nombre: String, var cliente: Cliente, val estado: Int, var fechaUpdate: Date = Date())
{
}