package com.example.schoolquiz.view

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.schoolquiz.R
import com.example.schoolquiz.network.model.QuestionDetail
import com.example.schoolquiz.viewModel.QuizActivityViewModel
import com.firebase.ui.auth.AuthUI
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
    //private var startTime:Long=0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizviewModel=ViewModelProvider(this).get(QuizActivityViewModel::class.java)
        //chronometer.format="H:MM:SS"
        loadQuiz()
        quizviewModel.questionList.observe(this, Observer {
            progressbar_quiz.visibility= GONE
            next_button.visibility= VISIBLE

            if (it.isNotEmpty()){
                questionList.clear()
                questionNumber=0
                score=0
                questionList.addAll(it)
                totalQuestions=questionList.size
                updateUi(questionNumber)

            }
            else{
                Toast.makeText(this,"This Quiz is currently not Available",Toast.LENGTH_SHORT).show()
                val notAvailable="This Quiz is currently not Available"
                error_textview.text=notAvailable
                error_textview.visibility= VISIBLE
            }

        })
    }
    private fun loadQuiz(){
        val categoryId=intent.getStringExtra("category_id")
        val amount=intent.getStringExtra("amount")
        val type= intent.getStringExtra("type")
        val difficulty=intent.getStringExtra("difficulty")
        progressbar_quiz.visibility= VISIBLE

        if (checkInternet()){
            quizviewModel.getQuiz(categoryId!!,amount!!,difficulty!!,type!!)
            error_textview.visibility= INVISIBLE
            refresh_button.visibility= INVISIBLE
            progressbar_quiz.visibility= INVISIBLE
        }
        else{
            val internetRequired="This app requires Internet connection!"
            error_textview.text=internetRequired
            error_textview.visibility= VISIBLE
            refresh_button.visibility= VISIBLE
            progressbar_quiz.visibility= INVISIBLE
        }


    }

    private fun updateUi(position:Int){
        chronometer.start()
        val questionObject= questionList[position]

        question_textview.text =questionObject.question
        val optionList=ArrayList<String>()
        optionList.addAll(questionObject.incorrectAnswers!!)
        correctAnswer=questionObject.correctAnswer!!
        optionList.add(questionObject.correctAnswer!!)
        optionList.sort()
        if (questionObject.type=="boolean"){
            optionsForBoolean(optionList)
        }
        else {
            optionsForMultiple(optionList)
        }

    }
    private fun optionsForBoolean(list:List<String>){
        option_A.text= list[0]
        option_B.text= list[1]
        option_C.visibility= GONE
        option_D.visibility= GONE
        qn_card_view.visibility= VISIBLE
    }
    fun optionsForMultiple(list:List<String>){
        option_C.visibility= VISIBLE
        option_D.visibility= VISIBLE
        option_A.text= list[0]
        option_B.text= list[1]
        option_C.text= list[2]
        option_D.text= list[3]

        qn_card_view.visibility= VISIBLE

    }

    fun submitAnswer(view:View){
        submit_button.isEnabled=false
        questionNumber++
        next_button.isEnabled=true
        val id= findViewById<RadioButton>(answer_radio_group.checkedRadioButtonId)
        studentAnswer=id.text.toString()

        if(studentAnswer==correctAnswer){
            Toast.makeText(this, "Correct Answer!",Toast.LENGTH_SHORT).show()
            score++
            result_textview.text=displayRightAnswerMessage
            result_textview.visibility=VISIBLE
            result_textview.setTextColor(resources.getColor(R.color.dark_green))
        }else{
            Toast.makeText(this, "Wrong Answer!$correctAnswer",Toast.LENGTH_SHORT).show()
            displayWrongAnswerMessage="Wrong Answer! The correct answer is $correctAnswer"
            result_textview.text=displayWrongAnswerMessage
            result_textview.visibility=VISIBLE
            result_textview.setTextColor(resources.getColor(R.color.wrongAnswer))
        }
        if (questionNumber>=totalQuestions){
            val finish="Finish"
            next_button.text=finish
        }


    }

    fun goNextQuestion(view:View){
        if (questionNumber<totalQuestions){
            updateUi(questionNumber)
            view.isEnabled=false
            answer_radio_group.clearCheck()
            result_textview.visibility=View.INVISIBLE
        }
        else{
            chronometer.stop()
            //Toast.makeText(this,"Quiz Ended.Your  score is $score",Toast.LENGTH_SHORT).show()
            publishResult()
        }

    }

    fun buttonClicked(view: View){
        if (view is RadioButton){
            submit_button.isEnabled=true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.sign_out){

            AuthUI.getInstance().signOut(this)
            Toast.makeText(this,"Sign out button clciked",Toast.LENGTH_SHORT).show()
        }

        return true
    }
    private fun publishResult(){
        category_result.text=intent.getStringExtra("category_name")
        name_result.text=intent.getStringExtra("student_name")
        score_result.text=score.toString()
        time_taken.text=chronometer.text
        if (score>=totalQuestions/2){
            val pass="PASS"
            final_result.text=pass
        }else{
            val fail="FAIL"
            final_result.text=fail
        }
        result_cardview.visibility= VISIBLE
    }

    private fun checkInternet():Boolean{
        val cm= applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork=cm.activeNetworkInfo
        val isConnected:Boolean=activeNetwork?.isConnectedOrConnecting==true
        return isConnected
    }

    fun refreshButtonClicked(view: View){
        loadQuiz()
    }

    override fun onPause() {
        super.onPause()
        chronometer.stop()
    }

    override fun onResume() {
        super.onResume()

    }


}