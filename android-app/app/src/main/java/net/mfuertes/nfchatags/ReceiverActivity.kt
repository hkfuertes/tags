package net.mfuertes.nfchatags

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.mfuertes.nfchatags.connectors.ApiConnectorDbHelper
import net.mfuertes.nfchatags.connectors.Connectable


class ReceiverActivity : AppCompatActivity() {
    private lateinit var _scannedTagId: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
        title = "Tag scanned"
        _scannedTagId = findViewById(R.id.scanned_tag_id)
        findViewById<Button>(R.id.finish_button).setOnClickListener { finish() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    override fun onResume() {
        super.onResume()
        if (intent != null) {
            val action = intent.action
            if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
                val tag = (intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?)!!
                _scannedTagId.text = tag.id.toHex()
                var sharedPreferences = SharedPreference(this)
                Connectable.getSavedConnector(sharedPreferences, dbHelper = ApiConnectorDbHelper(this))
                    ?.sendTag(this, tag.id.toHex(), onFinish = { finish() })
            }
        }
    }

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}