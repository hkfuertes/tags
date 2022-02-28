package net.mfuertes.nfchatags.connectors

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.gson.Gson


open class HomeAssistantIntentConnector(
    var packageName: String,
    var name: String = "Home Assistant App"
) : Connectable {

    override fun getId(): String {
        return packageName
    }

    override fun getDisplayName(): String {
        return name
    }

    override fun getDescription(): String {
        return packageName
    }

    override fun isEditable(): Boolean {
        return false;
    }

    override fun equals(other: Any?): Boolean {
        return (other is Connectable) && getId() == other.getId()
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