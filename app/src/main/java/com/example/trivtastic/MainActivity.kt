package com.example.trivtastic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //------------------------ variables Start-------------------------------
        //  Default no of questions
        var noOfQuestions = 5;
        //  Set to the default any category & Easy
        var category = "Any Category"
        //  Default mode
        var mode: String = "easy"
        //  Vibration effect
        val vibrate = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        //  Vibration time
        val vibrationTime: Long = 20


        //  UI element defined below
        //  Seekbar
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        //  Current Number of question text
        val selectedValueText = findViewById<TextView>(R.id.selectedValue)
        //  The drop down for categories
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategories)
        //  Start Button
        val btnStart = findViewById<Button>(R.id.btnStart)
        //  Easy mode button
        val btnEasy = findViewById<Button>(R.id.easy)
        //  Medium button
        val btnMedium = findViewById<Button>(R.id.medium)
        //  Hard button
        val btnHard = findViewById<Button>(R.id.hard)
        //  Read the json text values to populate the spinner
        val categoriesArray = readJsonValuesIntoArray("categories.json")
        //  Setting the spinner
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
            categoriesArray
        )
        val verticalOffset = 120
        val newColor = Color.parseColor("#FF6F61")
        seekBar.progressDrawable.setTint(newColor)
        seekBar.thumb.setTint(newColor)

        //------------------------ variables end-------------------------------

        //------------------------ Setup here-------------------------------\
        // Set the default easy mode btn color to green
        btnEasy.setBackgroundResource(R.drawable.rounded_corner_right)
        // Mode button listeners to chose mode and change colors
        // Easy mode
        btnEasy.setOnClickListener {
            btnEasy.setBackgroundResource(R.drawable.rounded_corner_right)
            btnMedium.setBackgroundResource(R.drawable.rounded_corner)
            btnHard.setBackgroundResource(R.drawable.rounded_corner)
            mode = "easy"
        }
        // Medium mode
        btnMedium.setOnClickListener {
            btnMedium.setBackgroundResource(R.drawable.rounded_corner_right)
            btnEasy.setBackgroundResource(R.drawable.rounded_corner)
            btnHard.setBackgroundResource(R.drawable.rounded_corner)
            mode = "medium"
        }
        // Hard mode
        btnHard.setOnClickListener {
            btnHard.setBackgroundResource(R.drawable.rounded_corner_right)
            btnEasy.setBackgroundResource(R.drawable.rounded_corner)
            btnMedium.setBackgroundResource(R.drawable.rounded_corner)
            mode = "hard"
        }

        // Setting up the spinner
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_color)
        spinnerCategory.adapter = spinnerArrayAdapter
        selectedValueText.text = " Number of questions: $noOfQuestions"
        spinnerCategory.dropDownVerticalOffset= verticalOffset

        // display the number of questions from seekbar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the selected value
                noOfQuestions = progress
                "Number of questions: $noOfQuestions".also { selectedValueText.text = it }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrate.vibrate(
                            VibrationEffect.createOneShot(
                                vibrationTime,
                                VibrationEffect.EFFECT_TICK))
                    }
                }
                else {
                    vibrate.vibrate(vibrationTime)
                }
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                category = parentView?.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        //------------------------ Setup end here-------------------------------

        //------------------------ Listeners here-------------------------------
        // Game start button here
        btnStart.setOnClickListener {
                if (isOnline(this))
                {                btnStart.isClickable = false
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)
                        btnStart.isClickable = true
                    }

                    val value = getValueFromJson(category)
                    val apiUrl: String = if (value == "any") {
                        "api.php?amount=$noOfQuestions&difficulty=$mode&type=multiple"
                    } else {
                        "api.php?amount=$noOfQuestions&category=$value&difficulty=$mode&type=multiple"
                    }
                    var quizResponse: QuizResponse?
                    quizResponse = null
                    CoroutineScope(Dispatchers.Main).launch {
                        quizResponse = getQuizQuestions("https://opentdb.com/", apiUrl)
                        val intent = Intent(this@MainActivity, GameActivity::class.java)
                        intent.putExtra("quizResponse", quizResponse)
                        startActivity(intent)
                    }
                    Toast.makeText(this@MainActivity, "$noOfQuestions+ :  $category + : $mode", Toast.LENGTH_SHORT)
                        .show()


            }else{

           // Toast.makeText(this@MainActivity, "No Network Connected", Toast.LENGTH_SHORT).show()
                //
            }
        }
        //------------------------ Listeners end here-------------------------------

    }

    //------------------------ Functions here-------------------------------
    private fun getValueFromJson(key: String): String? {
        return try {
            val inputStream: InputStream = applicationContext.assets.open("categories.json")
            val categoriesFile = InputStreamReader(inputStream)
            val categoriesJson = JsonParser.parseReader(categoriesFile)
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
                it.readText()
            }
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

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    //------------------------ Functions end here-------------------------------

}


