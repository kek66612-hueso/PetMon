package com.pozornik.mypetmon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.WindowInsetsControllerCompat;

public class CurvedTextView extends View {
    private Paint paint;
    private Path path;
    private String text = "";

    public CurvedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#9E9E9E")); // Наш фирменный серый
        paint.setTextSize(70f); // Размер текста
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        path = new Path();
    }

    public void setText(String text) {
        this.text = text;
        invalidate(); // Перерисовываем
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        path.reset();
        // Рисуем невидимую дугу, по которой пойдет текст
        path.addArc(0, 0, w, h, 180, 180);
        canvas.drawTextOnPath(text, path, 0, h / 4f, paint);
    }
}