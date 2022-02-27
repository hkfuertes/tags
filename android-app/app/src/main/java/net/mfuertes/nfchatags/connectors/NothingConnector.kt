package net.mfuertes.nfchatags.connectors

import android.content.Context

class NothingConnector(var lastTagId: String? = "unknown"): Connectable {
    override fun getDescription(): String{
        return "Last tag: $lastTagId"
    }
    override fun getId(): String {
        return "nothing"
    }

    override fun getDisplayName(): String {
        return "Nothing"
    }

    override fun isEditable(): Boolean {
        return false
    }

    override fun sendTag(context: Context, tagId: String, onFinish: () -> Unit) {

    }

}