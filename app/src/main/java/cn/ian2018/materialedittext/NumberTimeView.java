package cn.ian2018.materialedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author:chenshuai
 * E-mail:chenshuai@amberweather.com
 * Date:2018/11/9
 */
public class NumberTimeView extends View {
    private static final float STROKE_WIDTH = 3f;

    private Calendar mCalendar;

    private Paint mBackgroundPaint;
    private Paint mHandPaint;

    private float mSecondHandLength;

    private int mWidth;
    private int mHeight;
    private float mCenterX;
    private float mCenterY;
    private float mScale = 1;

    private Bitmap mBackgroundBitmap;

    private final float HAND_END_CAP_RADIUS = 4f;
    private final float SHADOW_RADIUS = 6f;

    public NumberTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.BLACK);

        // 获取表盘背景图
        mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

        mHandPaint = new Paint();
        mHandPaint.setColor(Color.BLACK);
        mHandPaint.setStrokeWidth(STROKE_WIDTH);
        // 消除锯齿
        mHandPaint.setAntiAlias(true);
        mHandPaint.setStyle(Paint.Style.STROKE);

        mCalendar = Calendar.getInstance();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 在屏幕上找到中心点的坐标
        mCenterX = mWidth / 2f;
        mCenterY = mHeight / 2f;

        // 计算图片缩放比例
        mScale = ((float) mWidth) / (float) mBackgroundBitmap.getWidth();
        // 计算表针的长度并将其存储在成员变量中。
        mSecondHandLength = 0.9f * mWidth / 2;


        // 缩放背景图片
        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                (int) (mBackgroundBitmap.getWidth() * mScale),
                (int) (mBackgroundBitmap.getHeight() * mScale), true);

        mUpdateTimeHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long now = System.currentTimeMillis();
        mCalendar.setTimeInMillis(now);

        //canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
        canvas.drawBitmap(mBackgroundBitmap, mCenterX - mBackgroundBitmap.getWidth() / 2, mCenterY - mBackgroundBitmap.getHeight() / 2, mBackgroundPaint);

        /*
         * 这些计算反映了每单位时间的旋转度，例如，
         * 360 / 60 = 6 and 360 / 12 = 30.
         * 1s = 6°
         * 1minute = 60s
         */
        final float seconds =
                (mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MINUTE) / 1000f);
        final float secondsRotation = seconds *(6f / 60f);
        Log.e("onDrawTT", "seconds " + seconds + "    secondsRotation  " + secondsRotation );

        // 在我们开始旋转它之前保存画布状态
        canvas.save();

        canvas.rotate(secondsRotation, mCenterX, mCenterY);
        canvas.drawCircle((float) (mCenterX + mSecondHandLength * Math.sin(secondsRotation)), (float) (mCenterY - mSecondHandLength * Math.cos(secondsRotation)), HAND_END_CAP_RADIUS, mHandPaint);

        // 恢复画布的原始方向
        canvas.restore();
    }

    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.MILLISECONDS.toMillis(10);
    // 处理程序在交互模式下每秒更新一次
    private final Handler mUpdateTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (0 == message.what) {
                invalidate();
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(0, delayMs);
            }
        }
    };
}
