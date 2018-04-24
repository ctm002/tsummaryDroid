package cl.cariola.tsummary.business.entities

import com.auth0.android.jwt.JWT


class SesionLocal {

    lateinit var authToken: String
    lateinit var imei: String
    private var id: Int = 0
    private lateinit var jwt : JWT

    constructor(){}

    constructor(_token: String, _imei : String)
    {
        this.authToken = _token
        this.jwt = JWT(this.authToken)
        this.imei = _imei
    }

    constructor(_token: String, _imei : String, _id : Int)
    {
        this.authToken = _token
        this.imei = _imei
        this.id = _id
    }

    constructor(_AuthToken: String)
    {
        this.authToken = _AuthToken
        this.jwt = JWT(this.authToken)
    }

    fun getExpiredAt() = jwt.expiresAt!!

    fun isExpired(): Boolean
    {
        return false
    }

    fun getIMEI() : String = this.imei
    fun getId() : Int = this.id
    fun getIdAbogado() : Int = jwt.getClaim("AboId").asInt()!!
    fun getLoginName() = jwt.getClaim("LoginName").asString()
    fun getNombre() = jwt.getClaim("Nombre").asString()!!
    fun getPerfil() = jwt.getClaim("Perfil").asString()!!
    fun getGrupo() = jwt.getClaim("Grupo").asString()!!
    fun getEmail() = jwt.getClaim("Email").asString()!!
    fun getIdUsuario() = jwt.getClaim("IdUsuario").asInt()!!

    companion object
    {
        fun create() = SesionLocal()
    }

}

