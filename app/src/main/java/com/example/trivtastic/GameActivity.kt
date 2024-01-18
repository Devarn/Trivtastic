package com.example.trivtastic

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        answers(answerOne,questions,questionNo, answerBtnList)
        answers(answerTwo,questions,questionNo, answerBtnList)
        answers(answerThree,questions,questionNo, answerBtnList)
        answers(answerFour,questions,questionNo, answerBtnList)
        
        updateQuestions(answerBtnList,questions[0])

        btnNext.setOnClickListener {
            for (btn in answerBtnList ){
                btn.isClickable = true
                runOnUiThread {
                    btn.setBackgroundResource(R.drawable.rounded_corner) }

            }
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
    private fun answers(button: Button, questions: List<Question>, questionNo:Int,answerBtnList: MutableList<Button>) {
        button.setOnClickListener {
            if (button.text.toString().equals(questions[questionNo].correct_answer, ignoreCase = true)){
                button.setBackgroundResource(R.drawable.rounded_corner_right_2)

              //Toast.makeText(this@GameActivity, "Correct!", Toast.LENGTH_SHORT).show()
            }else{
                button.setBackgroundResource(R.drawable.rounded_corner_wrong)
                for (btn in answerBtnList){
                    if (btn.text.toString().equals(questions[questionNo].correct_answer, ignoreCase = true)){
                        btn.setBackgroundResource(R.drawable.rounded_corner_right_2)
                    btn.isClickable= false}
                }

            }}

    }


    private fun updateQuestions(answerBtnList: MutableList<Button>, question: Question){
        answerBtnList.shuffle()
        val decodedQuestion = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val questionText = findViewById<TextView>(R.id.txtQuestion)
        questionText.text= decodedQuestion
        answerBtnList[0].text= HtmlCompat.fromHtml(question.correct_answer, HtmlCompat.FROM_HTML_MODE_LEGACY)
        answerBtnList[1].text= HtmlCompat.fromHtml(question.incorrect_answers[0], HtmlCompat.FROM_HTML_MODE_LEGACY)
        answerBtnList[2].text= HtmlCompat.fromHtml(question.incorrect_answers[1], HtmlCompat.FROM_HTML_MODE_LEGACY)
        answerBtnList[3].text= HtmlCompat.fromHtml(question.incorrect_answers[2], HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}