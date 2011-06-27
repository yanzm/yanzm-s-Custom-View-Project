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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ColorFilteredImageView extends ImageView {
	public ColorFilteredImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ColorFilteredImageView, defStyle, 0);

		int tint = a.getInt(R.styleable.ColorFilteredImageView_tint, 0);
		String poterduffMode = a.getString(
				R.styleable.ColorFilteredImageView_porterduff_mode);

		if (tint != 0) {
			PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;

			if(poterduffMode != null) {
				mode = (PorterDuff.Mode.valueOf(poterduffMode) != null) ? PorterDuff.Mode
					.valueOf(poterduffMode) : PorterDuff.Mode.SRC_ATOP;
			}

			setColorFilter(new PorterDuffColorFilter(tint, mode));
		}
		a.recycle();
	}

	public ColorFilteredImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ColorFilteredImageView(Context context) {
		super(context);
	}
}