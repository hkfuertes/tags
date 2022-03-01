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
        private val VALUES = listOf("API", "INTENT", "NOTHING")
        fun getSavedConnector(sharedPreferences: SharedPreference, dbHelper: ApiConnectorDbHelper): Connectable?{
            val currentType = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD_TYPE)
            if (currentType == VALUES[0]){
                sharedPreferences.getValueInt(SharedPreference.SELECTED_METHOD_DATA)?.let {
                    return dbHelper.getConnectorById(it)
                }
            }else if (currentType == VALUES[1]){
                sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD_DATA)
                    ?.let { return HomeAssistantIntentConnector(packageName = it) }
            }else{
                return NothingConnector();
            }
            return null
        }

        fun saveConnector (sharedPreferences: SharedPreference, connectable: Connectable){
            if (connectable is HomeAssistantApiConnector){
                if(connectable.id != null){
                    sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, VALUES[0])
                    sharedPreferences.save(SharedPreference.SELECTED_METHOD_DATA, connectable.id!!)
                }
            }else if (connectable is HomeAssistantIntentConnector){
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, VALUES[1])
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_DATA, connectable.packageName)
            }else{
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, VALUES[2])
            }
        }
    }
}