/*
 * Copyright (C) 2011 The yanzm Custom View Project
 * 
 * Copyright (C) 2006 The Android Open Source Project
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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TableRadioGroup extends TableLayout {

	private int mCheckedId = -1;
	private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
	private boolean mProtectFromCheckedChange = false;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private PassThroughHierarchyChangeListener mPassThroughListener;
	
	private static final String TAG = "TableRadioGroup";
	private static final boolean DEBUG = false;
	
	public TableRadioGroup(Context context) {
		super(context);
		init();
	}
	
	public TableRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		mChildOnCheckedChangeListener = new CheckedStateTracker();
		mPassThroughListener = new PassThroughHierarchyChangeListener();
		super.setOnHierarchyChangeListener(mPassThroughListener);
	}
	
	@Override
	public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
		mPassThroughListener.mOnHierarchyChangeListener = listener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		if(mCheckedId != -1) {
			mProtectFromCheckedChange = true;
			setCheckedStateForView(mCheckedId, true);
			mProtectFromCheckedChange = false;
			setCheckedId(mCheckedId);
		}
	}
	
	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if(DEBUG)
			Log.d(TAG, "child : " + child.getClass().getName() + ", index : " + index);
		
		if(child instanceof TableRow) {
			final TableRow row = (TableRow)child;

			for(int j = 0; j < row.getChildCount(); j++){
				View vv = row.getChildAt(j);
				
				if(vv instanceof RadioButton) {				
					final RadioButton button = (RadioButton) vv;
					
					if (button.isChecked()) {
						mProtectFromCheckedChange = true;
						if (mCheckedId != -1) {
							setCheckedStateForView(mCheckedId, false);
						}
						mProtectFromCheckedChange = false;
						setCheckedId(button.getId());
					}
				}
			}
		}
		super.addView(child, index, params);
	}
	
	public void check(int id) {
		if(DEBUG)
			Log.d(TAG, "check : id = " + id);

		if(id != -1 && (id == mCheckedId)) {
			return;
		}
		
		if(mCheckedId != -1) {
			setCheckedStateForView(mCheckedId, false);
		}
		
		if(id != -1) {
			setCheckedStateForView(id, true);
		}
		setCheckedId(id);
	}
	
	private void setCheckedId(int id) {
		if(DEBUG)
			Log.d(TAG, "setCheckedId : id = " + id);

		mCheckedId = id;
		if(mOnCheckedChangeListener != null) {
			mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
		}
	}
	
	private void setCheckedStateForView(int viewId, boolean checked){
		if(DEBUG)
			Log.d(TAG, "setCheckedStateForView : id = " + viewId + ", checked = " + checked);		

		View checkedView = findViewById(viewId);
		if(checkedView != null && checkedView instanceof RadioButton) {
			((RadioButton) checkedView).setChecked(checked);
		}
	}
	
	public int getCheckedRadioButtonId() {
		return mCheckedId;
	}
	
	public void clearCheck() {
		check(-1);
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}
	
	public interface OnCheckedChangeListener {
		public void onCheckedChanged(TableRadioGroup group, int checkedId);
	}
	
	private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(DEBUG) {
				Log.d(TAG, "mCheckedId = " + mCheckedId);
				Log.d(TAG, "mProtectFromCheckedChange = " + mProtectFromCheckedChange);
			}
			
			if(mProtectFromCheckedChange) {
				return;
			}
			
			mProtectFromCheckedChange = true;
			if(mCheckedId != -1) {
				setCheckedStateForView(mCheckedId, false);
			}
			mProtectFromCheckedChange = false;
			
			int id = buttonView.getId();
			setCheckedId(id);
		}
	}
	
	private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
		private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
		
		public void onChildViewAdded(View parent, View child) {
			if(DEBUG)
				Log.d(TAG, "onChildViewAdded : parent = " + parent + ", child = " + child);
			
			if(parent == TableRadioGroup.this && child instanceof TableRow) {
				final TableRow row = (TableRow)child;

				for(int j = 0; j < row.getChildCount(); j++){
					View vv = row.getChildAt(j);
					Log.d(TAG, "vv : " + vv.getClass().getName() + ", index : " + j);
									
					if(vv instanceof RadioButton) {
						int id = vv.getId();
								
						if(id == View.NO_ID) {
							id = vv.hashCode();
							vv.setId(id);
						}
						((RadioButton)vv).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
					}
				}				
			}
			if(mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewAdded(parent, child);
			}
		}
		
		public void onChildViewRemoved(View parent, View child) {
			if(DEBUG)
				Log.d(TAG, "onChildViewRemoved : parent = " + parent + ", child = " + child);

			if(parent == TableRadioGroup.this && child instanceof TableRow) {
				final TableRow row = (TableRow)child;

				for(int j = 0; j < row.getChildCount(); j++){
					View vv = row.getChildAt(j);
					Log.d(TAG, "vv : " + vv.getClass().getName() + ", index : " + j);
									
					if(vv instanceof RadioButton) {
						int id = vv.getId();
								
						if(id == View.NO_ID) {
							id = vv.hashCode();
							vv.setId(id);
						}
						((RadioButton)vv).setOnCheckedChangeListener(null);
					}
				}				
			}			
			if(mOnHierarchyChangeListener != null) {
				mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
			}			
		}
	}
}
