package net.mfuertes.nfchatags

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import net.mfuertes.nfchatags.SharedPreference.Companion.SELECTED_METHOD
import net.mfuertes.nfchatags.SharedPreference.Companion.SELECTED_METHOD_TYPE
import net.mfuertes.nfchatags.adapters.HomeAssitantConnectorsAdapter
import net.mfuertes.nfchatags.connectors.*


class MainActivity : AppCompatActivity() {
    private val TAG = "Tags"
    private val haPackages: List<String> = listOf(
        "io.homeassistant.companion.android",
        "io.homeassistant.companion.android.debug",
        "io.homeassistant.companion.android.minimal",
        "io.homeassistant.companion.android.minimal.debug"
    )
    private val methods : List<String> = listOf("API", "Intent")
    private val connectors: ArrayList<Connectable> = ArrayList<Connectable>()
    private lateinit var adapter: HomeAssitantConnectorsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var sharedPreferences = SharedPreference(this)
        var nothingOption = NothingConnector(sharedPreferences.getValueString(SharedPreference.LAST_TAG_ID));

        haPackages.forEach{
            if(isPackageInstalled(it, packageManager))
                connectors.add(
                    HomeAssitantIntentConnector(
                        packageName=it,
                        name = "Home Assitant App"
                    )
                )
        }
        connectors.add(HomeAssitantApiConnector.connector)
        connectors.add(nothingOption)

        val list = findViewById<RecyclerView>(R.id.list_conectors)
        var current = Connectable.getSavedConnector(sharedPreferences)
        adapter = HomeAssitantConnectorsAdapter(this, connectors, current, View.OnClickListener{
            val item: Connectable = connectors.get(list.getChildLayoutPosition(it))
            Connectable.saveConnector(sharedPreferences, item)
            adapter.current = item
            adapter.notifyDataSetChanged()
            findViewById<Button>(R.id.edit_connector).apply { isEnabled = item.isEditable() }
        })

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}