package net.mfuertes.nfchatags.connectors

import android.content.Context

class NothingConnector(): Connectable {
    override fun getDescription(): String{
        return "Use this option to see the tag's id."
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