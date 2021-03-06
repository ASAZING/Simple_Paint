package apps.dev.simplepaint;

import android.view.*;
import android.content.Context;
import android.util.*;
import android.graphics.*;

public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF000000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;


    private  OnTouchScreen onTouchScreen;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();

    }

    public void setOnTouchScreenListener(OnTouchScreen onTouchScreen){

        this.onTouchScreen = onTouchScreen;
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        brushSize = 10;
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);
        canvasBitmap = null;

    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }

    public void setCanvasBitmap(Bitmap canvasBitmap) {
        this.canvasBitmap = canvasBitmap;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHigh) {
        super.onSizeChanged(w, h, oldWidth, oldHigh);

        if (canvasBitmap == null)
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        else
            canvasBitmap = Bitmap.createScaledBitmap(canvasBitmap, w, h, false);

        drawCanvas = new Canvas(canvasBitmap);
        Log.i("Log: ", "Entro a onSizeChanged");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchScreen.onTouch(OnTouchScreen.ON_DOWN);
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                onTouchScreen.onTouch((OnTouchScreen.ON_UP));
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public float setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
        return pixelAmount;
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    public void setErase(boolean isErase){
        boolean erase = isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

}