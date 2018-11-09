package cn.ian2018.materialedittext;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import java.util.Calendar;

/**
 * Description:
 * Author:chenshuai
 * E-mail:chenshuai@amberweather.com
 * Date:2018/11/9
 */
public class MaterialEditText extends android.support.v7.widget.AppCompatEditText {

    public static final float TEXT_SIZE = dpToPx(12);
    public static final float TEXT_MARGIN = dpToPx(8);
    public static final float TEXT_VERTICAL_OFFSET = dpToPx(22);
    public static final float TEXT_HORIZONTAL_OFFSET = dpToPx(5);
    public static final float TEXT_ANIMATION_OFFSET = dpToPx(16);
    private Paint mPaint;
    private ObjectAnimator animator;

    float floatingLabelFraction;
    boolean floatingLabelShow;

    public float getFloatingLabelFraction() {
        return floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float floatingLableFraction) {
        this.floatingLabelFraction = floatingLableFraction;
        invalidate();
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(TEXT_SIZE);
        setPadding(getPaddingLeft(), (int) (getPaddingTop() + TEXT_SIZE + TEXT_MARGIN),getPaddingRight(),getPaddingBottom());

        animator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0, 1);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (floatingLabelShow && TextUtils.isEmpty(s)) {
                    floatingLabelShow = false;
                    // 不显示
                    animator.reverse();
                } else if (!floatingLabelShow && !TextUtils.isEmpty(s)){
                    floatingLabelShow = true;
                    // 显示
                    animator.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAlpha((int)(0xff * floatingLabelFraction));
        canvas.drawText(getHint().toString(),TEXT_HORIZONTAL_OFFSET,TEXT_VERTICAL_OFFSET, mPaint);
    }

    public static float dpToPx(float dp) {
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }
}
