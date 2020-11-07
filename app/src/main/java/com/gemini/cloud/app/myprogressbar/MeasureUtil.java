package com.gemini.cloud.app.myprogressbar;

import android.content.Context;

/**
 * 说明：度量工具类
 */
public class MeasureUtil {

    /**
     * 说明：根据手机的分辨率将dp转成为px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 说明：根据手机的分辨率将sp转成为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
