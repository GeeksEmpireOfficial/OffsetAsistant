package net.geekstools.offsetassistant.Util.UI

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ControllerView : View {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            println("*** DOWN")
        }
        if (event.action == MotionEvent.ACTION_UP) {
            println("*** UP")
        }

        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        return true
    }
}
