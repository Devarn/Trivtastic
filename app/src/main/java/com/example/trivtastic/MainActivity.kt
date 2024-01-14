package com.example.trivtastic

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // UI elements here
        // Seekbar
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        // Current Number of question text
        val selectedValueText = findViewById<TextView>(R.id.selectedValue)
        // The drop down for categories
        val spinnerCategory=findViewById<Spinner>(R.id.spinnerCategories)

        // Read the json text values to populate the spinner
        val categoriesArray=readJsonValuesIntoArray("categories.json")
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            categoriesArray
        )
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerArrayAdapter

        // Display the number of questions from seekbar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the selected value
                selectedValueText.text = "Number of questions: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })


    }

    fun readJsonValuesIntoArray(fileName: String): Array<String> {
        try {
            val categories = applicationContext.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(categories)
            // Empty list to store the values
            val values = mutableListOf<String>()

            // Loop the keys and add them to the array
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                values.add(key)
            }
            // Return as array
            return values.toTypedArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return emptyArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyArray()
        }
    }






}