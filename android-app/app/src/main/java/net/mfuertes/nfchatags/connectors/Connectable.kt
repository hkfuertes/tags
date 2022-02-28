package net.mfuertes.nfchatags.connectors

import android.content.Context
import net.mfuertes.nfchatags.SharedPreference

interface Connectable {
    fun getId():String
    fun getDisplayName():String
    fun getDescription():String
    fun isEditable(): Boolean
    fun sendTag(context: Context, tagId: String, onFinish: () -> Unit)
    companion object{
        private val VALUES = listOf<String>("API", "INTENT", "NOTHING")
        fun getSavedConnector(sharedPreferences: SharedPreference): Connectable?{
            var currentType = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD_TYPE)
            var current: Connectable? = null;
            if (currentType == VALUES[0]){
                current = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD_DATA)
                    ?.let { HomeAssistantApiConnector.fromData(it) }
            }else if (currentType == VALUES[1]){
                current = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD_DATA)
                    ?.let { HomeAssistantIntentConnector(packageName = it) }
            }else{
                current = NothingConnector();
            }
            return current
        }

        fun saveConnector (sharedPreferences: SharedPreference, connectable: Connectable){
            if (connectable is HomeAssistantApiConnector){
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, VALUES[0])
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_DATA, HomeAssistantApiConnector.toData(connectable))
            }else if (connectable is HomeAssistantIntentConnector){
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, VALUES[1])
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_DATA, connectable.getId())
            }else{
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, VALUES[2])
            }
        }
    }
}