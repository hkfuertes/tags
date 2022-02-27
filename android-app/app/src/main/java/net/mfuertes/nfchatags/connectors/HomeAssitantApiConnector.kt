package net.mfuertes.nfchatags.connectors

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.reflect.typeOf

open class HomeAssitantApiConnector(
    var ip: String,
    var port: Int,
    var pat: String,
    var name: String): Connectable {
    companion object{
         fun fromJson(json: String): HomeAssitantApiConnector {
            return Gson().fromJson(json, HomeAssitantApiConnector::class.java)
        }
        fun toJson(connector: HomeAssitantApiConnector): String {
            return Gson().toJson(connector)
        }

        var connector = HomeAssitantApiConnector(
            ip = "10.9.8.254",
            port = 8123,
            pat = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI4M2IxNzNlODEzZGI0ZjY2OGYyOTcxODU2ZDRhMDdjZSIsImlhdCI6MTY0NTg5Mzc4NywiZXhwIjoxOTYxMjUzNzg3fQ.5qX_8cV4JhMInLeAsebQhUJh9wrbV_fZbbUiEz82McU",
            name = "Madrid"
        )
    }

    override fun getId(): String {
        return ip
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getDescription(): String {
        return ip
    }

    override fun isEditable(): Boolean {
        return true;
    }

    override fun equals(other: Any?): Boolean {
        return (other is HomeAssitantApiConnector) && getId() == other.getId()
    }

    private val endpoint = "/api/events/tag_scanned";

    override fun sendTag(context: Context, tagId: String, onFinish: () -> Unit){
        postTag(context, tagId, onFinish)
    }

    private fun getUrl(): String{
        return "http://$ip:$port$endpoint"
    }
    private fun postTag(context: Context, tagId: String, onFinished: () -> Unit) {
        val url = getUrl()
        val jsonBody = JSONObject("{\"tag_id\": \"$tagId\"}")

        val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonBody,
            Response.Listener {
                onFinished()
            },
            Response.ErrorListener { error -> //handle the error
                error.printStackTrace()
            }) {
            //this is the part, that adds the header to the request
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Bearer $pat"
                params["content-type"] = "application/json"
                return params
            }
        }

        Volley.newRequestQueue(context).add(jsonRequest)
    }
}