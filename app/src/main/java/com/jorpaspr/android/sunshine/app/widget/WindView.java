package com.jorpaspr.android.sunshine.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class WindView extends View {

    private float mWindDirAngle;
    private float mWindSpeed;
    private boolean mDraw = false;

    public float getWindDirAngle() {
        return mWindDirAngle;
    }

    public void setWindDirAngle(float windDirAngle) {
        this.mWindDirAngle = windDirAngle % 360;
        mDraw = true;
        invalidate();
        //requestLayout();
    }

    public float getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.mWindSpeed = windSpeed;
    }

    public WindView(Context context) {
        super(context);
    }

    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //TODO Implement logic for measures
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(100, 100);
    }

    // TODO Improve
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mDraw) return;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.parseColor("#cccccc"));
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        int radius = (Math.min(height, width) / 2) - 10;

        Point centre = new Point(height / 2, width / 2);
        canvas.drawCircle(centre.x, centre.y, radius, paint);
        canvas.save();
        canvas.rotate(mWindDirAngle, centre.x, centre.y);
        paint.setColor(Color.parseColor("#646464"));
        canvas.drawLine(centre.x, centre.y, centre.x, centre.y - radius, paint);
        canvas.restore();
    }
}
