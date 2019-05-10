package net.geekstools.offsetassistant.Util.Functions

import android.app.Activity
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.ACCESSIBILITY_SERVICE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import net.geekstools.offsetassistant.OffsetAssistant
import net.geekstools.offsetassistant.R
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileReader
import java.nio.charset.Charset
import java.util.*

class FunctionsClass {

    var API: Int = 0

    lateinit var activity: Activity
    var context: Context

    /*GUI Functions*/
    val layoutParamsPointer: WindowManager.LayoutParams
        get() {
            val Width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19f, context.resources.displayMetrics).toInt()
            val Height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19f, context.resources.displayMetrics).toInt()

            val layoutParamsPointer = WindowManager.LayoutParams()
            layoutParamsPointer.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            layoutParamsPointer.format = PixelFormat.TRANSLUCENT
            layoutParamsPointer.flags = layoutParamsPointer.flags or (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            layoutParamsPointer.width = Width
            layoutParamsPointer.height = Height
            layoutParamsPointer.gravity = Gravity.TOP or Gravity.START
            layoutParamsPointer.x = displayX() / 2 - Width / 2
            layoutParamsPointer.y = displayY() / 2 - Height / 2
            layoutParamsPointer.windowAnimations = android.R.style.Animation_Dialog

            return layoutParamsPointer
        }

    val layoutParamsController: WindowManager.LayoutParams
        get() {
            val Width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47f, context.resources.displayMetrics).toInt()
            val Height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47f, context.resources.displayMetrics).toInt()

            val layoutParamsController = WindowManager.LayoutParams()
            layoutParamsController.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            layoutParamsController.format = PixelFormat.TRANSLUCENT
            layoutParamsController.flags = layoutParamsController.flags or (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            layoutParamsController.width = Width
            layoutParamsController.height = Height
            layoutParamsController.gravity = Gravity.TOP or Gravity.START
            layoutParamsController.x = displayX() - DpToInteger(99)
            layoutParamsController.y = displayY() - DpToInteger(157)
            layoutParamsController.windowAnimations = android.R.style.Animation_Dialog

            return layoutParamsController
        }

    val statusBar: Int
        get() = readPreference(".SystemBars", "StatusBar", 0)

    val navigationBar: Int
        get() = readPreference(".SystemBars", "NavigationBar", 0)

    fun deviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            CapitalizeFirstChar(model)
        } else {
            CapitalizeFirstChar(manufacturer) + " " + model
        }
    }

    fun countryIso(): String {
        var countryISO = "Undefined"
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            countryISO = telephonyManager.simCountryIso
            if (countryISO.length < 2) {
                countryISO = "Undefined"
            }
        } catch (e: Exception) {

            countryISO = "Undefined"
        }

        return countryISO
    }

    constructor(context: Context) {
        this.context = context
        API = Build.VERSION.SDK_INT

        loadSavedColor()
    }

    constructor(context: Context, activity: Activity) {
        this.context = context
        this.activity = activity
        API = Build.VERSION.SDK_INT

        loadSavedColor()
    }

    /*Interaction Observer*/
    fun sendInteractionObserverEvent(view: View, packageName: String, className: String, accessibilityEvent: Int, accessibilityAction: Int) {
        if (AccessibilityServiceEnabled() && SettingServiceRunning(OffsetAssistant::class.java)) {
            val accessibilityManager = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
            val event = AccessibilityEvent.obtain()
            event.setSource(view)
            event.eventType = accessibilityEvent
            event.action = accessibilityAction
            event.packageName = packageName
            event.className = className
            event.text.add(context.packageName)
            accessibilityManager.sendAccessibilityEvent(event)
        }
    }

    /*Color GUI Functions*/
    fun loadSavedColor(/*Light-Dark*/) {
        val sharedPreferences = context.getSharedPreferences(".ThemeColors", Context.MODE_PRIVATE)
        val primeColor = PreferenceManager.getDefaultSharedPreferences(context)
        if (primeColor.getString("LightDark", "3") == "1") {//Light
            PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
            PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))
            PublicVariable.colorLightDark = context.resources.getColor(R.color.light)
            PublicVariable.colorLightDarkOpposite = context.resources.getColor(R.color.dark)
        } else if (primeColor.getString("LightDark", "3") == "2") {//Dark
            PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
            PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))
            PublicVariable.colorLightDark = context.resources.getColor(R.color.dark)
            PublicVariable.colorLightDarkOpposite = context.resources.getColor(R.color.light)
        } else if (primeColor.getString("LightDark", "3") == "3") {//Dynamic
            if (colorLightDarkWallpaper()) {
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))
                PublicVariable.colorLightDark = context.resources.getColor(R.color.light)
                PublicVariable.colorLightDarkOpposite = context.resources.getColor(R.color.dark)
            } else if (!colorLightDarkWallpaper()) {
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))
                PublicVariable.colorLightDark = context.resources.getColor(R.color.dark)
                PublicVariable.colorLightDarkOpposite = context.resources.getColor(R.color.light)
            }
        } else if (primeColor.getString("LightDark", "3") == "4") {//Automatic
            val timeHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (timeHours >= 18 || timeHours < 6) {//Night
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))
                PublicVariable.colorLightDark = context.resources.getColor(R.color.dark)
                PublicVariable.colorLightDarkOpposite = context.resources.getColor(R.color.light)
            } else {
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))
                PublicVariable.colorLightDark = context.resources.getColor(R.color.light)
                PublicVariable.colorLightDarkOpposite = context.resources.getColor(R.color.dark)
            }
        }
    }

    /*Checkpoint GUI Functions*/
    fun displayX(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun displayY(): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun colorLightDarkWallpaper(): Boolean {
        var LightDark = false
        try {
            val sharedPreferences = context.getSharedPreferences(".ThemeColors", Context.MODE_PRIVATE)

            val vibrantColor = sharedPreferences.getInt("primaryColorLight", context.resources.getColor(R.color.default_color))
            val darkMutedColor = sharedPreferences.getInt("primaryColorDark", context.resources.getColor(R.color.default_color))
            val dominantColor = sharedPreferences.getInt("dominantColor", context.resources.getColor(R.color.default_color))

            val initMix = mixColors(vibrantColor, darkMutedColor, 0.50f)
            val finalMix = mixColors(dominantColor, initMix, 0.50f)

            val calculateLuminance = ColorUtils.calculateLuminance(dominantColor)
            if (calculateLuminance > 0.50) {//light
                LightDark = true
            } else if (calculateLuminance <= 0.50) {//dark
                LightDark = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return LightDark
    }

    fun extractWallpaperColor() {
        var primaryColorLight: Int
        var primaryColorDark: Int
        var dominantColor: Int
        val currentColor: Palette
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val currentWallpaper = wallpaperManager.drawable
            val bitmap = (currentWallpaper as BitmapDrawable).bitmap

            if (bitmap != null && !bitmap.isRecycled) {
                currentColor = Palette.from(bitmap).generate()
            } else {
                val bitmapTemp = BitmapFactory.decodeResource(context.resources, R.drawable.brilliant)
                currentColor = Palette.from(bitmapTemp).generate()
            }

            val defaultColor = context.resources.getColor(R.color.default_color)

            primaryColorLight = currentColor.getVibrantColor(defaultColor)
            primaryColorDark = currentColor.getDarkMutedColor(defaultColor)
            dominantColor = currentColor.getDominantColor(defaultColor)
        } catch (e: Exception) {
            e.printStackTrace()

            primaryColorLight = context.resources.getColor(R.color.default_color)
            primaryColorDark = context.resources.getColor(R.color.default_color)
            dominantColor = context.resources.getColor(R.color.default_color)
        }

        val sharedPreferences = context.getSharedPreferences(".ThemeColors", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("primaryColorLight", primaryColorLight)
        editor.putInt("primaryColorDark", primaryColorDark)
        editor.putInt("dominantColor", dominantColor)
        editor.apply()
    }

    fun extractVibrantColor(drawable: Drawable): Int {
        var VibrantColor = context.resources.getColor(R.color.default_color)
        var bitmap: Bitmap? = null
        if (returnAPI() >= 26) {
            if (drawable is VectorDrawable) {
                bitmap = drawableToBitmap(drawable)
            } else if (drawable is AdaptiveIconDrawable) {
                try {
                    bitmap = (drawable.background as BitmapDrawable).bitmap
                } catch (e: Exception) {
                    try {
                        bitmap = (drawable.foreground as BitmapDrawable).bitmap
                    } catch (e1: Exception) {
                        bitmap = drawableToBitmap(drawable)
                    }

                }

            } else {
                bitmap = drawableToBitmap(drawable)
            }
        } else {
            bitmap = drawableToBitmap(drawable)
        }
        var currentColor: Palette
        try {
            if (bitmap != null && !bitmap.isRecycled) {
                currentColor = Palette.from(bitmap).generate()

                val defaultColor = context.resources.getColor(R.color.default_color)
                VibrantColor = currentColor.getVibrantColor(defaultColor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                if (bitmap != null && !bitmap.isRecycled) {
                    currentColor = Palette.from(bitmap).generate()

                    val defaultColor = context.resources.getColor(R.color.default_color)
                    VibrantColor = currentColor.getMutedColor(defaultColor)
                }
            } catch (e1: Exception) {

            }

        }

        return VibrantColor
    }

    fun extractDominantColor(drawable: Drawable): Int {
        var VibrantColor = context.resources.getColor(R.color.default_color)
        var bitmap: Bitmap? = null
        if (returnAPI() >= 26) {
            if (drawable is VectorDrawable) {
                bitmap = drawableToBitmap(drawable)
            } else if (drawable is AdaptiveIconDrawable) {
                try {
                    bitmap = (drawable.background as BitmapDrawable).bitmap
                } catch (e: Exception) {
                    try {
                        bitmap = (drawable.foreground as BitmapDrawable).bitmap
                    } catch (e1: Exception) {
                        bitmap = drawableToBitmap(drawable)
                    }

                }

            } else {
                bitmap = drawableToBitmap(drawable)
            }
        } else {
            bitmap = drawableToBitmap(drawable)
        }
        var currentColor: Palette
        try {
            if (bitmap != null && !bitmap.isRecycled) {
                currentColor = Palette.from(bitmap).generate()

                val defaultColor = context.resources.getColor(R.color.default_color)
                VibrantColor = currentColor.getDominantColor(defaultColor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                if (bitmap != null && !bitmap.isRecycled) {
                    currentColor = Palette.from(bitmap).generate()

                    val defaultColor = context.resources.getColor(R.color.default_color)
                    VibrantColor = currentColor.getMutedColor(defaultColor)
                }
            } catch (e1: Exception) {

            }

        }

        return VibrantColor
    }

    fun brightenBitmap(bitmap: Bitmap): Bitmap {
        val canvas = Canvas(bitmap)
        val paint = Paint(Color.RED)
        val filter = LightingColorFilter(-0x1, 0x00222222) // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, Matrix(), paint)
        return bitmap
    }

    fun darkenBitmap(bitmap: Bitmap): Bitmap {
        val canvas = Canvas(bitmap)
        val paint = Paint(Color.RED)
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        val filter = LightingColorFilter(-0x808081, 0x00000000)    // darken
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, Matrix(), paint)
        return bitmap
    }

    fun manipulateColor(color: Int, aFactor: Float): Int {
        val a = Color.alpha(color)
        val r = Math.round(Color.red(color) * aFactor)
        val g = Math.round(Color.green(color) * aFactor)
        val b = Math.round(Color.blue(color) * aFactor)
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255))
    }

    fun mixColors(color1: Int, color2: Int, ratio: Float /*0 -- 1*/): Int {
        /*ratio = 1 >> color1*/
        /*ratio = 0 >> color2*/
        val inverseRation = 1f - ratio
        val r = Color.red(color1) * ratio + Color.red(color2) * inverseRation
        val g = Color.green(color1) * ratio + Color.green(color2) * inverseRation
        val b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRation
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }

    fun setColorAlpha(color: Int, alphaValue: Float /*1 -- 255*/): Int {
        val alpha = Math.round(Color.alpha(color) * alphaValue)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    /*Checkpoint Functions*/
    fun AccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = ComponentName(context, OffsetAssistant::class.java)

        val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)

            if (enabledService != null && enabledService == expectedComponentName)
                return true
        }

        return false
    }

    fun SettingServiceRunning(aClass: Class<*>): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (aClass.name == service.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /*Apps Checkpoint Functions*/
    fun appName(packageName: String): String? {
        var Name: String? = null
        try {
            val app = context.packageManager.getApplicationInfo(packageName, 0)
            Name = context.packageManager.getApplicationLabel(app).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Name
    }

    fun activityLabel(activityInfo: ActivityInfo): String? {
        var Name: String? = context.getString(R.string.app_name)
        try {
            Name = activityInfo.loadLabel(context.packageManager).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Name = appName(activityInfo.packageName)
        }

        return Name
    }

    fun appVersionName(packageName: String): String {
        var Version = "0"

        try {
            val packInfo = context.packageManager.getPackageInfo(packageName, 0)
            Version = packInfo.versionName
        } catch (e: Exception) {
        }

        return Version
    }

    fun appVersionCode(packageName: String): Int {
        var VersionCode = 0

        try {
            val packInfo = context.packageManager.getPackageInfo(packageName, 0)
            VersionCode = packInfo.versionCode
        } catch (e: Exception) {
        }

        return VersionCode
    }

    fun appInstalledOrNot(packName: String): Boolean {
        val pm = context.packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(packName, 0)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        } catch (e: Exception) {

            app_installed = false
        }

        return app_installed
    }

    fun ifSystem(packageName: String): Boolean {
        var ifSystem = false
        val packageManager = context.packageManager
        try {
            val targetPkgInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES)
            val sys = packageManager.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES)
            ifSystem = targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0] == targetPkgInfo.signatures[0]
        } catch (e: PackageManager.NameNotFoundException) {

            ifSystem = false
        } catch (e: Exception) {
        }

        return ifSystem
    }

    fun ifDefaultLauncher(packageName: String): Boolean {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val defaultLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val defaultLauncherStr = defaultLauncher.activityInfo.packageName
        return if (defaultLauncherStr == packageName) {
            true
        } else false
    }

    fun canLaunch(packageName: String): Boolean {
        return context.packageManager.getLaunchIntentForPackage(packageName) != null
    }

    fun returnAPI(): Int {
        return API
    }

    private fun CapitalizeFirstChar(text: String?): String {
        if (text == null || text.length == 0) {
            return ""
        }
        val first = text[0]
        return if (Character.isUpperCase(first)) {
            text
        } else {
            Character.toUpperCase(first) + text.substring(1)
        }
    }

    /*Converters GUI Functions*/
    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is VectorDrawable) {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        } else if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                bitmap = drawable.bitmap
            }
        } else if (drawable is LayerDrawable) {

            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }

    fun bitmapToDrawable(bitmap: Bitmap): Drawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    fun drawableToByte(drawable: Drawable): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        drawableToBitmap(drawable)!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun bitmapToByte(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun byteToDrawable(byteImage: ByteArray): Drawable {
        val bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)
        return bitmapToDrawable(bitmap)
    }

    fun DpToPixel(dp: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun PixelToDp(px: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun DpToInteger(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    /*File Functions*/
    fun saveFileEmpty(fileName: String) {
        try {
            val fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE)

            fOut.flush()
            fOut.close()
        } catch (e: Exception) {

        }

    }

    fun saveFile(fileName: String, content: String) {
        try {
            val fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fOut.write(content.toByteArray())

            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
        }

    }

    fun saveFileAppendLine(fileName: String, content: String) {
        try {
            val fOut = context.openFileOutput(fileName, Context.MODE_APPEND)
            fOut.write((content + "\n").toByteArray())

            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
        }

    }

    fun readFileLine(fileName: String): List<String> {

        return context.getFileStreamPath(fileName).readLines()
    }

    fun readFile(fileName: String): String {

        return context.getFileStreamPath(fileName).readText(Charset.defaultCharset())
    }

    fun countLine(fileName: String): Int {
        var nLines = 0
        try {
            val reader = BufferedReader(FileReader(context.getFileStreamPath(fileName)))
            while (reader.readLine() != null) {
                nLines++
            }
            reader.close()
        } catch (e: Exception) {
        }

        return nLines
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: String) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putString(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Int) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun savePreference(PreferenceName: String, KEY: String, VALUE: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String, VALUE: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putInt(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String, VALUE: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putString(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun saveDefaultPreference(KEY: String, VALUE: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editorSharedPreferences = sharedPreferences.edit()
        editorSharedPreferences.putBoolean(KEY, VALUE)
        editorSharedPreferences.apply()
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: String): String? {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Int): Int {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE)
    }

    fun readPreference(PreferenceName: String, KEY: String, defaultVALUE: Boolean): Boolean {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, defaultVALUE)
    }

    fun readDefaultPreference(KEY: String, defaultVALUE: Boolean): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, defaultVALUE)
    }
}
