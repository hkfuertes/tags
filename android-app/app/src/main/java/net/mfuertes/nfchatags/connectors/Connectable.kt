package net.mfuertes.nfchatags.connectors

import android.content.Context

interface Connectable {
    fun getId():String
    fun getDisplayName():String
    fun isEditable(): Boolean
    fun sendTag(context: Context, tagId: String, onFinish: () -> Unit)
}