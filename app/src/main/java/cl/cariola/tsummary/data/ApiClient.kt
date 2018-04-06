package cl.cariola.tsummary.data
import android.util.Log
import cl.cariola.tsummary.AsyncResponse
import org.json.JSONObject
import java.net.URL
import cl.cariola.tsummary.business.entities.*
import com.auth0.android.jwt.JWT
import okhttp3.MediaType
import org.json.JSONArray
import java.io.IOException
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
                asyncResponse?.recive(sesionLocal)
            }
        }
        catch (e: Exception){}

        /*
        var requestBody = okhttp3.RequestBody.create(JSON, strJSON)
        var url = URL("https://docroom.cariola.cl/tokenmobile")

        var request = okhttp3.Request.Builder()
                .header("Content-Type","application/json")
                .header("User-Agent", "OkHttp")
                .header("Host", "docroom.cariola.cl")
                .url(url)
                .post(requestBody)
                .build()

        val client = okhttp3.OkHttpClient.Builder().build()
        val response = client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call?, e: IOException?) {
                Log.d("FAIL", "Fallo al ejecuta consulta")
            }

            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                val strResponse = response?.body()?.string()
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
                else
                {
                    val mensaje = jsonObject.getString("mensaje")
                }
            }
        })
        */
    }

    /*

    fun pullV1(sesionLocal: SesionLocal) {
        val strJSON = "{\"abo_id\": 20, \"cantidad\":0}"
        var requestBody: okhttp3.RequestBody = okhttp3.RequestBody.create(JSON, strJSON.toByteArray(Charsets.UTF_8))

        Log.i("INFO", requestBody.toString())

        var host = "docroom.cariola.cl"
        var url = URL("https://$host/api/ClienteProyecto/getUltimosProyectoByAbogadoMob")
        var request = okhttp3.Request.Builder()
                .header("Content-Type","application/json")
                .header("Authorization", "bearer ${sesionLocal.token}")
                .header("User-Agent", "OkHttp")
                .header("Host", "$host")
                .url(url)
                .post(requestBody)
                .build()

        Log.d("LOG", request.toString())

        val client= okhttp3.OkHttpClient.Builder().addInterceptor {  interceptor: okhttp3.Interceptor.Chain ->
            val request= interceptor.request()
            val strBody =  request.body()?.toString()!!
            Log.d("INFO",  "${request.headers()} ${strBody}")

            var response = interceptor.proceed(request)
            response
        }.hostnameVerifier( { hostname: String?, session: SSLSession? -> true }).build()

        val response = client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                Log.d("INFO", response.toString())

                val strResponse = response?.body()?.toString()
                val jsonObjectResponse = JSONObject(strResponse)
                Log.d("I","Proyectos")
            }

            override fun onFailure(call: okhttp3.Call?, e: IOException?) {

            }
        })
    }

    fun useInsecureSSL() {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
        })

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)

        // Create all-trusting host name verifier
        val allHostsValid = HostnameVerifier { _, _ -> true }

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
    }
    */

    fun pullHoras(sesionLocal: SesionLocal): Map<Cliente, List<RegistroHora>>?
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
            var proyectos = ArrayList<Proyecto>()

            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val objJSON = JSONObject(strResponse)
            var registros= objJSON.get("data") as JSONArray
            for (index in 0..(registros.length()-1))
            {
                val item = registros.getJSONObject(index)
                //val cliente = Cliente(item.getString("nombreCliente"), item.getInt("cli_cod"), null)
                //val proyecto = Proyecto(item.getInt("pro_id"), item.getString("nombreProyecto"), cliente, item.getInt("estado"))
                //proyectos.add(proyecto)
                Log.d("HOUR", item.toString())
            }
            val horas = proyectos.groupBy { it.cliente }
            return null
            buffer.close()
        }
        catch (exception: Exception) { throw Exception("Error en la recuperación de los proyectos $exception.message") }

        return null

    }

    fun pullClientes(sesionLocal: SesionLocal) : Map<Cliente, List<Proyecto>>?
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
                val cliente = Cliente(item.getString("nombreCliente"), item.getInt("cli_cod"), null)
                val proyecto = Proyecto(item.getInt("pro_id"), item.getString("nombreProyecto"), cliente, item.getInt("estado"))
                proyectos.add(proyecto)
                Log.d("PROJECT", item.toString())
            }
            val clientes = proyectos.groupBy { it.cliente }
            return clientes
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