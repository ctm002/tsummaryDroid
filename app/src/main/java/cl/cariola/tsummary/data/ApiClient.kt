package cl.cariola.tsummary.data
import android.util.Log
import org.json.JSONObject
import java.net.URL
import cl.cariola.tsummary.business.entities.*
import com.google.gson.Gson
import okhttp3.MediaType
import org.json.JSONArray
import java.text.SimpleDateFormat
import javax.net.ssl.*

object ApiClient
{
    private val JSON: MediaType = okhttp3.MediaType.parse("application/json; charset=utf-8")!!
    private val host : String = "docroom.cariola.cl"

    fun register(imei: String, loginName: String, password: String): SesionLocal?
    {

        var JSONObjectRequest = JSONObject()
        JSONObjectRequest.put("imei",imei)
        JSONObjectRequest.put("usuario", loginName)
        JSONObjectRequest.put("password",password)
        val strJSON =  JSONObjectRequest.toString()
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)

        var httpClient = getHttpClient("tokenmobile", "POST", true)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)
        try
        {
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val JSONObjectResponse = JSONObject(strResponse)
            val token = JSONObjectResponse.getString("token")
            val estado = JSONObjectResponse.getInt("estado")
            if (estado == 1) {
                val sesionLocal = SesionLocal(token, imei)
                return sesionLocal
            }
        }
        catch (e: Exception){}
        return null
    }

    fun getNewToken(imei: String): SesionLocal?
    {
        var JSONObjectRequest = JSONObject()
        JSONObjectRequest.put("imei",imei)
        val strJSON =  JSONObjectRequest.toString()
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)

        var httpClient = getHttpClient("tokenIMEI", "POST", true)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)
        try
        {
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val JSONObjectResponse = JSONObject(strResponse)
            val token = JSONObjectResponse.getString("authToken")
            val estado = JSONObjectResponse.getInt("estado")
            if (estado == 1) {
                val sesionLocal = SesionLocal(token, imei)
                return sesionLocal
            }

        }
        catch (e: Exception){}
        return null
    }

    fun getListHours(idAbogado: Int, dateStart: String, dateEnd: String, token: String): List<RegistroHora>?
    {
        var JSONObjectRequest = JSONObject()
                .put("FechaI", dateStart)
                .put("FechaF", dateEnd)
                .put("tim_correl", 0)
                .put("AboId", idAbogado)
        val strJSON = JSONObjectRequest.toString()

        var httpClient = getHttpClient("api/Horas/GethorasByParameters", "POST", true)
        httpClient.setRequestProperty("Authorization", "bearer ${token}")
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)

        try
        {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            var horas = ArrayList<RegistroHora>()

            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val JSONObjectResponse = JSONObject(buffer.readText())
            var registros= JSONObjectResponse.get("data") as JSONArray

            for (index in 0..(registros.length()-1))
            {
                val item = registros.getJSONObject(index)
                val registro =  RegistroHoraParser.parse(item)
                horas.add(registro)
                //Log.d("HOUR", item.toString())
            }
            buffer.close()
            return horas
        }
        catch (exception: Exception) { throw Exception("Error en la recuperación de los horas $exception.message") }
        return null
    }

    fun getListHoursV2(idAbogado: Int, dateStart: String, dateEnd: String, token: String): MutableMap<String, RegistroHora>?
    {
        var JSONObjectRequest = JSONObject()
                .put("FechaI", dateStart)
                .put("FechaF", dateEnd)
                .put("tim_correl", 0)
                .put("AboId", idAbogado)
        val strJSON = JSONObjectRequest.toString()

        var httpClient = getHttpClient("api/Horas/GetHorasByParameters", "POST", true)
        httpClient.setRequestProperty("Authorization", "bearer ${token}")
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)

        try
        {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            var horas = mutableMapOf<String, RegistroHora>()
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val JSONObjectResponse = JSONObject(buffer.readText())
            var registros= JSONObjectResponse.get("data") as JSONArray

            for (index in 0..(registros.length()-1))
            {
                val item = registros.getJSONObject(index)
                val registro =  RegistroHoraParser.parse(item)
                horas.put(registro.mCorrelativo.toString(), registro)
            }

            buffer.close()
            return horas
        }
        catch (exception: Exception) { throw Exception("Error en la recuperación de los horas $exception.message") }
        return null
    }

    fun getListProjects(token: String) : List<Proyecto>?
    {
        var httpClient = getHttpClient("api/ClienteProyecto/getUltimosProyectoByAbogadoMob", "POST", true)
        httpClient.setRequestProperty("Authorization", "bearer ${token}")
        val strJSON = "{\"abo_id\": 0, \"cantidad\":0}" //Para todos los abogados se retorna la misma cantidad de proyectos
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

    fun getListProjectsV2(token: String) : MutableMap<String, Proyecto>?
    {
        var httpClient = getHttpClient("api/ClienteProyecto/getUltimosProyectoByAbogadoMob", "POST", true)
        httpClient.setRequestProperty("Authorization", "bearer ${token}")
        val strJSON = "{\"abo_id\": 0, \"cantidad\":0}" //Para todos los abogados se retorna la misma cantidad de proyectos
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.outputStream.write(postData)

        try
        {
            var proyectos = mutableMapOf<String, Proyecto>()
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val objJSON = JSONObject(strResponse)
            var clienteProyectos= objJSON.get("data") as JSONArray
            for (index in 0..(clienteProyectos.length()-1))
            {
                val item = clienteProyectos.getJSONObject(index)
                val cliente = Cliente(item.getInt("cli_cod"), item.getString("nombreCliente"),null,  item.getString("idioma"))
                val proyecto = Proyecto(item.getInt("pro_id"), item.getString("nombreProyecto"), cliente, item.getInt("estado"))
                proyectos.put(proyecto.id.toString(), proyecto)
                //Log.d("PROJECT", item.toString())
            }
            return proyectos
            buffer.close()
        }
        catch (exception: Exception) { throw Exception("Error en la recuperación de los proyectos $exception.message") }

        return null
    }

    private fun getHttpClient( strURL: String, method: String, doOutput: Boolean) : HttpsURLConnection
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

    fun save(registro: RegistroHora, token: String)
    {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var JSONObjectRequest = JSONObject()
        JSONObjectRequest.put("tim_correl", registro.mCorrelativo)
        JSONObjectRequest.put("pro_id", registro.mProyectoId)

        val strDate = dateFormat.format(registro.mFechaIng)
        JSONObjectRequest.put("tim_fecha_ing",  strDate)
        JSONObjectRequest.put("tim_asunto", registro.mAsunto)
        JSONObjectRequest.put("tim_horas", registro.mHoraTotal.horas)
        JSONObjectRequest.put("tim_minutos", registro.mHoraTotal.minutos)
        JSONObjectRequest.put("abo_id", registro.mAbogadoId)
        JSONObjectRequest.put("OffLine", registro.mOffLine)
        JSONObjectRequest.put("FechaInsert", dateFormat.format(registro.mFechaInsert))
        JSONObjectRequest.put("Estado", registro.mEstado.value)
        val strJSONRequest =  JSONObjectRequest.toString()

        val postData: ByteArray = strJSONRequest.toByteArray(Charsets.UTF_8)
        var httpClient = getHttpClient("api/HorasMobile", "POST",true)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.setRequestProperty("Authorization", "bearer ${token}" )
        httpClient.outputStream.write(postData)
        var buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
        val objJSONResponse = JSONObject(buffer.readText())
        if  (objJSONResponse.get("data") != null)
        {
            val data = objJSONResponse.get("data") as JSONObject
            registro.mCorrelativo = data.getInt("tim_correl")
        }
    }

    fun delete(registro: RegistroHora, token: String)
    {
        var httpClient = getHttpClient("api/Horas/${registro.mCorrelativo}" , "DELETE", true)
        httpClient.setRequestProperty("Authorization", "bearer ${token}")
        var buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
        val JSONObjectResponse = JSONObject(buffer.readText())
        if (JSONObjectResponse.getInt("estado") == 1)
        {

        }
    }

    fun pushHours(data: DataSend, token: String)
    {
        val gson = Gson()
        val strJSON = gson.toJson(data)
        val postData: ByteArray = strJSON.toByteArray(Charsets.UTF_8)

        var httpClient = getHttpClient("api/HorasMobile/Sincronizar", "POST", true)
        httpClient.setRequestProperty("Content-Length", postData.size.toString())
        httpClient.setRequestProperty("Authorization", "bearer ${token}")
        httpClient.outputStream.write(postData)
        try
        {
            val buffer = httpClient.inputStream.bufferedReader(Charsets.UTF_8)
            val strResponse = buffer.readText()
            val JSONObjectResponse = JSONObject(strResponse)
            print(JSONObjectResponse.toString())
        }
        catch (e: Exception){}
    }

}