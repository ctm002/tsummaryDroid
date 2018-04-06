package cl.cariola.tsummary.business.entities

import java.time.LocalDateTime
import java.util.*


class SesionLocal(usuario: Usuario, var token: String, var expiresAt: Date) {

    var cuenta : Cuenta? = null
    var usuario : Usuario?= null
    init
    {
        this.cuenta =  usuario.cuenta
        this.usuario = usuario
    }

}

