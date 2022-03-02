package net.mfuertes.nfchatags

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.mfuertes.nfchatags.connectors.ApiConnector
import net.mfuertes.nfchatags.connectors.ApiConnectorDbHelper

class EditConnectorActivity : AppCompatActivity() {
    private lateinit var dbHelper: ApiConnectorDbHelper
    private var connector: ApiConnector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var connector: ApiConnector? = null
        private var remove: Preference? = null
        private var save: Preference? = null

        private var name: EditTextPreference? = null
        private var ip: EditTextPreference? = null
        private var port: EditTextPreference? = null
        private var pat: EditTextPreference? = null

        private var dbHelper: ApiConnectorDbHelper? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_edit_connector, rootKey)
            name = preferenceManager.findPreference("name")
            ip = preferenceManager.findPreference("ip")
            port = preferenceManager.findPreference("port")
            port?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            pat = preferenceManager.findPreference("pat")
            remove = preferenceManager.findPreference("remove")
            remove?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (connector != null){
                    if (dbHelper?.deleteConnector(connector!!.id!!) ?: -1 > 0)
                        requireActivity().finish()
                }
                return@OnPreferenceClickListener true
            }
            save = preferenceManager.findPreference("save")
            save?.onPreferenceClickListener= Preference.OnPreferenceClickListener {
                if (
                    name?.text?.isNotBlank() == true &&
                    port?.text?.isNotBlank() == true &&
                    ip?.text?.isNotBlank() == true &&
                    pat?.text?.isNotBlank() == true
                ) {
                    val nconnector = ApiConnector(
                        ip = ip!!.text!!,
                        name = name!!.text!!,
                        port = port!!.text!!.toInt(),
                        pat = pat!!.text!!.toString(),
                        id = if (connector != null) connector!!.id else null
                    )
                    if (dbHelper?.upsertConnector(nconnector) ?: -1 > 0)
                        requireActivity().finish()
                }

                return@OnPreferenceClickListener true
            }
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            dbHelper = ApiConnectorDbHelper(context)
            val connectorId = requireActivity().intent.getIntExtra(EditConnectorOld.CONNECTOR_TO_EDIT, -1)
            if (connectorId != -1) {
                connector = dbHelper!!.getConnectorById(connectorId)
            }
            requireActivity().title = connector?.name ?: "Create"
        }

        override fun onResume() {
            name?.apply {
                text = connector?.name
                if (connector == null){
                    isVisible = true
                }
            }
            ip?.text = connector?.ip
            port?.text = if(connector!= null) connector!!.port.toString() else "8123"
            pat?.text = connector?.pat
            remove?.apply { isEnabled = connector != null }

            super.onResume()
        }
    }
}