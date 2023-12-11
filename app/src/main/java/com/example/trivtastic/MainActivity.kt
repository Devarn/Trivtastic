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

        val spinnerCategory=findViewById<Spinner>(R.id.spinnerCategories)
        val categoriesArray=readJsonValuesIntoArray("categories.json")


        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            categoriesArray
        ) //selected item will look like a spinner set from XML

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerArrayAdapter
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val selectedValueText = findViewById<TextView>(R.id.selectedValue)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the selected value
                selectedValueText.text = "Number of questions: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed, but you can add custom behavior here if necessary
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed, but you can add custom behavior here if necessary
            }
        })


    }

    fun readJsonValuesIntoArray(fileName: String): Array<String> {
        try {
            val categories = applicationContext.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(categories)

            // Initialize an empty list to store the values
            val values = mutableListOf<String>()

            // Loop through the keys in the JSON object and add their values to the list
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                //val value = jsonObject.getString(key)
                values.add(key)
            }

            // Convert the list to an array of strings and return it
            return values.toTypedArray()
        } catch (e: IOException) {
            // Handle the IOException, or you can simply propagate it
            e.printStackTrace()
            return emptyArray()
        } catch (e: Exception) {
            // Handle any other exceptions that might occur during JSON parsing
            e.printStackTrace()
            return emptyArray()
        }
    }






}