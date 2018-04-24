package cl.cariola.tsummary.business.entities

import java.text.SimpleDateFormat

class Hora
{
    var horas: Int = 0
    var minutos: Int = 0

    constructor(_Horas: Int, _Minutos: Int)
    {
        horas = _Horas
        minutos = _Minutos
    }

    constructor(_fecha: String)
    {
        if (!_fecha.isNullOrBlank() || !_fecha.isNullOrEmpty()) {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val dtInicio = dateFormat.parse(_fecha)
            horas = dtInicio.hours
            minutos = dtInicio.minutes
        }
    }
}