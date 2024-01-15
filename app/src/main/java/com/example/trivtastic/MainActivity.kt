package com.example.trivtastic

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //------------------------ variables used to construct ze URL here-------------------------------
        var noOfQuestions = 1;
        // Its set to the default any category
        var category= "Any Category"
        var mode: String
        //------------------------ variables used to construct ze URL end here-------------------------------

        //------------------------ UI elements defined here-------------------------------

        // Seekbar
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        // Current Number of question text
        val selectedValueText = findViewById<TextView>(R.id.selectedValue)
        // The drop down for categories
        val spinnerCategory=findViewById<Spinner>(R.id.spinnerCategories)
        // Start Button
        val btnStart=findViewById<Button>(R.id.btnStart)
        //------------------------ UI elements defined end here-------------------------------

        //------------------------ Setup defined here-------------------------------

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
                if (progress==0){ noOfQuestions=1}
                "Number of questions: $noOfQuestions".also { selectedValueText.text = it }
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
        //------------------------ Setup defined end here-------------------------------

        //------------------------ Listeners defined here-------------------------------
        // Game start button here
        btnStart.setOnClickListener{

            val value = getValueFromJson(category)
            val apiUrl :String = if(value == "any"){
                "api.php?amount=$noOfQuestions&difficulty=easy&type=multiple"
            } else{
                "api.php?amount=$noOfQuestions&category=$value&difficulty=easy&type=multiple"
            }
            CoroutineScope(Dispatchers.Main).launch {
                val m = getQuizQuestions("https://opentdb.com/", apiUrl).toString()
            }
            Toast.makeText(this@MainActivity, "$noOfQuestions $category $value", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        //------------------------ Listeners defined end here-------------------------------

    }
    //------------------------ Functions defined here-------------------------------
    private fun getValueFromJson(key: String): String? {
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
            val categories = applicationContext.assets.open(fileName).bufferedReader().use {
                it.readText() }
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
    private suspend fun getQuizQuestions(baseUrl: String, apiEndpoint: String): QuizResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val quizApi = retrofit.create(TrivAPI::class.java)
                val call = quizApi.getQuiz(apiEndpoint)
                val response = call.execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    println("Error: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                println("Network request failed: ${e.javaClass.simpleName}")
                e.printStackTrace()
                null
            }
        }
    }
    //------------------------ Functions defined end here-------------------------------

    }


