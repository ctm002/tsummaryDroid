package cl.cariola.tsummary.data
import android.util.Log
import cl.cariola.tsummary.AsyncResponse
import org.json.JSONObject
import java.net.URL
import cl.cariola.tsummary.business.entities.*
import com.auth0.android.jwt.JWT
import okhttp3.MediaType
import org.json.JSONArray
import java.text.SimpleDateFormat
import javax.net.ssl.*

class ApiClient {

    val JSON: MediaType = okhttp3.MediaType.parse("application/json; charset=utf-8")!!
    var asyncResponse: AsyncResponse? = null
    val host : String = "docroom.cariola.cl"

    fun registrar(imei: String, userName: String, password: String)
    {

        var json = JSONObject()
        json.put("imei",imei)
        json.put("usuario", userName)
        json.put("password",password)
        val strJSON =  json.toString()

        var httpClient = getHttpClient("tokenmobile", "POST", true)
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)

        try
        {
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val jsonObject = JSONObject(strResponse)
            val token = jsonObject.getString("token")
            val estado = jsonObject.getInt("estado")
            if (estado == 1) {

                val jwt = JWT(token)
                val loginName = jwt.getClaim("LoginName").asString()
                val expiresAt = jwt.expiresAt!!
                val cuenta = Cuenta(loginName!!, password, imei)

                val id : Int = jwt.getClaim("AboId").asInt()!!
                val nombre : String = jwt.getClaim("Nombre").asString()!!
                val perfil : String = jwt.getClaim("Perfil").asString()!!
                val grupo : String = jwt.getClaim("Grupo").asString()!!
                val email : String = jwt.getClaim("Email").asString()!!
                val idUsuario : Int = jwt.getClaim("IdUsuario").asInt()!!
                val usuario = Usuario(id, nombre, perfil, grupo, email, idUsuario, cuenta)
                Log.d("INFO", "${nombre}->${perfil}->${grupo}->${email}->${id}")
                var sesionLocal = SesionLocal(usuario, token, expiresAt)
                asyncResponse?.send(sesionLocal)
            }
        }
        catch (e: Exception){}
    }

    fun getHoras(sesionLocal: SesionLocal): List<RegistroHora>?
    {
        var JSONObject = JSONObject()
                .put("FechaI", "20180415")
                .put("FechaF", "20180515")
                .put("tim_correl", 0)
                .put("AboId", 20)
        val strJSON = JSONObject.toString()

        var httpClient = getHttpClient("api/Horas/GethorasByParameters", "POST", true)
        httpClient.setRequestProperty("Authorization", "bearer ${sesionLocal.token}")
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)

        try
        {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            var horas = ArrayList<RegistroHora>()

            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val objJSON = JSONObject(strResponse)
            var registros= objJSON.get("data") as JSONArray

            for (index in 0..(registros.length()-1))
            {
                val item = registros.getJSONObject(index)
                val registro = RegistroHora()
                registro.mCorrelativo = item.getInt("tim_correl")
                registro.mProyectoId = item.getInt("pro_id")
                registro.mFechaHoraInicio = format.parse(item.getString("fechaInicio"))
                registro.mInicio = Hora(item.getInt("tim_horas"), item.getInt("tim_minutos"))
                registro.mAsunto = item.getString("tim_asunto")
                registro.mFechaInsert = format.parse(item.getString("tim_fecha_insert"))
                registro.mEstado = Estados.ANTIGUO
                registro.mModificable = item.getInt("nro_folio") == 0
                registro.mOffLine = false
                registro.mAbogadoId = item.getInt("abo_id")
                horas.add(registro)
                //Log.d("HOUR", item.toString())
            }
            buffer.close()
            return horas
        }
        catch (exception: Exception) { throw Exception("Error en la recuperación de los proyectos $exception.message") }
        return null
    }

    fun getProyectos(sesionLocal: SesionLocal) : List<Proyecto>?
    {
        var httpClient = getHttpClient("api/ClienteProyecto/getUltimosProyectoByAbogadoMob", "POST", true)
        httpClient.setRequestProperty("Authorization", "bearer ${sesionLocal.token}")
        val strJSON = "{\"abo_id\": 20, \"cantidad\":0}"
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)

        try
        {
            var proyectos = ArrayList<Proyecto>()
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val objJSON = JSONObject(strResponse)
            var clienteProyectos= objJSON.get("data") as JSONArray
            for (index in 0..(clienteProyectos.length()-1))
            {
                val item = clienteProyectos.getJSONObject(index)
                val cliente = Cliente(item.getInt("cli_cod"), item.getString("nombreCliente"),null,  item.getString("idioma"))
                val proyecto = Proyecto(item.getInt("pro_id"), item.getString("nombreProyecto"), cliente, item.getInt("estado"))
                proyectos.add(proyecto)
                Log.d("PROJECT", item.toString())
            }
            return proyectos
            buffer.close()
        }
        catch (exception: Exception) { throw Exception("Error en la recuperación de los proyectos $exception.message") }

        return null
    }

    fun getHttpClient( strURL: String, method: String, doOutput: Boolean) : HttpsURLConnection
    {
        var url = URL("https://$host/$strURL")
        val httpClient =  url.openConnection() as HttpsURLConnection
        httpClient.doOutput = doOutput
        httpClient.requestMethod = method
        httpClient.connectTimeout = 300000
        httpClient.setRequestProperty("Charset", "utf-8")
        httpClient.setRequestProperty("Content-Type", "application/json")
        httpClient.setRequestProperty("User-Agent", "OkHttp")
        httpClient.setRequestProperty("Host", "$host")
        return httpClient
    }

}