package net.mfuertes.nfchatags

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val TAG = "Tags"
    private val haPackages: List<String> = listOf(
        "io.homeassistant.companion.android",
        "io.homeassistant.companion.android.debug",
        "io.homeassistant.companion.android.minimal",
        "io.homeassistant.companion.android.minimal.debug"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val packageName = findViewById<TextView>(R.id.ha_detected)
        getInstalledAppPackage().also { item -> if (item != null) packageName.text = item }
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