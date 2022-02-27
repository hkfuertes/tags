package net.mfuertes.nfchatags

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import net.mfuertes.nfchatags.adapters.HomeAssitantConnectorsAdapter
import net.mfuertes.nfchatags.connectors.HomeAssitantConnector
import net.mfuertes.nfchatags.connectors.NothingConnector


class MainActivity : AppCompatActivity() {
    private val TAG = "Tags"
    private val haPackages: List<String> = listOf(
        "io.homeassistant.companion.android",
        "io.homeassistant.companion.android.debug",
        "io.homeassistant.companion.android.minimal",
        "io.homeassistant.companion.android.minimal.debug"
    )
    private val methods : List<String> = listOf("API", "Intent")
    private val connectors: ArrayList<HomeAssitantConnector> = ArrayList<HomeAssitantConnector>()
    private lateinit var adapter: HomeAssitantConnectorsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var sharedPreferences = SharedPreference(this)

        val installedPackage = getInstalledAppPackage();
        if (installedPackage != null){
            connectors.add(
                HomeAssitantConnector(
                packageName=installedPackage,
                name = "Home Assitant App"
            )
            )
        }
        connectors.add(HomeAssitantConnector.connector)
        connectors.add(NothingConnector().also { item ->
            val tagid = sharedPreferences.getValueString(SharedPreference.LAST_TAG_ID)
            item.description = tagid?.let { item.description.toString().replace("%tag_id%", it) };
        })
        val list = findViewById<RecyclerView>(R.id.list_conectors)
        var current = Gson().fromJson(
            sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD),
            HomeAssitantConnector::class.java
        )
        adapter = HomeAssitantConnectorsAdapter(this, connectors, current, View.OnClickListener{
            val itemPosition: Int = list.getChildLayoutPosition(it)
            val item: HomeAssitantConnector = connectors.get(itemPosition)
            sharedPreferences.save(SharedPreference.SELECTED_METHOD, Gson().toJson(item))
            adapter.current = item
            //list.adapter = adapter
            adapter.notifyDataSetChanged()
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

    private fun getInstalledAppPackage(): String?{
        haPackages.forEach{
            if(isPackageInstalled(it, packageManager))
                return it;
        }
        return null
    }

}