package net.mfuertes.nfchatags.connectors

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson


open class IntentConnector(
    var packageName: String
) : Connectable {

    override fun getUniqueId(): String {
        return Connectable.TYPES[1] + "|" +packageName
    }

    override fun getDisplayName(): String {
        return if(packageName.contains("minimal")) "Home Assistant Minimal App" else "Home Assistant App"
    }

    override fun getDescription(): String {
        return packageName
    }

    override fun isEditable(): Boolean {
        return false;
    }

    override fun equals(other: Any?): Boolean {
        return (other is Connectable) && getUniqueId() == other.getUniqueId()
    }

    override fun sendTag(context: Context, tagId: String, onFinish: () -> Unit) {
        openIntent(context, tagId, onFinish)
    }

    private fun openIntent(context: Context, tagId: String, onFinished: () -> Unit) {
        val newIntent = Intent(Intent.ACTION_VIEW)
        newIntent.setPackage(packageName)
        newIntent.data = Uri.parse("https://www.home-assistant.io/tag/$tagId")
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(newIntent)
            onFinished()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No HomeAssitant app detected on device.", Toast.LENGTH_LONG)
                .show()
        }

    }
}