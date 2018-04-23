package cl.cariola.tsummary.business.entities

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

            var arFecha = _fecha.split(":")
            if (arFecha.size == 2) {
                horas = arFecha.get(0).toInt()
                minutos = arFecha.get(1).toInt()
            }
        }
    }
}