package net.mfuertes.nfchatags

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.mfuertes.nfchatags.adapters.HomeAssitantConnectorsAdapter
import net.mfuertes.nfchatags.connectors.*


class MainActivity : AppCompatActivity() {
    private var editButton: Button? = null
    private val TAG = "Tags"
    private val haPackages: List<String> = listOf(
        "io.homeassistant.companion.android",
        "io.homeassistant.companion.android.debug",
        "io.homeassistant.companion.android.minimal",
        "io.homeassistant.companion.android.minimal.debug"
    )
    private val connectors: ArrayList<Connectable> = ArrayList<Connectable>()
    private lateinit var adapter: HomeAssitantConnectorsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //UI elements
        editButton = findViewById<Button>(R.id.edit_connector)

        var sharedPreferences = SharedPreference(this)
        var nothingOption = NothingConnector();

        haPackages.forEach{
            if(isPackageInstalled(it, packageManager))
                connectors.add(
                    HomeAssistantIntentConnector(
                        packageName=it
                    )
                )
        }
        connectors.add(HomeAssistantApiConnector.connector)
        connectors.add(nothingOption)

        val list = findViewById<RecyclerView>(R.id.list_conectors)
        var current = Connectable.getSavedConnector(sharedPreferences)
        editButtonHandler(editButton,current)
        adapter = HomeAssitantConnectorsAdapter(this, connectors, current, View.OnClickListener{
            val item: Connectable = connectors.get(list.getChildLayoutPosition(it))
            Connectable.saveConnector(sharedPreferences, item)
            adapter.current = item
            adapter.notifyDataSetChanged()
            editButtonHandler(editButton,item)
        })

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        findViewById<Button>(R.id.create_connector).setOnClickListener {
            val intent = Intent(this, EditConnector::class.java)
            startActivity(intent)
        }
    }

    private fun editButtonHandler(button: Button?,connector: Connectable?){
        if (button!=null && connector != null){
            button.apply {
                isEnabled = connector.isEditable()
            }
            button.setOnClickListener {
                //Toast.makeText(this,connector.getDisplayName()+" | "+connector.getId(), Toast.LENGTH_LONG).show()
                val intent = Intent(this, EditConnector::class.java)
                intent.putExtra(EditConnector.CONNECTOR_TO_EDIT, HomeAssistantApiConnector.toData(connector as HomeAssistantApiConnector))
                startActivity(intent)
            }
        }
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