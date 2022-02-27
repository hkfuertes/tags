package net.mfuertes.nfchatags

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcelable
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var _tagIdText: TextView
    private var nfcAdapter: NfcAdapter? = null
    var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _tagIdText = findViewById<TextView>(R.id.tagid)

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)?.let { it }
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            resolveIntent(intent)
        };
    }

    override fun onResume() {
        super.onResume()
        assert(nfcAdapter != null)
        nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)

        //We recover the field from the intent
        val id = this.intent.getByteArrayExtra("tag_id");
        if (id != null) {
            _tagIdText.text = id.toHex()
        }

    }

    fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }


    override fun onPause() {
        super.onPause()
        //Onpause stop listening
        if (nfcAdapter != null) {
            nfcAdapter!!.disableForegroundDispatch(this)
        }
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val tag = (intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?)!!
            intent.putExtra("tag_id", tag.id)
        }
    }
}