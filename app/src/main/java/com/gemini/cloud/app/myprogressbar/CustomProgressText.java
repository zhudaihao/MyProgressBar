package com.gemini.cloud.app.myprogressbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

/**
 * 说明：可以显示图标和文字的ProgressBar
 */
public class CustomProgressText extends ProgressBar {
    private Context mContext;
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;
    private float mProgress;
    private int mState;
    // IconTextProgressBar的状态
    public static final int STATE_DEFAULT = 101;
    public static final int STATE_DOWNLOADING = 102;
    public static final int STATE_PAUSE = 103;
    public static final int STATE_DOWNLOAD_FINISH = 104;
    public static final int STATE_UPDATE = 105;
    // IconTextProgressBar的文字大小(sp)
    private static final float TEXT_SIZE_SP = 12f;

    public CustomProgressText(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
        mContext = context;
        init();
    }

    public CustomProgressText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setIndeterminate(false);
        setIndeterminateDrawable(ContextCompat.getDrawable(mContext,
                android.R.drawable.progress_indeterminate_horizontal));
        setProgressDrawable(ContextCompat.getDrawable(mContext,
                R.drawable.shape_pb_blue_text));
        setMax(100);
        setProgress(0);

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(MeasureUtil.sp2px(mContext, TEXT_SIZE_SP));
        mPaint.setTypeface(Typeface.MONOSPACE);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }


    /**
     * 设置下载状态
     */
    public synchronized void setState(int state) {
        mState = state;
        invalidate();
    }

    /**
     * 设置下载进度
     */
    public synchronized void setProgress(float progress) {
        super.setProgress((int) progress);
        mProgress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mState) {
            case STATE_DEFAULT:
                drawIconAndText(canvas, STATE_DEFAULT, true);
                break;

            case STATE_DOWNLOADING:
                drawIconAndText(canvas, STATE_DOWNLOADING, false);
                break;

            case STATE_PAUSE:
                drawIconAndText(canvas, STATE_PAUSE, true);
                break;

            case STATE_DOWNLOAD_FINISH:
                drawIconAndText(canvas, STATE_DOWNLOAD_FINISH, true);
                break;

            case STATE_UPDATE:
                drawIconAndText(canvas, STATE_DOWNLOAD_FINISH, true);
                break;
            default:
                drawIconAndText(canvas, STATE_DEFAULT, true);
                break;
        }
    }


    /**
     * 绘制动画
     *
     * @param canvas   画布
     * @param state    绘制状态
     * @param onlyText 是否绘制动画
     */
    private void drawIconAndText(Canvas canvas, int state, boolean onlyText) {
        initForState(state);
        String text = getText(state);
        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        if (onlyText) {
            // 仅绘制文字
            float textX = (getWidth() / 2) - textRect.centerX();
            float textY = (getHeight() / 2) - textRect.centerY();
            canvas.drawText(text, textX, textY, mPaint);

        } else {
            // 绘制文字
            float textX = (getWidth() / 2) - textRect.centerX();
            float textY = (getHeight() / 2) - textRect.centerY();
            canvas.drawText(text, textX, textY, mPaint);

            if (state == STATE_DEFAULT) {
                return;
            }

            //绘制目标图覆盖原图
            Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bufferCanvas = new Canvas(bufferBitmap);
            bufferCanvas.drawText(text, textX, textY, mPaint);

            // 设置混合模式
            mPaint.setXfermode(mPorterDuffXfermode);
            mPaint.setColor(Color.WHITE);
            RectF rectF = new RectF(0, 0, getWidth() * mProgress / 100, getHeight());
            // 绘制源图形（显示的文本）
            bufferCanvas.drawRect(rectF, mPaint);
            // 绘制目标图
            canvas.drawBitmap(bufferBitmap, 0, 0, null);
            // 清除混合模式
            mPaint.setXfermode(null);

            //释放混合模式
            if (!bufferBitmap.isRecycled()) {
                bufferBitmap.recycle();
            }
        }
    }

    private void initForState(int state) {
        switch (state) {
            case STATE_DEFAULT:
                setProgress(0);
                mPaint.setColor(Color.parseColor("#FF3B7FFF"));
                break;

            case STATE_DOWNLOADING:
                mPaint.setColor(ContextCompat.getColor(mContext, R.color.pb_blue));
                break;

            case STATE_PAUSE:
                mPaint.setColor(ContextCompat.getColor(mContext, R.color.pb_blue));
                break;

            case STATE_DOWNLOAD_FINISH:
                setProgress(0);
                mPaint.setColor(Color.parseColor("#FF3B7FFF"));
                break;

            default:
                setProgress(0);
                mPaint.setColor(Color.WHITE);
                break;
        }
    }


    private String getText(int state) {
        String text;
        switch (state) {
            case STATE_DEFAULT:
                text = "安装";
                break;

            case STATE_DOWNLOADING:
                text = "安装中";
                break;

            case STATE_PAUSE:
                text = getResources().getString(R.string.pb_continue);
                break;

            case STATE_DOWNLOAD_FINISH:
                text = getResources().getString(R.string.pb_open);
                break;

            default:
                text = "安装";
                break;
        }
        return text;
    }


}
