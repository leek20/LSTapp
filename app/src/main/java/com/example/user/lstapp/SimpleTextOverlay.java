package com.example.user.lstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by nathanielwendt on 2/28/15.
 */
public class SimpleTextOverlay extends Overlay {
    private double val;

    public SimpleTextOverlay(Context ctx, double val){
        super(ctx);
        this.val = val;
    }

    @Override
    protected void draw(Canvas c, MapView osmv, boolean shadow) {
        if (shadow) return;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        float textSize = 100f;
        paint.setTextSize(textSize);
        String text = String.format( "%.2f", val * 100);
        c.drawText(text + "%", (osmv.getWidth() / 2) - 100, (osmv.getHeight() / 2) + 15,  paint);
    }
}
