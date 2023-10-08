package com.raghav.paint.xml

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.google.android.material.slider.RangeSlider
import com.raghav.paint.databinding.ActivityMainBinding
import com.raghav.paint.util.ERROR_SAVING


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val saveImageLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("image/*")) {
                it?.let { uri ->
                    val bitmap = binding.drawView.save()
                    contentResolver.openOutputStream(uri)?.use { op ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, op)
                    }
                } ?: Toast.makeText(this, ERROR_SAVING, Toast.LENGTH_SHORT).show()
            }

        //the undo button will remove the most recent stroke from the canvas
        binding.btnUndo.setOnClickListener { binding.drawView.undo() }

        binding.btnRedo.setOnClickListener { binding.drawView.redo() }

        //the save button will save the current canvas which is actually a bitmap
        //in form of PNG, in the storage
        binding.btnSave.setOnClickListener { saveImageLauncher.launch("sample.png") }

        //the color button will allow the user to select the color of his brush
        binding.btnColor.setOnClickListener {
            MaterialColorPickerDialog
                .Builder(this)                            // Pass Activity Instance
                .setTitle("Pick Theme")                // Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)    // Default ColorShape.CIRCLE
                .setColorSwatch(ColorSwatch._300)    // Default ColorSwatch._500
//                .setDefaultColor(mDefaultColor) 		// Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection
                    binding.drawView.setColor(color)
                }
                .show()
        }
        // the button will toggle the visibility of the RangeBar/RangeSlider
        binding.btnStroke.setOnClickListener {
            if (binding.rangebar.visibility == View.VISIBLE)
                binding.rangebar.visibility = View.GONE
            else binding.rangebar.visibility = View.VISIBLE
        }

        //set the range of the RangeSlider
        binding.rangebar.setValueFrom(0.0f)
        binding.rangebar.setValueTo(100.0f)
        //adding a OnChangeListener which will change the stroke width
        //as soon as the user slides the slider
        binding.rangebar.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            binding.drawView.setStrokeWidth(
                value.toInt()
            )
        })

        //pass the height and width of the custom view to the init method of the DrawView object
        val vto: ViewTreeObserver = binding.drawView.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.drawView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.drawView.measuredWidth
                val height = binding.drawView.measuredHeight
                binding.drawView.init(height, width)
            }
        })
    }
}