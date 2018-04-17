package cl.cariola.tsummary.business.entities

data class Proyecto(var id: Int, var nombre: String, var cliente: Cliente, val estado: Int)
{
}