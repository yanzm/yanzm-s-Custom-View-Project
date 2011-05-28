/*
 * Copyright (C) 2011 The yanzm Custom View Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yanzm.products.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }	
	
    private final int[] mColors = new int[] {
            0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 
            0xFF00FF00, 0xFFFFFF00, 0xFFFF0000
        };
    private int[] mChroma = new int[] {
    		0xFF000000, 0xFF888888, 0xFFFFFFFF
    };

    private Paint mPaint, mPaintC;
    private Paint mOKPaint;
    private OnColorChangedListener mListener;
    private Shader sg, lg;
    private int selectColor;
    private float selectHue = 0;
    
    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ColorPickerView, defStyle, 0);

		int color = a.getInt(R.styleable.ColorPickerView_color, 0);

		if(color == 0) {
			color = Color.BLACK;
		}
		a.recycle();        

		selectColor = color;
		init();
    }
    
    public void setColor(int c) {
    	selectColor = c;
    }
    
    public void setOnColorChangedListener(OnColorChangedListener l) {
        mListener = l;    	
    }
    
    
    public void init() {
        selectHue = getHue(selectColor);
        
        sg = new SweepGradient(0, 0, mColors, null);
        lg = new LinearGradient(OK_X0, 0, OK_X1, 0, mChroma, null, Shader.TileMode.CLAMP);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(sg);
        mPaint.setStrokeWidth(CENTER_RADIUS);

        mPaintC = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintC.setStyle(Paint.Style.FILL);
        mPaintC.setShader(lg);
        mPaintC.setStrokeWidth(2);
        
        mOKPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOKPaint.setStyle(Paint.Style.FILL);
        mOKPaint.setColor(selectColor);
        mOKPaint.setStrokeWidth(5);
    }
    
    private boolean mTrackingOK;
    private boolean mHighlightOK;
    
    private static final int CENTER_X = 100;
    private static final int CENTER_Y = 100;
    private static final int CENTER_RADIUS = 24;
    private static final float OK_X0 = - CENTER_X/2;
    private static final float OK_X1 =   CENTER_X/2;
    private static final float OK_Y0 = (float) (CENTER_X * 1.2);
    private static final float OK_Y1 = (float) (CENTER_X * 1.5);
    
    private void drawSVRegion(Canvas canvas) {
    	final float RESOLUTION = (float)0.01;
    	
    	for(float y = 0; y < 1; y += RESOLUTION) {
        	mChroma = new int[10];

        	int i = 0;
        	for(float x = 0; i < 10; x += 0.1, i+=1) {
        		mChroma[i] = setHSVColor(selectHue, x, y);
        	}
            lg = new LinearGradient(OK_X0, 0, OK_X1, 0, mChroma, null, Shader.TileMode.CLAMP);
            mPaintC.setShader(lg);

            //canvas.drawRect(OK_X0, OK_X0 + (CENTER_X * y), OK_X1, OK_X0 + (float)(CENTER_X * (y)), mPaintC);
        	canvas.drawLine(OK_X0, OK_X0 + (CENTER_X * y), OK_X1, OK_X0 + (float)(CENTER_X * (y)), mPaintC);
        }
    }
    
    @Override 
    protected void onDraw(Canvas canvas) {
        float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;
        
        canvas.translate(CENTER_X, CENTER_X);
        canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
        
        drawSVRegion(canvas);

        canvas.drawRoundRect(new RectF(OK_X0, OK_Y0, OK_X1, OK_Y1), 5, 5, mOKPaint);
        
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(20);
        textPaint.setAntiAlias(true);
        //canvas.drawText("OK", 0 - 12, (float) (CENTER_X * 1.2) + 22, textPaint);
        canvas.drawText("OK", 0 - 14, (float) (CENTER_X * 1.4) + 2, textPaint);

        if (mTrackingOK) {
            int c = mOKPaint.getColor();
            mOKPaint.setStyle(Paint.Style.STROKE);
            
            if (mHighlightOK) 
                mOKPaint.setAlpha(0xFF);
            else 
                mOKPaint.setAlpha(0x80);

            float padding = 5;
            //canvas.drawCircle(0, 0, CENTER_RADIUS + mOKPaint.getStrokeWidth(), mOKPaint);
            canvas.drawRoundRect(new RectF(OK_X0 - padding, OK_Y0 - padding, OK_X1 + padding, OK_Y1 + padding), 5, 5, mOKPaint);
            mOKPaint.setStyle(Paint.Style.FILL);
            mOKPaint.setColor(c);
        }                    
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(CENTER_X * 2, (int)(CENTER_Y * 2.8));
    }

    private int floatToByte(float x) {
        int n = java.lang.Math.round(x);
        return n;
    }

    private int pinToByte(int n) {
        if (n < 0) 
            n = 0;
        else if (n > 255) 
            n = 255;
        return n;
    }
    
    private float getHue(int color) {
    	float hsv[] = new float[3];
    	Color.colorToHSV(color, hsv);
    	return hsv[0];
    }
    
    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }
    
    private int interpColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }
        
        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0),   Color.red(c1),   p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0),  Color.blue(c1),  p);
        
        return Color.argb(a, r, g, b);
    }
    
    private int rotateColor(int color, float rad) {
        float deg = rad * 180 / PI;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        
        ColorMatrix cm  = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();

        cm.setRGB2YUV();
        tmp.setRotate(0, deg);
        cm.postConcat(tmp);
        tmp.setYUV2RGB();
        cm.postConcat(tmp);
        
        final float[] a = cm.getArray();

        int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
        int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
        int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);
        
        return Color.argb(Color.alpha(color), pinToByte(ir), pinToByte(ig), pinToByte(ib));
    }
    
    private int setHSVColor(float hue, float saturation, float value) {
        float[] hsv = new float[3];
        if(hue >= 360)
        	hue = 359;
        else if(hue < 0)
        	hue = 0;

        if(saturation > 1)
        	saturation = 1;
        else if(saturation < 0)
        	saturation = 0;
        
        if(value > 1)
        	value = 1;
        else if(value < 0)
        	value = 0;

        hsv[0] = hue;
        hsv[1] = saturation;
        hsv[2] = value;
        
        return Color.HSVToColor(hsv);
    }

    private static final float PI = 3.1415927f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - CENTER_X;
        float y = event.getY() - CENTER_Y;
        float r = (float)(java.lang.Math.sqrt(x*x + y*y));
        boolean inOK = false;
        boolean inOval = false;
        boolean inRect = false;
        
        if(r <= CENTER_X) {
        	if(r > CENTER_X - CENTER_RADIUS)
        		inOval = true;            		
        	else if(x >= OK_X0 && x < OK_X1 && y >= OK_X0 && y < OK_X1)
        		inRect = true;
        }
        else if(x >= OK_X0 && x < OK_X1 && y >= OK_Y0 && y < OK_Y1){
        	inOK = true;
        }
        	
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTrackingOK = inOK;
                if (inOK) {
                    mHighlightOK = true;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if (mTrackingOK) {
                    if (mHighlightOK != inOK) {
                        mHighlightOK = inOK;
                        invalidate();
                    }
                } 
                else if(inOval) {
                    float angle = (float)java.lang.Math.atan2(y, x);
                    // need to turn angle [-PI ... PI] into unit [0....1]
                    float unit = angle/(2*PI);
                    if (unit < 0) {
                        unit += 1;
                    }
                    selectColor = interpColor(mColors, unit);
                    mOKPaint.setColor(selectColor);
                    //mChroma[1] = selectColor;
                    selectHue = getHue(selectColor);
                    //lg = new LinearGradient(OK_X0, 0, OK_X1, 0, mChroma, null, Shader.TileMode.CLAMP);
                    //mPaintC.setShader(lg);
                    invalidate();
                } 
                else if(inRect){
                	int selectColor2 = setHSVColor(selectHue, (x - OK_X0)/CENTER_X, (y - OK_X0)/CENTER_Y);
                	selectColor = selectColor2;
                    mOKPaint.setColor(selectColor);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTrackingOK) {
                    if (inOK) {
                    	if(mListener != null) {
                    		mListener.colorChanged(mOKPaint.getColor());
                    	}
                    }
                    mTrackingOK = false;    // so we draw w/o halo
                    invalidate();
                }
                break;
        }
        return true;
    }
}
