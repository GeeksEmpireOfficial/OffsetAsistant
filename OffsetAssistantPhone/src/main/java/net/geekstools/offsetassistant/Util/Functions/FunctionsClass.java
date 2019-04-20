package net.geekstools.offsetassistant.Util.Functions;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import net.geekstools.offsetassistant.OffsetAssistant;
import net.geekstools.offsetassistant.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import static android.content.Context.ACCESSIBILITY_SERVICE;

public class FunctionsClass {

    int API;
    Activity activity;
    Context context;

    public FunctionsClass(Context context) {
        this.context = context;
        API = Build.VERSION.SDK_INT;

        loadSavedColor();
    }

    public FunctionsClass(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        API = Build.VERSION.SDK_INT;

        loadSavedColor();
    }

    /*Interaction Observer*/
    public void sendInteractionObserverEvent(View view, String packageName, String className, int accessibilityEvent, int accessibilityAction) {
        if (AccessibilityServiceEnabled() && SettingServiceRunning(OffsetAssistant.class)) {
            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setSource(view);
            event.setEventType(accessibilityEvent);
            event.setAction(accessibilityAction);
            event.setPackageName(packageName);
            event.setClassName(className);
            event.getText().add(context.getPackageName());
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

    /*GUI Functions*/
    public WindowManager.LayoutParams getLayoutParamsPointer() {
        final int Width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19, context.getResources().getDisplayMetrics());
        final int Height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19, context.getResources().getDisplayMetrics());

        WindowManager.LayoutParams layoutParamsPointer = new WindowManager.LayoutParams();
        layoutParamsPointer.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParamsPointer.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParamsPointer.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }*/
        layoutParamsPointer.format = PixelFormat.TRANSLUCENT;
        layoutParamsPointer.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParamsPointer.width = Width;
        layoutParamsPointer.height = Height;
        layoutParamsPointer.gravity = Gravity.TOP | Gravity.START;
        layoutParamsPointer.x = displayX()/2 - (Width/2);
        layoutParamsPointer.y = displayY()/2 - (Height/2);
        layoutParamsPointer.windowAnimations = android.R.style.Animation_Dialog;

        return layoutParamsPointer;
    }

    public WindowManager.LayoutParams getLayoutParamsController() {
        final int Width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, context.getResources().getDisplayMetrics());
        final int Height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, context.getResources().getDisplayMetrics());

        WindowManager.LayoutParams layoutParamsController = new WindowManager.LayoutParams();
        layoutParamsController.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParamsController.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParamsController.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }*/
        layoutParamsController.format = PixelFormat.TRANSLUCENT;
        layoutParamsController.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParamsController.width = Width;
        layoutParamsController.height = Height;
        layoutParamsController.gravity = Gravity.TOP | Gravity.START;
        layoutParamsController.x = displayX() - DpToInteger(99);
        layoutParamsController.y = displayY() - DpToInteger(157);
        layoutParamsController.windowAnimations = android.R.style.Animation_Dialog;

        return layoutParamsController;
    }

    /*Color GUI Functions*/
    public void loadSavedColor(/*Light-Dark*/) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(".ThemeColors", Context.MODE_PRIVATE);
        SharedPreferences primeColor = PreferenceManager.getDefaultSharedPreferences(context);
        if (primeColor.getString("LightDark", "3").equals("1")) {//Light
            PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
            PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));
            PublicVariable.colorLightDark = context.getResources().getColor(R.color.light);
            PublicVariable.colorLightDarkOpposite = context.getResources().getColor(R.color.dark);
        } else if (primeColor.getString("LightDark", "3").equals("2")) {//Dark
            PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
            PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
            PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));
            PublicVariable.colorLightDark = context.getResources().getColor(R.color.dark);
            PublicVariable.colorLightDarkOpposite = context.getResources().getColor(R.color.light);
        } else if (primeColor.getString("LightDark", "3").equals("3")) {//Dynamic
            if (colorLightDarkWallpaper()) {
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));
                PublicVariable.colorLightDark = context.getResources().getColor(R.color.light);
                PublicVariable.colorLightDarkOpposite = context.getResources().getColor(R.color.dark);
            } else if (!colorLightDarkWallpaper()) {
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));
                PublicVariable.colorLightDark = context.getResources().getColor(R.color.dark);
                PublicVariable.colorLightDarkOpposite = context.getResources().getColor(R.color.light);
            }
        } else if (primeColor.getString("LightDark", "3").equals("4")) {//Automatic
            int timeHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (timeHours >= 18 || timeHours < 6) {//Night
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));
                PublicVariable.colorLightDark = context.getResources().getColor(R.color.dark);
                PublicVariable.colorLightDarkOpposite = context.getResources().getColor(R.color.light);
            } else {
                PublicVariable.primaryColor = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
                PublicVariable.primaryColorOpposite = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
                PublicVariable.dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));
                PublicVariable.colorLightDark = context.getResources().getColor(R.color.light);
                PublicVariable.colorLightDarkOpposite = context.getResources().getColor(R.color.dark);
            }
        }
    }

    /*Checkpoint GUI Functions*/
    public int displayX() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public int displayY() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public boolean colorLightDarkWallpaper() {
        boolean LightDark = false;
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(".ThemeColors", Context.MODE_PRIVATE);

            int vibrantColor = sharedPreferences.getInt("primaryColorLight", context.getResources().getColor(R.color.default_color));
            int darkMutedColor = sharedPreferences.getInt("primaryColorDark", context.getResources().getColor(R.color.default_color));
            int dominantColor = sharedPreferences.getInt("dominantColor", context.getResources().getColor(R.color.default_color));

            int initMix = mixColors(vibrantColor, darkMutedColor, 0.50f);
            int finalMix = mixColors(dominantColor, initMix, 0.50f);

            double calculateLuminance = ColorUtils.calculateLuminance(dominantColor);
            if (calculateLuminance > 0.50) {//light
                LightDark = true;
            } else if (calculateLuminance <= 0.50) {//dark
                LightDark = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LightDark;
    }

    public void extractWallpaperColor() {
        int primaryColorLight, primaryColorDark, dominantColor;
        Palette currentColor;
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            final Drawable currentWallpaper = wallpaperManager.getDrawable();
            final Bitmap bitmap = ((BitmapDrawable) currentWallpaper).getBitmap();

            if (bitmap != null && !bitmap.isRecycled()) {
                currentColor = Palette.from(bitmap).generate();
            } else {
                Bitmap bitmapTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.brilliant);
                currentColor = Palette.from(bitmapTemp).generate();
            }

            int defaultColor = context.getResources().getColor(R.color.default_color);

            primaryColorLight = currentColor.getVibrantColor(defaultColor);
            primaryColorDark = currentColor.getDarkMutedColor(defaultColor);
            dominantColor = currentColor.getDominantColor(defaultColor);
        } catch (Exception e) {
            e.printStackTrace();

            primaryColorLight = context.getResources().getColor(R.color.default_color);
            primaryColorDark = context.getResources().getColor(R.color.default_color);
            dominantColor = context.getResources().getColor(R.color.default_color);
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(".ThemeColors", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("primaryColorLight", primaryColorLight);
        editor.putInt("primaryColorDark", primaryColorDark);
        editor.putInt("dominantColor", dominantColor);
        editor.apply();
    }

    public int extractVibrantColor(Drawable drawable) {
        int VibrantColor = context.getResources().getColor(R.color.default_color);
        Bitmap bitmap = null;
        if (returnAPI() >= 26) {
            if (drawable instanceof VectorDrawable) {
                bitmap = drawableToBitmap(drawable);
            } else if (drawable instanceof AdaptiveIconDrawable) {
                try {
                    bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getBackground()).getBitmap();
                } catch (Exception e) {
                    try {
                        bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getForeground()).getBitmap();
                    } catch (Exception e1) {
                        bitmap = drawableToBitmap(drawable);
                    }
                }
            } else {
                bitmap = drawableToBitmap(drawable);
            }
        } else {
            bitmap = drawableToBitmap(drawable);
        }
        Palette currentColor;
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                currentColor = Palette.from(bitmap).generate();

                int defaultColor = context.getResources().getColor(R.color.default_color);
                VibrantColor = currentColor.getVibrantColor(defaultColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    currentColor = Palette.from(bitmap).generate();

                    int defaultColor = context.getResources().getColor(R.color.default_color);
                    VibrantColor = currentColor.getMutedColor(defaultColor);
                }
            } catch (Exception e1) {

            }
        }
        return VibrantColor;
    }

    public int extractDominantColor(Drawable drawable) {
        int VibrantColor = context.getResources().getColor(R.color.default_color);
        Bitmap bitmap = null;
        if (returnAPI() >= 26) {
            if (drawable instanceof VectorDrawable) {
                bitmap = drawableToBitmap(drawable);
            } else if (drawable instanceof AdaptiveIconDrawable) {
                try {
                    bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getBackground()).getBitmap();
                } catch (Exception e) {
                    try {
                        bitmap = ((BitmapDrawable) ((AdaptiveIconDrawable) drawable).getForeground()).getBitmap();
                    } catch (Exception e1) {
                        bitmap = drawableToBitmap(drawable);
                    }
                }
            } else {
                bitmap = drawableToBitmap(drawable);
            }
        } else {
            bitmap = drawableToBitmap(drawable);
        }
        Palette currentColor;
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                currentColor = Palette.from(bitmap).generate();

                int defaultColor = context.getResources().getColor(R.color.default_color);
                VibrantColor = currentColor.getDominantColor(defaultColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (bitmap != null && !bitmap.isRecycled()) {
                    currentColor = Palette.from(bitmap).generate();

                    int defaultColor = context.getResources().getColor(R.color.default_color);
                    VibrantColor = currentColor.getMutedColor(defaultColor);
                }
            } catch (Exception e1) {

            }
        }
        return VibrantColor;
    }

    public Bitmap brightenBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(0xFFFFFFFF, 0x00222222); // lighten
        //ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return bitmap;
    }

    public Bitmap darkenBitmap(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return bitmap;
    }

    public int manipulateColor(int color, float aFactor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * aFactor);
        int g = Math.round(Color.green(color) * aFactor);
        int b = Math.round(Color.blue(color) * aFactor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public int mixColors(int color1, int color2, float ratio /*0 -- 1*/) {
        /*ratio = 1 >> color1*/
        /*ratio = 0 >> color2*/
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public int setColorAlpha(int color, float alphaValue /*1 -- 255*/) {
        int alpha = Math.round(Color.alpha(color) * alphaValue);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    public int getStatusBar() {
        return readPreference(".SystemBars", "StatusBar", 0);
    }

    public int getNavigationBar() {
        return readPreference(".SystemBars", "NavigationBar", 0);
    }

    /*Checkpoint Functions*/
    public boolean AccessibilityServiceEnabled() {
        ComponentName expectedComponentName = new ComponentName(context, OffsetAssistant.class);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    public boolean SettingServiceRunning(Class aClass) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (aClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*Apps Checkpoint Functions*/
    public String appName(String packageName) {
        String Name = null;
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            Name = context.getPackageManager().getApplicationLabel(app).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Name;
    }

    public String activityLabel(ActivityInfo activityInfo) {
        String Name = context.getString(R.string.app_name);
        try {
            Name = activityInfo.loadLabel(context.getPackageManager()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Name = appName(activityInfo.packageName);
        }
        return Name;
    }

    public String appVersionName(String packageName) {
        String Version = "0";

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            Version = packInfo.versionName;
        } catch (Exception e) {
        }

        return Version;
    }

    public int appVersionCode(String packageName) {
        int VersionCode = 0;

        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            VersionCode = packInfo.versionCode;
        } catch (Exception e) {
        }

        return VersionCode;
    }

    public boolean appInstalledOrNot(String packName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(packName, 0);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        } catch (Exception e) {

            app_installed = false;
        }
        return app_installed;
    }

    public boolean ifSystem(String packageName) {
        boolean ifSystem = false;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo targetPkgInfo = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            PackageInfo sys = packageManager.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            ifSystem = (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {

            ifSystem = false;
        } catch (Exception e) {
        }
        return ifSystem;
    }

    public boolean ifDefaultLauncher(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
        if (defaultLauncherStr.equals(packageName)) {
            return true;
        }
        return false;
    }

    public boolean canLaunch(String packageName) {
        return (context.getPackageManager().getLaunchIntentForPackage(packageName) != null);
    }

    public int returnAPI() {
        return API;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return CapitalizeFirstChar(model);
        } else {
            return CapitalizeFirstChar(manufacturer) + " " + model;
        }
    }

    private String CapitalizeFirstChar(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        char first = text.charAt(0);
        if (Character.isUpperCase(first)) {
            return text;
        } else {
            return Character.toUpperCase(first) + text.substring(1);
        }
    }

    public String getCountryIso() {
        String countryISO = "Undefined";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            countryISO = telephonyManager.getSimCountryIso();
            if (countryISO.length() < 2) {
                countryISO = "Undefined";
            }
        } catch (Exception e) {

            countryISO = "Undefined";
        }
        return countryISO;
    }

    /*Converters GUI Functions*/
    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof VectorDrawable) {
            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                bitmap = bitmapDrawable.getBitmap();
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;

            bitmap = Bitmap.createBitmap(layerDrawable.getIntrinsicWidth(), layerDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            layerDrawable.draw(canvas);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    public Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public byte[] drawableToByte(Drawable drawable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        drawableToBitmap(drawable).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Drawable byteToDrawable(byte[] byteImage) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
        return bitmapToDrawable(bitmap);
    }

    public float DpToPixel(float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public float PixelToDp(float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public int DpToInteger(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /*File Functions*/
    public void saveFileEmpty(String fileName) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            fOut.flush();
            fOut.close();
        } catch (Exception e) {

        }
    }

    public void saveFile(String fileName, String content) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fOut.write((content).getBytes());

            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
    }

    public void saveFileAppendLine(String fileName, String content) {
        try {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_APPEND);
            fOut.write((content + "\n").getBytes());

            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
    }

    public String[] readFileLine(String fileName) {
        String[] contentLine = null;
        if (context.getFileStreamPath(fileName).exists()) {
            try {
                FileInputStream fin = new FileInputStream(context.getFileStreamPath(fileName));
                BufferedReader myDIS = new BufferedReader(new InputStreamReader(fin));

                int count = countLine(fileName);
                contentLine = new String[count];
                String line = "";
                int i = 0;
                while ((line = myDIS.readLine()) != null) {
                    contentLine[i] = line;
                    i++;
                }
            } catch (Exception e) {
            }
        }
        return contentLine;
    }

    public String readFile(String fileName) {
        String temp = "0";

        File G = context.getFileStreamPath(fileName);
        if (!G.exists()) {
            temp = "0";
        } else {
            try {
                FileInputStream fin = context.openFileInput(fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(fin, "UTF-8"), 1024);

                int c;
                temp = "";
                while ((c = br.read()) != -1) {
                    temp = temp + Character.toString((char) c);
                }
            } catch (Exception e) {
            }
        }

        return temp;
    }

    public int countLine(String fileName) {
        int nLines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(context.getFileStreamPath(fileName)));
            while (reader.readLine() != null) {
                nLines++;
            }
            reader.close();
        } catch (Exception e) {
        }
        return nLines;
    }

    public void removeLine(String fileName, String lineToRemove) {
        try {
            FileInputStream fin = context.openFileInput(fileName);
            BufferedReader myDIS = new BufferedReader(new InputStreamReader(fin));
            OutputStreamWriter fOut = new OutputStreamWriter(context.openFileOutput(fileName + ".tmp", Context.MODE_APPEND));

            String tmp = "";
            while ((tmp = myDIS.readLine()) != null) {
                if (!tmp.trim().equals(lineToRemove)) {
                    fOut.write(tmp);
                    fOut.write("\n");
                }
            }

            fOut.close();
            myDIS.close();
            fin.close();

            File tmpD = context.getFileStreamPath(fileName + ".tmp");
            File New = context.getFileStreamPath(fileName);

            if (tmpD.isFile()) {
            }
            context.deleteFile(fileName);
            tmpD.renameTo(New);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
        }
    }

    public void savePreference(String PreferenceName, String KEY, String VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putString(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void savePreference(String PreferenceName, String KEY, int VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putInt(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void savePreference(String PreferenceName, String KEY, boolean VALUE) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putBoolean(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void saveDefaultPreference(String KEY, int VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putInt(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void saveDefaultPreference(String KEY, String VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putString(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public void saveDefaultPreference(String KEY, boolean VALUE) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editorSharedPreferences = sharedPreferences.edit();
        editorSharedPreferences.putBoolean(KEY, VALUE);
        editorSharedPreferences.apply();
    }

    public String readPreference(String PreferenceName, String KEY, String defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getString(KEY, defaultVALUE);
    }

    public int readPreference(String PreferenceName, String KEY, int defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getInt(KEY, defaultVALUE);
    }

    public boolean readPreference(String PreferenceName, String KEY, boolean defaultVALUE) {
        return context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE).getBoolean(KEY, defaultVALUE);
    }

    public int readDefaultPreference(String KEY, int defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, defaultVALUE);
    }

    public String readDefaultPreference(String KEY, String defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY, defaultVALUE);
    }

    public boolean readDefaultPreference(String KEY, boolean defaultVALUE) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY, defaultVALUE);
    }
}
