package com.example.trivtastic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val receivedQuizResponse = intent.getSerializableExtra("quizResponse") as? QuizResponse
        var questionNo = 0
        Log.d("qqqqqqq", receivedQuizResponse.toString())
        val questions: List<Question> = receivedQuizResponse?.results ?: emptyList()
        val answerOne = findViewById<Button>(R.id.answer1)
        val answerTwo = findViewById<Button>(R.id.answer2)
        val answerThree = findViewById<Button>(R.id.answer3)
        val answerFour = findViewById<Button>(R.id.answer4)
        val btnNext = findViewById<Button>(R.id.btnNext)

        val answerBtnList: MutableList<Button> = mutableListOf()
        answerBtnList.add(answerOne)
        answerBtnList.add(answerTwo)
        answerBtnList.add(answerThree)
        answerBtnList.add(answerFour)

        answerOne.setOnClickListener {
            if (answerOne.text == questions[questionNo].correct_answer){
                Toast.makeText(this@GameActivity, "Correct!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@GameActivity, "Wrong", Toast.LENGTH_SHORT).show()
            }

        }
        answerTwo.setOnClickListener {
            if (answerTwo.text == questions[questionNo].correct_answer){
                Toast.makeText(this@GameActivity, "Correct!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@GameActivity, "Wrong", Toast.LENGTH_SHORT).show()
            }

        }
        answerThree.setOnClickListener {
            if (answerThree.text == questions[questionNo].correct_answer){
                Toast.makeText(this@GameActivity, "Correct!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@GameActivity, "Wrong", Toast.LENGTH_SHORT).show()
            }

        }
        answerFour.setOnClickListener {
            if (answerFour.text == questions[questionNo].correct_answer){
                Toast.makeText(this@GameActivity, "Correct!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@GameActivity, "Wrong", Toast.LENGTH_SHORT).show()
            }

        }



        updateQuestions(answerBtnList,questions[0])

        btnNext.setOnClickListener {
            questionNo += 1
            var question = questions[questionNo]
            if (questions.size>=questionNo){

                runOnUiThread {
                    updateQuestions(answerBtnList,question)
                }
            }else{
                Toast.makeText(this@GameActivity, "Questions ended", Toast.LENGTH_SHORT).show()
            }


        }
    }
    fun updateQuestions(answerBtnList: MutableList<Button>,question: Question){
        answerBtnList.shuffle()
        val decodedQuestion = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val questionText = findViewById<TextView>(R.id.txtQuestion)
        questionText.text= "Question: ${decodedQuestion}"

        answerBtnList[0].text= question.correct_answer
        answerBtnList[1].text= question.incorrect_answers[0]
        answerBtnList[2].text= question.incorrect_answers[1]
        answerBtnList[3].text= question.incorrect_answers[2]


    }
}