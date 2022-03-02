package net.mfuertes.nfchatags

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.mfuertes.nfchatags.connectors.*


class ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        title = getString(R.string.app_name)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nfc_white)
        //supportActionBar?.subtitle = "Reuse you NFC cards!"
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var sharedPrefs: SharedPreference
        private var add: Preference? = null
        private var edit: Preference? = null
        private var method: ListPreference? = null
        private lateinit var connectors: Map<String, Connectable>
        private lateinit var dbHelper: ApiConnectorDbHelper
        private val nothingOption = NothingConnector();
        private val haPackages: List<String> = listOf(
            "io.homeassistant.companion.android",
            "io.homeassistant.companion.android.debug",
            "io.homeassistant.companion.android.minimal",
            "io.homeassistant.companion.android.minimal.debug"
        )

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            edit = preferenceManager.findPreference("edit")
            edit?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                connectors[method?.value].also {
                    val intent = Intent(activity, EditConnectorActivity::class.java)
                    intent.putExtra(
                        EditConnectorOld.CONNECTOR_TO_EDIT,
                        (it as ApiConnector).id
                    )
                    startActivity(intent)
                }
                return@OnPreferenceClickListener true
            }
            method = preferenceManager.findPreference("method")
            method?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    edit?.apply { isEnabled = newValue.toString().contains(Connectable.TYPES[0]) }
                    connectors[newValue]?.let { Connectable.saveConnector(sharedPrefs, it) }
                    return@OnPreferenceChangeListener true

                }
            add = preferenceManager.findPreference<Preference>("create")
            add?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(activity, EditConnectorActivity::class.java)
                startActivity(intent)
                return@OnPreferenceClickListener true
            }
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            dbHelper = ApiConnectorDbHelper(context)
            sharedPrefs = SharedPreference(context)
        }

        override fun onResume() {
            connectors = retrieveConnectorMap()
            method?.entries = connectors.values.map { it.getDisplayName() }.toTypedArray()
            method?.entryValues = connectors.values.map { it.getUniqueId() }.toTypedArray()

            Connectable.getSavedConnector(sharedPrefs, dbHelper).also {
                if (it != null) {
                    method?.value = it.getUniqueId()
                }else{
                    method?.value = nothingOption.getUniqueId()
                }
                edit?.apply {
                    if (it != null) {
                        isEnabled = it.getUniqueId().contains(Connectable.TYPES[0])
                    }else{
                        isEnabled = false
                    }
                }
            }
            super.onResume()
        }

        private fun retrieveConnectorMap(): Map<String, Connectable> {
            val connectors = ArrayList<Connectable>()
            haPackages.forEach {
                if (isPackageInstalled(it, requireActivity().packageManager))
                    connectors.add(
                        IntentConnector(
                            packageName = it
                        )
                    )
            }
            connectors.addAll(dbHelper.getConnectors() as ArrayList<Connectable>)
            connectors.add(nothingOption)

            return connectors.associateBy { c -> c.getUniqueId() }
        }

        private fun isPackageInstalled(
            packageName: String,
            packageManager: PackageManager
        ): Boolean {
            return try {
                packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
    }
}