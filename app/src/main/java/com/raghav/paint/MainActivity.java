package com.raghav.paint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.slider.RangeSlider;

import java.io.FileNotFoundException;
import java.io.OutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {
    //creating the object of type DrawView
    //in order to get the reference of the View
    private DrawView paint;
    //creating objects of type button
    private ImageButton save,color,stroke,undo;
    //creating a RangeSlider object, which will
    // help in selecting the width of the Stroke
    private RangeSlider rangeSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting the reference of the views from their ids
        paint=(DrawView)findViewById(R.id.draw_view);
        rangeSlider=(RangeSlider)findViewById(R.id.rangebar);
        undo=(ImageButton)findViewById(R.id.btn_undo);
        save=(ImageButton)findViewById(R.id.btn_save);
        color=(ImageButton)findViewById(R.id.btn_color);
        stroke=(ImageButton)findViewById(R.id.btn_stroke);
        
        //creating a OnClickListener for each button, to perform certain actions

        //the undo button will remove the most recent stroke from the canvas
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.undo();
            }
        });
        //the save button will save the current canvas which is actually a bitmap
        //in form of PNG, in the storage
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the bitmap from DrawView class
                Bitmap bmp=paint.save();
                //opening a OutputStream to write into the file
                OutputStream imageOutStream = null;

                ContentValues cv=new ContentValues();
                //name of the file
                cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png");
                //type of the file
                cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                //location of the file to be saved
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                //ge the Uri of the file which is to be v=created in the storage
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                try {
                    //open the output stream with the above uri
                    imageOutStream = getContentResolver().openOutputStream(uri);
                    //this method writes the files in storage
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
                    //close the output stream after use
                    imageOutStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        //the color button will allow the user to select the color of his brush
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ColorPicker colorPicker=new ColorPicker(MainActivity.this);
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        //get the integer value of color selected from the dialog box and
                        // set it as the stroke color
                       paint.setColor(color);

                    }

                    @Override
                    public void onCancel() {

                        colorPicker.dismissDialog();
                    }
                })
                        //set the number of color columns you want  to show in dialog.
                        .setColumns(5)
                        //set a default color selected in the dialog
                        .setDefaultColorButton(Color.parseColor("#000000"))
                        .show();
            }
        });
        // the button will toggle the visibility of the RangeBar/RangeSlider
        stroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rangeSlider.getVisibility()==View.VISIBLE)
                    rangeSlider.setVisibility(View.GONE);
                else
                    rangeSlider.setVisibility(View.VISIBLE);
            }
        });

        //set the range of the RangeSlider
        rangeSlider.setValueFrom(0.0f);
        rangeSlider.setValueTo(100.0f);
        //adding a OnChangeListener which will change the stroke width
        //as soon as the user slides the slider
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                paint.setStrokeWidth((int) value);
            }
        });

        //pass the height and width of the custom view to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
    }
}