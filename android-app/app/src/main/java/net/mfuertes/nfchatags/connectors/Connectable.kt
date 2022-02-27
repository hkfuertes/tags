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
        fun getSavedConnector(sharedPreferences: SharedPreference): Connectable?{
            var currentType = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD_TYPE)
            var current: Connectable? = null;
            if (currentType == HomeAssitantApiConnector::javaClass.toString()){
                current = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD)
                    ?.let { HomeAssitantApiConnector.fromJson(it) }
            }else if (currentType == HomeAssitantIntentConnector::javaClass.toString()){
                current = sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD)
                    ?.let { HomeAssitantIntentConnector(name = "Home Assitant App", packageName = it) }
            }else{
                current = NothingConnector(sharedPreferences.getValueString(SharedPreference.LAST_TAG_ID));
            }
            return current
        }

        fun saveConnector (sharedPreferences: SharedPreference, connectable: Connectable){
            if (connectable is HomeAssitantApiConnector){
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, HomeAssitantApiConnector::javaClass.toString())
                sharedPreferences.save(SharedPreference.SELECTED_METHOD, HomeAssitantApiConnector.toJson(connectable))
            }else if (connectable is HomeAssitantIntentConnector){
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, HomeAssitantIntentConnector::javaClass.toString())
                sharedPreferences.save(SharedPreference.SELECTED_METHOD, connectable.getId())
            }else{
                sharedPreferences.save(SharedPreference.SELECTED_METHOD_TYPE, NothingConnector::javaClass.toString())
            }
        }
    }
}