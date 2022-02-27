package net.mfuertes.nfchatags

import android.content.Context

class NothingConnector: HomeAssitantConnector(name="Nothing", description = "Last tag scanned: %tag_id%") {

    override fun sendTag(context: Context, tagId: String, onFinish: () -> Unit){
        onFinish()
    }
}