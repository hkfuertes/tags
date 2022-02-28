package net.mfuertes.nfchatags

import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.mfuertes.nfchatags.connectors.HomeAssistantApiConnector

class EditConnector : AppCompatActivity() {
    companion object {
        const val CONNECTOR_TO_EDIT = "CONNECTOR_TO_EDIT"
    }

    var connector: HomeAssistantApiConnector? = null
    lateinit var ip: EditText
    lateinit var port: EditText
    lateinit var pat: EditText
    lateinit var name:EditText
    lateinit var nameCopy: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_connector)
        //EditTexts
        ip = findViewById(R.id.ip_input)
        port = findViewById(R.id.port_input)
        pat = findViewById(R.id.pat_input)

        name = findViewById(R.id.name_input)
        nameCopy = findViewById(R.id.name_copy)

        val connectorData = intent.getStringExtra(CONNECTOR_TO_EDIT)
        if (connectorData != null) {
            connector = HomeAssistantApiConnector.fromData(connectorData)
        }
        // add back arrow to toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        title = connector?.name ?: "New"
        if (connector != null){
            ip.text = connector!!.ip.toEditable()
            pat.text = connector!!.pat.toEditable()
            port.text = connector!!.port.toString().toEditable()
        }else{
            name.visibility = View.VISIBLE
            nameCopy.visibility = View.VISIBLE
        }

    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}