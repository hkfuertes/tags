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
import com.google.gson.Gson
import net.mfuertes.nfchatags.connectors.HomeAssitantConnector


class ReceiverActivity : AppCompatActivity() {
    private lateinit var _scannedTagId: TextView
    private lateinit var _finishButton: Button

    private val haPackages: List<String> = listOf(
        "io.homeassistant.companion.android",
        "io.homeassistant.companion.android.debug",
        "io.homeassistant.companion.android.minimal",
        "io.homeassistant.companion.android.minimal.debug")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
        setTitle("Tag scanned")
        _scannedTagId = findViewById(R.id.scanned_tag_id)
        _finishButton = findViewById(R.id.finish_button)
        _finishButton.setOnClickListener { finish() }
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
                _scannedTagId.text = tag.id.toHex().uppercase()
                var sharedPreferences = SharedPreference(this)
                sharedPreferences.save(SharedPreference.LAST_TAG_ID, tag.id.toHex())
                Gson().fromJson(
                    sharedPreferences.getValueString(SharedPreference.SELECTED_METHOD),
                    HomeAssitantConnector::class.java
                )?.sendTag(this, tag.id.toHex(), onFinish = { finish() })
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

    private fun getInstalledAppPackage(): String?{
        haPackages.forEach{
            if(isPackageInstalled(it, packageManager))
                return it;
        }
        return null
    }

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}