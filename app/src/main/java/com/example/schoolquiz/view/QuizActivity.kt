package com.example.schoolquiz.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.schoolquiz.R
import com.example.schoolquiz.network.model.QuestionDetail
import com.example.schoolquiz.viewModel.QuizActivityViewModel
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {
    private lateinit var quizviewModel:QuizActivityViewModel
    private var questionNumber=0
    private  var totalQuestions:Int =0
    private var questionList:ArrayList<QuestionDetail> = ArrayList()
    private var score=0
    private lateinit var correctAnswer:String
    private lateinit var studentAnswer:String
    private var displayRightAnswerMessage="Congratulations!! Perfect Answer"
    private lateinit var displayWrongAnswerMessage:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizviewModel=ViewModelProvider(this).get(QuizActivityViewModel::class.java)

        val categoryId=intent.getStringExtra("category_id")
        val amount=intent.getStringExtra("amount")
        val type= intent.getStringExtra("type")
        val difficulty=intent.getStringExtra("difficulty")

        quizviewModel.getQuiz(categoryId!!,amount!!,difficulty!!,type!!)

        quizviewModel.questionList.observe(this, Observer {
            questionList.clear()
            questionNumber=0
            score=0
            questionList.addAll(it)
            totalQuestions=questionList.size
            updateUi(questionNumber)
        })

       /**
        answer_radio_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{answer_radio_group,i ->
            if (i==R.id.option_A){
                Toast.makeText(this,"Option A",Toast.LENGTH_SHORT).show()
            }
            if (i==R.id.option_B){
                Toast.makeText(this,"Option B",Toast.LENGTH_SHORT).show()
            }
            if (i==R.id.option_C){
                Toast.makeText(this,"Option C",Toast.LENGTH_SHORT).show()
            }
            if (i==R.id.option_D){
                Toast.makeText(this,"Option D",Toast.LENGTH_SHORT).show()
            }
        })
        **/

    }

    fun updateUi(position:Int){

        question_textview.text =questionList.get(position).question
        val optionList=ArrayList<String>()
        optionList.addAll(questionList.get(position).incorrectAnswers!!)
        correctAnswer=questionList.get(position).correctAnswer!!
        optionList.add(questionList.get(position).correctAnswer!!)
        // optionList.sortBy { it.label.toString() }
        option_A.text=optionList.get(0)
        option_B.text=optionList.get(1)
        option_C.text=optionList.get(2)
        option_D.text=optionList.get(3)

    }

    fun submitAnswer(view:View){
        questionNumber++
        next_button.isEnabled=true
        val id= findViewById<RadioButton>(answer_radio_group.checkedRadioButtonId)
        studentAnswer=id.text.toString()

        if(studentAnswer==correctAnswer){
            Toast.makeText(this, "Correct Answer!",Toast.LENGTH_SHORT).show()
            score++
            result_textview.text=displayRightAnswerMessage
            result_textview.setTextColor(resources.getColor(R.color.correctAnswer))
        }else{
            Toast.makeText(this, "Wrong Answer!$correctAnswer",Toast.LENGTH_SHORT).show()
            displayWrongAnswerMessage="Wrong Answer! The correct answer is $correctAnswer"
            result_textview.text=displayWrongAnswerMessage
            result_textview.setTextColor(resources.getColor(R.color.wrongAnswer))
        }


    }

    fun goNextQuestion(view:View){
        if (questionNumber<totalQuestions){
            updateUi(questionNumber)
            view.isEnabled=false
            answer_radio_group.clearCheck()
        }
        else{
            Toast.makeText(this,"Quiz Ended.Your  score is $score",Toast.LENGTH_SHORT).show()
            result_textview.text="Quiz Ended.Your  score is $score"
        }

    }

    fun checkAnswer(view: View){
        if (view is RadioButton){

        }
    }
}