package cl.cariola.tsummary.data
import android.util.Log
import cl.cariola.tsummary.AsyncResponse
import cl.cariola.tsummary.business.entities.Cuenta
import cl.cariola.tsummary.business.entities.Usuario
import com.auth0.android.jwt.JWT
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class ApiClient {
    val JSON: MediaType = okhttp3.MediaType.parse("application/json; charset=utf-8")!!
    var asyncResponse: AsyncResponse? = null

    fun registrar(imei: String, userName: String, password: String)
    {
        var json = JSONObject()
        json.put("imei",imei)
        json.put("usuario", userName)
        json.put("password",password)
        val strJSON =  json.toString()

        var requestBody = RequestBody.create(JSON, strJSON)
        var url = URL("https://docroom.cariola.cl/tokenmobile")
        val request = Request.Builder()
                .addHeader("Content-Type","application/json")
                .url(url)
                .post(requestBody)
                .build()

        val client = OkHttpClient()
        val response = client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                print("Fallo al ejecutar la consulta")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val strResponse = response?.body()?.string()
                val jsonObject = JSONObject(strResponse)
                val token = jsonObject.getString("token")
                //val estado = jsonObject.getInt("estado")
                //val mensaje = jsonObject.getString("mensaje")

                val jwt = JWT(token)
                val id = jwt.getClaim("AboId").asInt()
                val nombre = jwt.getClaim ("Nombre").asString()
                val perfil = jwt.getClaim("Perfil").asString()
                val grupo = jwt.getClaim("Grupo").asString()
                val email = jwt.getClaim("Email").asString()
                val usuario = Usuario(id!!, nombre!!, perfil!!, grupo!!)

                val loginName = jwt.getClaim("LoginName").asString()
                val cuenta = Cuenta(imei, loginName!!, password, usuario)
                Log.d("INFO", "${nombre}->${perfil}->${grupo}->${email}->${id}")
                asyncResponse?.send(cuenta)
            }
        })
    }
}