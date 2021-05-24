/*
 * Copyright (C) 2017 Gautam Chibde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.besmart.myvisualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Custom view that creates a Bar visualizer effect for
 * the android {@link android.media.MediaPlayer}
 * <p>
 * Created by gautam chibde on 28/10/17.
 */

public class BarVisualizer extends BaseVisualizer {

    private float density = 50;
    private int gap;

    public BarVisualizer(Context context) {
        super(context);
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        this.density = 50;
        this.gap = 4;
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Sets the density to the Bar visualizer i.e the number of bars
     * to be displayed. Density can vary from 10 to 256.
     * by default the value is set to 50.
     *
     * @param density density of the bar visualizer
     */
    public void setDensity(float density) {
        this.density = density;
        if (density > 256) {
            this.density = 256;
        } else if (density < 10) {
            this.density = 10;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isFFT) {

            byte[] fft = bytes;
            int n = fft.length;
            float[] magnitudes = new float[n / 2 + 1];
            float[] phases = new float[n / 2 + 1];
            magnitudes[0] = (float) Math.abs(fft[0]);      // DC
            magnitudes[n / 2] = (float) Math.abs(fft[1]);  // Nyquist
            phases[0] = phases[n / 2] = 0;

            float barWidth = getWidth() / (float) (magnitudes.length - 1);
            paint.setStrokeWidth(barWidth);

//            Log.i("cclin", String.format("柱形宽度：%s-%d-%d-%d", barWidth, magnitudes.length - 1, getWidth(), n));

            canvas.drawLine(0, getHeight(), 0, (1 - magnitudes[0] / 20.0f) * getHeight(), paint);

            for (int k = 1; k < n / 2; k++) {
                int i = k * 2;
                magnitudes[k] = (float) Math.hypot(fft[i], fft[i + 1]);
                phases[k] = (float) Math.atan2(fft[i + 1], fft[i]);
//                Log.i("cclin", String.format("第 %d 个点的值 %f, %f", k, magnitudes[k], phases[k]));

                float barX = (k * barWidth) + 0;
                canvas.drawLine(barX, getHeight(), barX, (1 - magnitudes[k] / 20.0f) * getHeight(), paint);
            }
            float barX = (n / 2 * barWidth) + 0;
            canvas.drawLine(barX, getHeight(), barX, (1 - magnitudes[n / 2] / 20.0f) * getHeight(), paint);

        } else {
            if (bytes != null) {
                float barWidth = getWidth() / density;
                float div = bytes.length / density;
                paint.setStrokeWidth(barWidth - gap);

                for (int i = 0; i < density; i++) {
                    int bytePosition = (int) Math.ceil(i * div);
//                Log.e("cclin", String.format("%d %d %d %d", bytes[bytePosition], Math.abs(bytes[bytePosition]),
//                        (Math.abs(bytes[bytePosition]) + 128), ((byte) (Math.abs(bytes[bytePosition]) + 128))));

                    /**
                     * 计算分析：
                     * 1.由于 bytes[bytePosition] 是一个无符号 8bit 数
                     * 2.因此，其取值范围是：[-128~127]
                     * 3.Math.abs(bytes[bytePosition]) + 128 ==> 归一化为 128~256
                     * 4.类型转换为 byte ==> 归一化为 -[0~128]
                     * 5.因此，top = h + h*(n/128) // n<0
                     */
                    int top = getHeight() + ((byte) (Math.abs(bytes[bytePosition]) + 128)) * getHeight() / 128;
                    // top = h + h*(n/128) // n<0
                    float barX = (i * barWidth) + (barWidth / 2);
                    canvas.drawLine(barX, getHeight(), barX, top, paint);
                }
                super.onDraw(canvas);
            }
        }
    }
}
