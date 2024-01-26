package com.example.trivtastic

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
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
     var questionNo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val receivedQuizResponse = intent.getSerializableExtra("quizResponse") as? QuizResponse
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

        answers(answerOne,questions, answerBtnList)
        answers(answerTwo,questions, answerBtnList)
        answers(answerThree,questions, answerBtnList)
        answers(answerFour,questions, answerBtnList)

        updateQuestions(answerBtnList,questions[0])

        btnNext.setOnClickListener {
            for (btn in answerBtnList ){
                btn.isClickable = true
                runOnUiThread {
                    btn.setBackgroundResource(R.drawable.rounded_corner)
                }
            }
            val nextQuestionNo = questionNo + 1
            if (questions.size-2==questionNo){
                runOnUiThread {
                    btnNext.text = "Finish"
                }
                }

                if (questions.size-1!=questionNo){
                questionNo = nextQuestionNo
                val question = questions[questionNo]
                runOnUiThread {
                    updateQuestions(answerBtnList,question)
                }
            }else{
                    finish()
                Toast.makeText(this@GameActivity, "Questions ended", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@GameActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun answers(button: Button, questions: List<Question>,answerBtnList: MutableList<Button>) {
         val rightSound: MediaPlayer = MediaPlayer.create(this, R.raw.right)
        val wrongSound = MediaPlayer.create(this, R.raw.wrong)!!
        button.setOnClickListener {
            for (btn in answerBtnList){
                btn.isClickable= false}
            if (button.text.toString().equals(questions[questionNo].correct_answer, ignoreCase = true)){
                rightSound.start()
                button.setBackgroundResource(R.drawable.rounded_corner_right
                )
            }else{
                button.setBackgroundResource(R.drawable.rounded_corner_wrong)
                wrongSound.start()
                for (btn in answerBtnList){

                    if (btn.text.toString().equals(questions[questionNo].correct_answer, ignoreCase = true)){
                        btn.setBackgroundResource(R.drawable.rounded_corner_right)
                    }
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