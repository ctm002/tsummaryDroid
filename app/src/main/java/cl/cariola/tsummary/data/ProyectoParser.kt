package cl.cariola.tsummary.data

import cl.cariola.tsummary.business.entities.Cliente
import cl.cariola.tsummary.business.entities.Proyecto
import org.json.JSONObject

class ProyectoParser
{
    companion object {
        fun parse(item: JSONObject): Proyecto
        {
            val cliente = Cliente(item.getInt("cli_cod"), item.getString("nombreCliente"),null,  item.getString("idioma"))
            val proyecto = Proyecto(item.getInt("pro_id"), item.getString("nombreProyecto"), cliente, item.getInt("estado"))
            return proyecto
        }
    }
}

