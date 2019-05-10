package net.geekstools.offsetassistant

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.net.Uri
import android.provider.Settings
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import net.geekstools.offsetassistant.Util.Functions.FunctionsClass

class OffsetAssistant : AccessibilityService() {

    lateinit var functionsClass: FunctionsClass

    lateinit var windowManager: WindowManager
    lateinit var offsetAssistantPointer: ViewGroup
    lateinit var offsetAssistantController: ViewGroup
    lateinit var layoutParamsPointer: WindowManager.LayoutParams
    lateinit var layoutParamsController: WindowManager.LayoutParams

    internal var triggerClick = true
    internal var xClick: Float = 0.toFloat()
    internal var yClick: Float = 0.toFloat()

    override fun onServiceConnected() {
        functionsClass = FunctionsClass(applicationContext)
        if (!Settings.canDrawOverlays(applicationContext)) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
        when (accessibilityEvent.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (accessibilityEvent.action == 10296) {
                    if (functionsClass.returnAPI() < 26) {
                        startService(Intent(applicationContext, BindServices::class.java))
                    } else {
                        startForegroundService(Intent(applicationContext, BindServices::class.java))
                    }

                    functionsClass = FunctionsClass(applicationContext)
                    functionsClass.savePreference("OffsetAssistant", "isRunning", true)

                    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

                    val layoutInflaterPointer = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    offsetAssistantPointer = layoutInflaterPointer.inflate(R.layout.offset_assistant_pointer, null) as ViewGroup
                    val viewPointer = offsetAssistantPointer.findViewById<View>(R.id.pointer) as ImageView

                    val drawablePointer = getDrawable(R.drawable.ic_pointer)
                    drawablePointer!!.setTint(getColor(R.color.red))
                    viewPointer.setImageDrawable(drawablePointer)

                    layoutParamsPointer = functionsClass.layoutParamsPointer
                    windowManager.addView(offsetAssistantPointer, layoutParamsPointer)

                    /* --- */

                    val layoutInflaterController = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    offsetAssistantController = layoutInflaterController.inflate(R.layout.offset_assistant_controller, null) as ViewGroup
                    val viewController = offsetAssistantController.findViewById<View>(R.id.control) as ImageView

                    layoutParamsController = functionsClass.layoutParamsController
                    windowManager.addView(offsetAssistantController, layoutParamsController)

                    offsetAssistantController.setOnTouchListener(object : View.OnTouchListener {
                        internal var initialX: Int = 0
                        internal var initialY: Int = 0
                        internal var initialTouchX: Float = 0.toFloat()
                        internal var initialTouchY: Float = 0.toFloat()

                        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                            val layoutParams = layoutParamsPointer

                            when (motionEvent.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    initialX = layoutParams.x
                                    initialY = layoutParams.y

                                    initialTouchX = motionEvent.rawX
                                    initialTouchY = motionEvent.rawY
                                }
                                MotionEvent.ACTION_UP -> {
                                    triggerClick = true

                                    xClick = initialX + (motionEvent.rawX - initialTouchX)
                                    yClick = initialY + (motionEvent.rawY - initialTouchY)
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    val difMoveX = (layoutParams.x - initialTouchX).toInt()
                                    val difMoveY = (layoutParams.y - initialTouchY).toInt()
                                    if (Math.abs(difMoveX) > Math.abs(functionsClass.DpToInteger(19) + functionsClass.DpToInteger(19) * 70 / 100) || Math.abs(difMoveY) > Math.abs(functionsClass.DpToInteger(19) + functionsClass.DpToInteger(19) * 70 / 100)) {
                                        triggerClick = false
                                    }


                                    layoutParams.x = initialX + (motionEvent.rawX - initialTouchX).toInt()
                                    layoutParams.y = initialY + (motionEvent.rawY - initialTouchY).toInt()

                                    windowManager.updateViewLayout(offsetAssistantPointer, layoutParams)

                                    xClick = layoutParams.x.toFloat()
                                    yClick = layoutParams.y.toFloat()
                                }
                            }/*if ((motionEvent.getRawX() > functionsClass.getLayoutParamsController().x)
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
                            return false
                        }
                    })

                    offsetAssistantController.setOnClickListener {
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
                                if (yClick + functionsClass.statusBar < functionsClass.statusBar) {
                                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
                                } else {
                                    val swipePath = Path()
                                    swipePath.moveTo(xClick + layoutParamsPointer.width,
                                            yClick + (layoutParamsPointer.height + layoutParamsPointer.height * 70 / 100))
                                    val gestureBuilder = GestureDescription.Builder()
                                    gestureBuilder.addStroke(GestureDescription.StrokeDescription(
                                            swipePath,
                                            0,
                                            1))
                                    dispatchGesture(gestureBuilder.build(), null, null)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    offsetAssistantController.setOnLongClickListener {
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
                            val swipePath = Path()
                            swipePath.moveTo(xClick + layoutParamsPointer.width,
                                    yClick + (layoutParamsPointer.height + layoutParamsPointer.height * 70 / 100))
                            val gestureBuilder = GestureDescription.Builder()
                            gestureBuilder.addStroke(GestureDescription.StrokeDescription(
                                    swipePath,
                                    0,
                                    555))
                            dispatchGesture(gestureBuilder.build(), null, null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        false
                    }
                }
            }
        }
    }

    override fun onInterrupt() {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, BindServices::class.java))
    }
}
