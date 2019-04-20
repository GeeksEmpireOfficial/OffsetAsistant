package net.geekstools.offsetassistant;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.geekstools.offsetassistant.Util.Functions.FunctionsClass;

import java.util.ArrayList;

public class Configurations extends Activity {

    FunctionsClass functionsClass;

    RelativeLayout configurationView;

    TextView permissionsInfo;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 666: {
                if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant.class)) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_view);
        functionsClass = new FunctionsClass(getApplicationContext(), Configurations.this);

        configurationView = (RelativeLayout) findViewById(R.id.configurationView);
        permissionsInfo = (TextView) findViewById(R.id.permissionsInfo);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (functionsClass.returnAPI() > 25) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        window.setStatusBarColor(getColor(R.color.light));
        window.setNavigationBarColor(getColor(R.color.light));

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.light)));
        actionBar.setTitle(Html.fromHtml("<font color='" + getResources().getColor(R.color.default_color) + "'>" + getString(R.string.app_name) + "</font>"));
        actionBar.setSubtitle(Html.fromHtml("<small><font color='" + getResources().getColor(R.color.default_color_light) + "'>" + functionsClass.appVersionName(getPackageName()) + "</font></small>"));

        functionsClass.savePreference(".SystemBars", "StatusBar",
                getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")));
        functionsClass.savePreference(".SystemBars", "NavigationBar",
                getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android")));

        final ArrayList<String> Permissions = new ArrayList<String>();
        Permissions.add(Manifest.permission.INTERNET);
        Permissions.add(Manifest.permission.WAKE_LOCK);
        Permissions.add(Manifest.permission.VIBRATE);
        Permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
        Permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        Permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        Permissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        Permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        Permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant.class)) {
            functionsClass.savePreference("OffsetAssistant", "isRunning", false);

            permissionsInfo.setText("I Need Permissions in Accessibility Services to Click On Your Behalf.\nPlease, Click Here to Continue...");
        } else {
            permissionsInfo.setText("Use Blue Circle (Controller) to Move the Red Circle (Pointer) then Click Or Long Click on Blue Circle (Controller) to Trigger Action on Wherever the Red Circle (Pointer) is.\nPlease, Click Here to Continue...");
        }

        configurationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (functionsClass.readPreference("OffsetAssistant", "isRunning", false)) {

                } else if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant.class)) {
                    requestPermissions(Permissions.toArray(new String[Permissions.size()]), 666);
                } else if (functionsClass.AccessibilityServiceEnabled() && functionsClass.SettingServiceRunning(OffsetAssistant.class) && Settings.canDrawOverlays(getApplicationContext())) {
                    functionsClass.sendInteractionObserverEvent(configurationView,
                            getPackageName(),
                            Configurations.class.getSimpleName(),
                            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                            10296);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!Settings.canDrawOverlays(getApplicationContext())) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(OffsetAssistant.class)) {
            permissionsInfo.setText("I Need Permissions in Accessibility Services to Click On Your Behalf.\nPlease, Click Here to Continue...");
        } else {
            permissionsInfo.setText("Use Blue Circle (Controller) to Move the Red Circle (Pointer) then Click Or Long Click on Blue Circle (Controller) to Trigger Action on Wherever the Red Circle (Pointer) is.\nPlease, Click Here to Continue...");
        }
    }
}
