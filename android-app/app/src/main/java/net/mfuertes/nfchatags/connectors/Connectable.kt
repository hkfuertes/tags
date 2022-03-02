package net.mfuertes.nfchatags.connectors

import android.content.Context
import net.mfuertes.nfchatags.SharedPreference

interface Connectable {
    fun getUniqueId():String
    fun getDisplayName():String
    fun getDescription():String
    fun isEditable(): Boolean
    fun sendTag(context: Context, tagId: String, onFinish: () -> Unit)
    companion object{
        val TYPES = listOf("API", "INTENT", "NOTHING")
        fun getSavedConnector(sharedPreferences: SharedPreference, dbHelper: ApiConnectorDbHelper): Connectable?{
            val current = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD)
            if(current!= null){
                val parts = current.split("|")
                return when(parts[0]){
                    TYPES[0] -> dbHelper.getConnectorById(parts[1].toInt())
                    TYPES[1] -> IntentConnector(packageName = parts[1])
                    else -> NothingConnector()
                }
            }
            return null
        }

        fun saveConnector (sharedPreferences: SharedPreference, connectable: Connectable){
            sharedPreferences.save(SharedPreference.SELECTED_METHOD, connectable.getUniqueId())
        }
    }
}