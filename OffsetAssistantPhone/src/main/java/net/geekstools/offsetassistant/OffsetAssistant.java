package net.geekstools.offsetassistant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;

import net.geekstools.offsetassistant.Util.Functions.FunctionsClass;

import java.util.concurrent.atomic.AtomicBoolean;

public class OffsetAssistant extends AccessibilityService {

    FunctionsClass functionsClass;

    WindowManager windowManager;
    ViewGroup offsetAssistantPointer, offsetAssistantController;
    WindowManager.LayoutParams layoutParamsPointer, layoutParamsController;

    boolean triggerClick = true;
    float xClick, yClick;

    @Override
    protected void onServiceConnected() {
        functionsClass = new FunctionsClass(getApplicationContext());
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        switch (accessibilityEvent.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                if (accessibilityEvent.getAction() == 10296) {
                    if (functionsClass.returnAPI() < 26) {
                        startService(new Intent(getApplicationContext(), BindServices.class));
                    } else {
                        startForegroundService(new Intent(getApplicationContext(), BindServices.class));
                    }

                    functionsClass = new FunctionsClass(getApplicationContext());
                    functionsClass.savePreference("OffsetAssistant", "isRunning", true);

                    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

                    LayoutInflater layoutInflaterPointer = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    offsetAssistantPointer = (ViewGroup) layoutInflaterPointer.inflate(R.layout.offset_assistant_pointer, null);
                    ImageView viewPointer = (ImageView) offsetAssistantPointer.findViewById(R.id.pointer);

                    Drawable drawablePointer = getDrawable(R.drawable.ic_pointer);
                    drawablePointer.setTint(getColor(R.color.red));
                    viewPointer.setImageDrawable(drawablePointer);

                    layoutParamsPointer = functionsClass.getLayoutParamsPointer();
                    windowManager.addView(offsetAssistantPointer, layoutParamsPointer);

                    /* --- */

                    LayoutInflater layoutInflaterController = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    offsetAssistantController = (ViewGroup) layoutInflaterController.inflate(R.layout.offset_assistant_controller, null);
                    ImageView viewController = (ImageView) offsetAssistantController.findViewById(R.id.control);

                    layoutParamsController = functionsClass.getLayoutParamsController();
                    windowManager.addView(offsetAssistantController, layoutParamsController);

                    offsetAssistantController.setOnTouchListener(new View.OnTouchListener() {
                        int initialX, initialY;
                        float initialTouchX, initialTouchY;

                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            WindowManager.LayoutParams layoutParams = layoutParamsPointer;

                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    initialX = layoutParams.x;
                                    initialY = layoutParams.y;

                                    initialTouchX = motionEvent.getRawX();
                                    initialTouchY = motionEvent.getRawY();

                                    break;
                                case MotionEvent.ACTION_UP:
                                    triggerClick = true;

                                    xClick = initialX + (motionEvent.getRawX() - initialTouchX);
                                    yClick = initialY + (motionEvent.getRawY() - initialTouchY);

                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    int difMoveX = (int) (layoutParams.x - initialTouchX);
                                    int difMoveY = (int) (layoutParams.y - initialTouchY);
                                    if (Math.abs(difMoveX) > Math.abs(functionsClass.DpToInteger(19) + ((functionsClass.DpToInteger(19) * 70) / 100))
                                            || Math.abs(difMoveY) > Math.abs(functionsClass.DpToInteger(19) + ((functionsClass.DpToInteger(19) * 70) / 100))) {
                                        triggerClick = false;
                                    }


                                    layoutParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                                    layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);

                                    windowManager.updateViewLayout(offsetAssistantPointer, layoutParams);

                                    xClick = layoutParams.x;
                                    yClick = layoutParams.y;


                                    /*if ((motionEvent.getRawX() > functionsClass.getLayoutParamsController().x)
                                            && (motionEvent.getRawX() < (functionsClass.getLayoutParamsController().x + functionsClass.getLayoutParamsController().width))

                                            && (motionEvent.getRawY() > (functionsClass.getLayoutParamsController().y + functionsClass.getStatusBar()))
                                            && (motionEvent.getRawY() < (functionsClass.getLayoutParamsController().y + functionsClass.getLayoutParamsController().height + functionsClass.getStatusBar()))) {


                                        layoutParams.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);     // X movePoint
                                        layoutParams.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);     // Y movePoint

                                        windowManager.updateViewLayout(offsetAssistantPointer, layoutParams);

                                        xClick = layoutParams.x;
                                        yClick = layoutParams.y;


                                        if (xClick < initialX && yClick < initialY) {
                                            System.out.println("Pointer Going Top-Left");
                                        }

                                    } else {

                                        System.out.println("*** Outside Controller ***");

                                    }*/


                                    break;
                            }
                            return false;
                        }
                    });

                    offsetAssistantController.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*//Perform Swipe and Simulate DragDrop
                            swipePath.lineTo(xClick, yClick);
                            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
                                    swipePath,
                                    0,
                                    1));*/

                            /*if (triggerClick) {
                                Path swipePath = new Path();
                                swipePath.moveTo(xClick + (layoutParamsPointer.width),
                                        yClick + (layoutParamsPointer.height + (layoutParamsPointer.height*70)/100));
                                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
                                        swipePath,
                                        0,
                                        1));
                                dispatchGesture(gestureBuilder.build(), null, null);
                            }*/

                            try {
                                if (triggerClick == true) {
                                    if ((yClick + functionsClass.getStatusBar()) < functionsClass.getStatusBar()) {
                                        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS);
                                    } else {
                                        Path swipePath = new Path();
                                        swipePath.moveTo(xClick + (layoutParamsPointer.width),
                                                yClick + (layoutParamsPointer.height + (layoutParamsPointer.height*70)/100));
                                        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                                        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
                                                swipePath,
                                                0,
                                                1));
                                        dispatchGesture(gestureBuilder.build(), null, null);
                                    }
                                }
                            } catch (Exception e) { e.printStackTrace(); }

                        }
                    });

                    offsetAssistantController.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            /*if (triggerClick) {
                                Path swipePath = new Path();
                                swipePath.moveTo(xClick + (layoutParamsPointer.width),
                                        yClick + (layoutParamsPointer.height + (layoutParamsPointer.height*70)/100));
                                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
                                        swipePath,
                                        0,
                                        1));
                                dispatchGesture(gestureBuilder.build(), null, null);
                            }*/

                            try {
                                Path swipePath = new Path();
                                swipePath.moveTo(xClick + (layoutParamsPointer.width),
                                        yClick + (layoutParamsPointer.height + (layoutParamsPointer.height*70)/100));
                                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
                                        swipePath,
                                        0,
                                        555));
                                dispatchGesture(gestureBuilder.build(), null, null);
                            } catch (Exception e) { e.printStackTrace(); }

                            return false;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onInterrupt() { }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), BindServices.class));
    }
}
