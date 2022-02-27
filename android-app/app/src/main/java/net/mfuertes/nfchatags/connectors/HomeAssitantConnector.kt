package net.mfuertes.nfchatags

import android.content.ActivityNotFoundException
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject


open class HomeAssitantConnector(
    var ip: String? = null,
    var port: Int? = null,
    var pat: String? = null,
    var packageName: String? = null,
    var description: String? = null,
    var name: String) {
    companion object{
        var connector = HomeAssitantConnector(
            ip = "10.9.8.254",
            port = 8123,
            pat = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI4M2IxNzNlODEzZGI0ZjY2OGYyOTcxODU2ZDRhMDdjZSIsImlhdCI6MTY0NTg5Mzc4NywiZXhwIjoxOTYxMjUzNzg3fQ.5qX_8cV4JhMInLeAsebQhUJh9wrbV_fZbbUiEz82McU",
            packageName = null,
            name = "Madrid"
        )
    }

    override operator fun equals(other: Any?): Boolean{
        val gson = Gson()
        return gson.toJson(other).equals(gson.toJson(this))
    }

    private val endpoint = "/api/events/tag_scanned";

    open fun sendTag(context: Context, tagId: String, onFinish: () -> Unit){
        //We prefer the package name, intent method.
        if (packageName == null){
            postTag(context, tagId, onFinish)
        }else{
            openIntent(context, tagId, onFinish)
        }
    }

    private fun openIntent(context: Context, tagId: String, onFinished: () -> Unit){
        val newIntent = Intent(Intent.ACTION_VIEW)
        newIntent.setPackage(packageName)
        newIntent.data = Uri.parse("https://www.home-assistant.io/tag/$tagId")
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(newIntent)
            onFinished()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No HomeAssitant app detected on device.", Toast.LENGTH_LONG).show()
        }

    }

    private fun _getUrl(): String{
        return "http://"+ip+":"+port.toString()+endpoint
    }
    private fun postTag(context: Context, tagId: String, onFinished: () -> Unit) {
        val url = _getUrl()
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