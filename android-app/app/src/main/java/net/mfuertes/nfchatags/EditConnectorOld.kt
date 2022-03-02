package net.mfuertes.nfchatags

import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import net.mfuertes.nfchatags.connectors.ApiConnectorDbHelper
import net.mfuertes.nfchatags.connectors.ApiConnector

class EditConnectorOld : AppCompatActivity() {
    companion object {
        const val CONNECTOR_TO_EDIT = "CONNECTOR_TO_EDIT"
    }

    var connector: ApiConnector? = null
    lateinit var ip: EditText
    lateinit var port: EditText
    lateinit var pat: EditText
    lateinit var name: EditText
    lateinit var saveButton: Button
    lateinit var removeButton: Button
    val dbHelper = ApiConnectorDbHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_connector)
        //EditTexts
        ip = findViewById(R.id.ip_input)
        port = findViewById(R.id.port_input)
        pat = findViewById(R.id.pat_input)
        name = findViewById(R.id.name_input)

        saveButton = findViewById(R.id.edit_connector_save)
        removeButton = findViewById(R.id.edit_connector_remove)

        val connectorId = intent.getIntExtra(CONNECTOR_TO_EDIT,-1)
        if (connectorId != -1) {
            connector = dbHelper.getConnectorById(connectorId)
            removeButton.apply { isEnabled = true}
            //removeButton.backgroundTintList =
        }
        // add back arrow to toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        title = connector?.name ?: "New"
        if (connector != null) {
            ip.text = connector!!.ip.toEditable()
            pat.text = connector!!.pat.toEditable()
            port.text = connector!!.port.toString().toEditable()
            name.text = connector!!.name.toEditable()
        }

        saveButton.setOnClickListener {
            if (
                name.text.isNotBlank() &&
                ip.text.isNotBlank() &&
                port.text.isNotBlank() &&
                pat.text.isNotBlank()
            ) {
                val nconnector = ApiConnector(
                    ip = ip.text.toString(),
                    name = name.text.toString(),
                    port = port.text.toString().toInt(),
                    pat = pat.text.toString(),
                    id = if (connector != null) connector!!.id else null
                )
                if (dbHelper.upsertConnector(nconnector) > 0)
                    finish()
            }
        }

        removeButton.setOnClickListener {
            if (connector != null){
                if (dbHelper.deleteConnector(connector!!.id!!) > 0)
                    finish()
            }
        }

    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}