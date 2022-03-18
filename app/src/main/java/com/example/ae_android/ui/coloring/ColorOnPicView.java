package com.example.ae_android.ui.coloring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ColorOnPicView  extends View {
    private Paint paint = new Paint();

    private Path path = new Path();
    private int x,y;



    public ColorOnPicView(Context context) {
        super(context);
    }
    public ColorOnPicView(Context context, AttributeSet attrs){
        super(context,attrs);

    }
    public ColorOnPicView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }
/*
    //Canvas 크기 설정
    int height, width =0;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //setMeasuredDimension(500,500);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if(heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED){
            height = 200;
        }else if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }

        if(widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED){
            width = 200;
        }else if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }

        setMeasuredDimension(width,height);
    }
*/
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setAlpha(30);     //투명도
        canvas.drawPath(path,paint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //return super.onTouchEvent(event);
        x = (int) event.getX();
        y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                y = (int) event.getY();

                path.lineTo(x, y);
                break;
        }
        invalidate();

        return true;
    }


}
