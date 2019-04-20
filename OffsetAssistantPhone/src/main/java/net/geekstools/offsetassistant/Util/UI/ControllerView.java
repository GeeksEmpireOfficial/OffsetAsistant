package net.geekstools.offsetassistant.Util.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ControllerView extends View {

    public ControllerView(Context context) {
        super(context);
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            System.out.println("*** DOWN");
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            System.out.println("*** UP");
        }

        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return true;
    }
}
