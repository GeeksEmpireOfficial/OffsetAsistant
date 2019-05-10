package net.geekstools.offsetassistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.RelativeLayout
import android.widget.TextView
import net.geekstools.offsetassistant.Util.Functions.FunctionsClass
import java.util.*

class Configurations : Activity() {

    lateinit var functionsClass: FunctionsClass

    lateinit var configurationView: RelativeLayout

    lateinit var permissionsInfo: TextView

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            666 -> {
                if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant::class.java)) {
                    val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration_view)
        functionsClass = FunctionsClass(applicationContext, this@Configurations)

        configurationView = findViewById<View>(R.id.configurationView) as RelativeLayout
        permissionsInfo = findViewById<View>(R.id.permissionsInfo) as TextView

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (functionsClass.returnAPI() > 25) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        window.statusBarColor = getColor(R.color.light)
        window.navigationBarColor = getColor(R.color.light)

        val actionBar = actionBar
        actionBar!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.light)))
        actionBar.title = Html.fromHtml("<font color='" + resources.getColor(R.color.default_color) + "'>" + getString(R.string.app_name) + "</font>")
        actionBar.subtitle = Html.fromHtml("<small><font color='" + resources.getColor(R.color.default_color_light) + "'>" + functionsClass.appVersionName(packageName) + "</font></small>")

        functionsClass.savePreference(".SystemBars", "StatusBar",
                resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")))
        functionsClass.savePreference(".SystemBars", "NavigationBar",
                resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android")))

        val Permissions = ArrayList<String>()
        Permissions.add(Manifest.permission.INTERNET)
        Permissions.add(Manifest.permission.WAKE_LOCK)
        Permissions.add(Manifest.permission.VIBRATE)
        Permissions.add(Manifest.permission.CHANGE_WIFI_STATE)
        Permissions.add(Manifest.permission.ACCESS_WIFI_STATE)
        Permissions.add(Manifest.permission.ACCESS_NETWORK_STATE)
        Permissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED)
        Permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        Permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant::class.java)) {
            functionsClass.savePreference("OffsetAssistant", "isRunning", false)

            permissionsInfo.text = "I Need Permissions in Accessibility Services to Click On Your Behalf.\nPlease, Click Here to Continue..."
        } else {
            permissionsInfo.text = "Use Blue Circle (Controller) to Move the Red Circle (Pointer) then Click Or Long Click on Blue Circle (Controller) to Trigger Action on Wherever the Red Circle (Pointer) is.\nPlease, Click Here to Continue..."
        }

        configurationView.setOnClickListener {
            if (functionsClass.readPreference("OffsetAssistant", "isRunning", false)) {

            } else if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant::class.java)) {
                requestPermissions(Permissions.toTypedArray(), 666)
            } else if (functionsClass.AccessibilityServiceEnabled() && functionsClass.SettingServiceRunning(OffsetAssistant::class.java) && Settings.canDrawOverlays(applicationContext)) {
                functionsClass.sendInteractionObserverEvent(configurationView,
                        packageName,
                        Configurations::class.java.simpleName,
                        AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                        10296)

            }
        }
    }

    public override fun onResume() {
        super.onResume()

        if (!Settings.canDrawOverlays(applicationContext)) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant::class.java)) {
            permissionsInfo.text = "I Need Permissions in Accessibility Services to Click On Your Behalf.\nPlease, Click Here to Continue..."
        } else {
            permissionsInfo.text = "Use Blue Circle (Controller) to Move the Red Circle (Pointer) then Click Or Long Click on Blue Circle (Controller) to Trigger Action on Wherever the Red Circle (Pointer) is.\nPlease, Click Here to Continue..."
        }
    }
}
