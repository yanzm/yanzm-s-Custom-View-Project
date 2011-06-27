/*
 * Copyright (C) 2011 The yanzm Custom View Project
 *      Yuki Anzai, uPhyca Inc.
 *      http://www.uphyca.com
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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.TextView;


/**
 * An extension to CheckedTextView that enable to set check-marks on the left side.
 * 
 */
public class CustomCheckedTextView extends TextView implements Checkable  {
	private int mPosition;
	private int mDrawablePadding;
    private boolean mChecked;
    private int mCheckMarkResource;
    private Drawable mCheckMarkDrawable;
    private int mBasePaddingRight;
    private int mCheckMarkWidth;
    
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };

    public CustomCheckedTextView(Context context) {
        this(context, null);
    }

    public CustomCheckedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCheckedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomCheckedTextView, defStyle, 0);

        mPosition = a.getInteger(R.styleable.CustomCheckedTextView_position, 0);
        
        mDrawablePadding = a.getDimensionPixelSize(R.styleable.CustomCheckedTextView_drawablePadding, 0);
        
        Drawable d = a.getDrawable(R.styleable.CustomCheckedTextView_checkMark);
        if (d != null) {
            setCheckMarkDrawable(d);
        }

        boolean checked = a.getBoolean(R.styleable.CustomCheckedTextView_checked, false);
        setChecked(checked);
        
        a.recycle();
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * <p>Changes the checked state of this text view.</p>
     *
     * @param checked true to check the text, false to uncheck it
     */
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }


    /**
     * Set the checkmark to a given Drawable, identified by its resourece id. This will be drawn
     * when {@link #isChecked()} is true.
     * 
     * @param resid The Drawable to use for the checkmark.
     */
    public void setCheckMarkDrawable(int resid) {
        if (resid != 0 && resid == mCheckMarkResource) {
            return;
        }

        mCheckMarkResource = resid;

        Drawable d = null;
        if (mCheckMarkResource != 0) {
            d = getResources().getDrawable(mCheckMarkResource);
        }
        setCheckMarkDrawable(d);
    }

    /**
     * Set the checkmark to a given Drawable. This will be drawn when {@link #isChecked()} is true.
     *
     * @param d The Drawable to use for the checkmark.
     */
    public void setCheckMarkDrawable(Drawable d) {
        if (mCheckMarkDrawable != null) {
            mCheckMarkDrawable.setCallback(null);
            unscheduleDrawable(mCheckMarkDrawable);
        }
        if (d != null) {
            d.setCallback(this);
            d.setVisible(getVisibility() == VISIBLE, false);
            d.setState(CHECKED_STATE_SET);
            setMinHeight(d.getIntrinsicHeight());
            
            mCheckMarkWidth = d.getIntrinsicWidth();
            
            switch(mPosition) {
            	case LEFT:
            		mBasePaddingRight = getPaddingLeft();
                    super.setPadding(getPaddingLeft() + mCheckMarkWidth + mDrawablePadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            		break;
            	case RIGHT:
            		mBasePaddingRight = getPaddingRight();
                    super.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + mCheckMarkWidth + mDrawablePadding, getPaddingBottom());
            		break;
            }
            d.setState(getDrawableState());
        } else {
        }
        mCheckMarkDrawable = d;
        requestLayout();
    }
    
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if(mCheckMarkDrawable != null) {
            switch(mPosition) {
        		case LEFT:
        	        mBasePaddingRight = left;
        			super.setPadding(getPaddingLeft() + mCheckMarkWidth + mDrawablePadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        			break;
        		case RIGHT:
        	        mBasePaddingRight = right;
        			super.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight() + mCheckMarkWidth + mDrawablePadding, getPaddingBottom());
        			break;
            }
    	}
    	else {
    		super.setPadding(left, top, right, bottom);
    	}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Drawable checkMarkDrawable = mCheckMarkDrawable;
        if (checkMarkDrawable != null) {
            final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            final int height = checkMarkDrawable.getIntrinsicHeight();

            int y = 0;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y = getHeight() - height;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y = (getHeight() - height) / 2;
                    break;
            }
            
            switch(mPosition) {
            	case LEFT:
            		checkMarkDrawable.setBounds(
            				mBasePaddingRight, 
            				y, 
            				mBasePaddingRight + mCheckMarkWidth, 
            				y + height);
            		checkMarkDrawable.draw(canvas);
            		break;
            	case RIGHT:            	
            		int right = getWidth();
            		checkMarkDrawable.setBounds(
            				right - mCheckMarkWidth - mBasePaddingRight, 
            				y, 
            				right - mBasePaddingRight, 
            				y + height);
            		checkMarkDrawable.draw(canvas);
            		break;
            }
        }
    }
    
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        
        if (mCheckMarkDrawable != null) {
            int[] myDrawableState = getDrawableState();
            
            // Set the state of the Drawable
            mCheckMarkDrawable.setState(myDrawableState);
            
            invalidate();
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = super.dispatchPopulateAccessibilityEvent(event);
        if (!populated) {
            event.setChecked(mChecked);
        }
        return populated;
    }
}
