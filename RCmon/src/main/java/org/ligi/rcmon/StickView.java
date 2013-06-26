/**************************************************************************
 *
 * View for Piloting the UFO via Multitouch
 *
 * Author:  Marcus -LiGi- Bueschleb   
 *
 * Project URL:
 *  http://mikrokopter.de/ucwiki/en/DUBwise
 *
 * License:
 *  http://creativecommons.org/licenses/by-nc-sa/2.0/de/ 
 *  (Creative Commons / Non Commercial / Share Alike)
 *  Additionally to the Creative Commons terms it is not allowed
 *  to use this project in _any_ violent manner! 
 *  This explicitly includes that lethal Weapon owning "People" and 
 *  Organisations (e.g. Army & Police) 
 *  are not allowed to use this Project!
 *
 **************************************************************************/

package org.ligi.rcmon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class StickView extends View {

    private int act_nick = 0;
    private int act_roll = 0;
    private int act_gier = 0;
    private int act_gas = -127;
    private int circle_size = 12;
    private int rect_size = 0;

    private Paint mPaint = new Paint();
    private Paint mBluePaint = new Paint();


    public StickView(Context context) {
        super(context);
        init();
    }

    public StickView(Context context, AttributeSet as) {
        super(context,as);
        init();
    }

    private void init() {
        mBluePaint.setColor(Color.BLUE);
    }

    public void setNick(int nick) {
        this.act_nick = nick;
    }


    public void setRoll(int roll) {
        this.act_roll = roll;
    }

    public void setYaw(int yaw) {
        this.act_gier = yaw;
    }


    public float gierAsFloat() {
        return act_gier / 127.0f;
    }

    public float gasAsFloat() {
        return act_gas / 127.0f * -1.0f;
    }

    public float nickAsFloat() {
        return act_nick / 127.0f;
    }


    public float rollAsFloat() {
        return act_roll / 127.0f;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        rect_size = h - 42;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(0xCCCCCCCC);

        canvas.drawRect(new Rect(0, this.getHeight(), rect_size, this.getHeight() - rect_size), mPaint);
        canvas.drawRect(new Rect(this.getWidth(), this.getHeight(), this.getWidth() - rect_size, this.getHeight() - rect_size), mPaint);

        mPaint.setColor(0xFF000000);
        canvas.drawLine(0, this.getHeight(), rect_size, this.getHeight() - rect_size, mPaint);
        canvas.drawLine(0, this.getHeight() - rect_size, rect_size, this.getHeight(), mPaint);


        canvas.drawLine(this.getWidth(), this.getHeight(), this.getWidth() - rect_size, this.getHeight() - rect_size, mPaint);
        canvas.drawLine(this.getWidth(), this.getHeight() - rect_size, this.getWidth() - rect_size, this.getHeight(), mPaint);


        mPaint.setColor(0xCCCCCCBB);

        // circles for calced position
        mPaint.setColor(0xCCCCCCBB);

        canvas.drawCircle(rect_size / 2 + gierAsFloat() * rect_size / 2, this.getHeight() - rect_size / 2 + gasAsFloat() * rect_size / 2, circle_size, mBluePaint);
        canvas.drawCircle(this.getWidth() - rect_size / 2 - rollAsFloat() * rect_size / -2, this.getHeight() - rect_size / 2 + nickAsFloat() * rect_size / 2, circle_size, mBluePaint);

        //canvas.drawCircle(this.getHeight()-rect_size/2 + circle_size/2 ,rect_size/2  - circle_size,circle_size,mPaint );

        //canvas.drawCircle(x2,y2,30,mPaint );


        postInvalidateDelayed(16);
    }


    public void setGas(int gas) {
        this.act_gas = gas;
    }
}
