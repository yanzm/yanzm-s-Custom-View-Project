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

import yanzm.products.customview.ColorPickerView.OnColorChangedListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ColorPickerDialog extends Dialog {

    private OnColorChangedListener mListener;
    private int mInitialColor;
    
    public ColorPickerDialog(Context context, OnColorChangedListener listener, int initialColor) {
    	super(context);
    	mListener = listener;
    	mInitialColor = initialColor;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color) {
                mListener.colorChanged(color);
                dismiss();
            }
        };

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        
        ColorPickerView v = new ColorPickerView(getContext());
        v.setOnColorChangedListener(l);
        v.setColor(mInitialColor);
        
        setContentView(v, lp);
        setTitle("- Color -");
    }

}