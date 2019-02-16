package apps.dev.simplepaint;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;

import android.view.animation.AnimationUtils;

import android.widget.*;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.SeekBar;

import io.github.yavski.fabspeeddial.FabSpeedDial;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity  {
    private static final String KEY_DRAW = "draw";
    private DrawingView drawView;
    private ImageButton currPaint;
    private LinearLayout colorOp;
    private MyCountDownTimer countDownTimer;
    private FabSpeedDial fabSpeedDial;
    private TextView sizeBrush;
    private int sizeb = 30;
    public static final int REQUEST_STORAGE = 47;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = findViewById(R.id.drawing);

        LinearLayout paintLayout =findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        drawView.setBrushSize(10);
        drawView.setDrawingCacheEnabled(true);
        colorOp = findViewById(R.id.colors);
        countDownTimer = new MyCountDownTimer(1000, 100);
        fabSpeedDial = findViewById(R.id.fabspeeddial);

        //Check permission WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);

            }
        }
        //Boton Flotante
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                drawView.setBrushSize(drawView.getLastBrushSize());
                /* Obtengo el titulo de cada una de las opciones del menu
                y las comparo para ejecutar su accion corespondiente */
                if (menuItem.getTitle().equals(getResources().getString(R.string.start_new))){
                    newP();
                }else if (menuItem.getTitle().equals(getResources().getString(R.string.save))){
                    savePNG();
                }else if (menuItem.getTitle().equals(getResources().getString(R.string.brush))){
                    showD(1);
                }else if (menuItem.getTitle().equals(getResources().getString(R.string.erase))){
                    showD(2);
                }
                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

        drawView.setOnTouchScreenListener(new OnTouchScreen() {

            @Override
            public void onTouch(int event) {

                switch (event){

                    case OnTouchScreen.ON_DOWN:
                        //Cuando estoy tocando la pantalla
                        // Inicio la animacion y ocualto botones
                        anim(2);
                        break;

                    case OnTouchScreen.ON_UP:
                        //Cuando no estoy tocando la pantalla
                        // Inicio el contador para mostrar los botones
                        countDownTimer.start();
                        break;

                }
            }
        });


        if (savedInstanceState != null){

            drawView.setCanvasBitmap((Bitmap) savedInstanceState.getParcelable(KEY_DRAW));
        }

    }


    public void paintClicked(View view){
            drawView.setErase(false); // desactivo el borrador
            if ( view!=currPaint) {
                ImageButton imgView = (ImageButton) view;
                String color = view.getTag().toString();
                drawView.setColor(color);
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
                currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
                currPaint = (ImageButton) view;
                drawView.setBrushSize(drawView.getLastBrushSize());
            }
    }

    private void savePNG(){
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle(R.string.save_drawing);
        saveDialog.setMessage(R.string.saveGallery);
        saveDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png", "drawing");
                if (imgSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            R.string.successful_save, Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            R.string.error_save, Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }

            }
        });
        saveDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
        drawView.destroyDrawingCache();

    }

    private void newP(){
        //Genero un nuevo lienzo
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle(R.string.new_drawing);
        newDialog.setMessage(R.string.start_newd);
        newDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                drawView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private void anim(int a){
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
        Animation rescale = AnimationUtils.loadAnimation(this, R.anim.rscale);

        if(a==1){
            if(fabSpeedDial.getVisibility() == View.GONE || colorOp.getVisibility() == View.GONE) {
                fabSpeedDial.setVisibility(View.VISIBLE);
                colorOp.setVisibility(View.VISIBLE);
                fabSpeedDial.startAnimation(scale);
                colorOp.startAnimation(scale);
                countDownTimer.start();
            }
        }else if (a == 2){
            if(fabSpeedDial.getVisibility() == View.VISIBLE || colorOp.getVisibility() == View.VISIBLE){
                fabSpeedDial.startAnimation(rescale);
                colorOp.startAnimation(rescale);
                fabSpeedDial.setVisibility(View.GONE);
                colorOp.setVisibility(View.GONE);
                countDownTimer.cancel();

            }

        }

    }

    private class MyCountDownTimer extends CountDownTimer {
        MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }
        /* Despues de que pase el tiempo que asigne oculto
           los botnes e inico las animaciones
            */
        @Override
        public void onFinish() {
                anim(1);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "onTick: ");
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //this method is for save paints and colors
        outState.putParcelable(KEY_DRAW, drawView.getCanvasBitmap());
        super.onSaveInstanceState(outState);

    }

    private void showD(int d){
        final int dialogI = d;
        String title= "";
        final Dialog brushDialog = new Dialog(this);
        if(d == 1){
            title = "Tamaño del pincel :";
        }else if(d == 2){
            title = "Tamaño del borrador :";

        }

        brushDialog.setTitle(title);
        brushDialog.setContentView(R.layout.brush_chooser);
        SeekBar seekBar = brushDialog.findViewById(R.id.seekBarw);
        sizeBrush = brushDialog.findViewById(R.id.sizeB);
        Button setSizeBtn = brushDialog.findViewById(R.id.setSizeB);
        brushDialog.show();
        String p = Integer.toString(sizeb).concat(" px");
        sizeBrush.setText(p);
        seekBar.setProgress(sizeb);
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeb = progress;
                String p = Integer.toString(sizeb)+" px";
                sizeBrush.setText(p);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setSizeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogI == 1){

                    drawView.setBrushSize(sizeb);
                    drawView.setLastBrushSize(sizeb);
                    drawView.setErase(false);

                }else if(dialogI == 2){
                    drawView.setErase(true);
                    drawView.setBrushSize(sizeb);
                }
                brushDialog.dismiss();
            }
        });
    }



}
