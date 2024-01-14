package com.example.trivtastic

import android.os.Bundle
import android.system.Os.read
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var noOfQuestions = 1;
        var category= "Any Category"

        // UI elements here
        // Seekbar
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        // Current Number of question text
        val selectedValueText = findViewById<TextView>(R.id.selectedValue)
        // The drop down for categories
        val spinnerCategory=findViewById<Spinner>(R.id.spinnerCategories)
        // Start Button
        val btnStart=findViewById<Button>(R.id.btnStart)

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
                noOfQuestions = progress
                selectedValueText.text = "Number of questions: $noOfQuestions"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                category = parentView?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        btnStart.setOnClickListener{

            var value = getValueFromJson(category)
            var apiUrl :String
            apiUrl = if(value == "any"){
                "https://opentdb.com/api.php?amount=$noOfQuestions&difficulty=easy&type=multiple"
            } else{
                "https://opentdb.com/api.php?amount=$noOfQuestions&category=$value&difficulty=easy&type=multiple"
            }
            Toast.makeText(this@MainActivity, "$noOfQuestions $category $value", Toast.LENGTH_SHORT).show()
        }

    }
    fun getValueFromJson(key: String): String? {
        return try {
            val inputStream: InputStream = applicationContext.assets.open("categories.json")
            val categoriesFile= InputStreamReader(inputStream)
            val categoriesJson=JsonParser.parseReader(categoriesFile)
            val jsonObject = categoriesJson.asJsonObject
            val value = jsonObject.get(key)?.asString
            value
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    private fun readJsonValuesIntoArray(fileName: String): Array<String> {
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