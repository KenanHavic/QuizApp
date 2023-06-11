package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizapp.databinding.ActivityQuizQuestionsBinding

class QuizQuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<Question>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mUserName: String? = null
    private var mCurrentAnswers: Int = 0

    private lateinit var binding: ActivityQuizQuestionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mUserName = intent.getStringExtra(Constants.USER_NAME)

        binding.tvOptionOne.setOnClickListener(this@QuizQuestionsActivity)
        binding.tvOptionTwo.setOnClickListener(this@QuizQuestionsActivity)
        binding.tvOptionThree.setOnClickListener(this@QuizQuestionsActivity)
        binding.tvOptionFour.setOnClickListener(this@QuizQuestionsActivity)
        binding.btnSubmit.setOnClickListener(this@QuizQuestionsActivity)

        mQuestionsList = Constants.getQuestions()

        setQuestion()
        defaultOptionsView()
    }

    private fun setQuestion() = with(binding) {
        defaultOptionsView()
        val question: Question = mQuestionsList!![mCurrentPosition - 1]
        ivImage.setImageResource(question.image)

        tvProgress.progress = mCurrentPosition
        progressBar.text = "$mCurrentPosition / ${tvProgress.max}"
        tvQuestion.text = question.question
        tvOptionOne.text = question.optionOne
        tvOptionTwo.text = question.optionTwo
        tvOptionThree.text = question.optionThree
        tvOptionFour.text = question.optionFour

        if (mCurrentPosition == mQuestionsList!!.size) {
            btnSubmit.text = getString(R.string.finish_label)
        } else {
            btnSubmit.text = "Submit" // todo: dodaj lokalizaciju
        }
    }

    private fun defaultOptionsView() = with(binding) {
        buildList {
            add(tvOptionOne)
            add(tvOptionTwo)
            add(tvOptionThree)
            add(tvOptionFour)
        }.forEach { currentTextView ->
            currentTextView.setTextColor(Color.parseColor("#7A8089"))
            currentTextView.typeface = Typeface.DEFAULT
            currentTextView.background = ContextCompat.getDrawable(
                this@QuizQuestionsActivity,
                R.drawable.default_option_border_bg,
            )
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        defaultOptionsView()

        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_option_border_bg,
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_option_one -> selectedOptionView(binding.tvOptionOne, 1)
            R.id.tv_option_two -> selectedOptionView(binding.tvOptionTwo, 2)
            R.id.tv_option_three -> selectedOptionView(binding.tvOptionThree, 3)
            R.id.tv_option_four -> selectedOptionView(binding.tvOptionFour, 4)
            R.id.btn_submit -> {
                if (mSelectedOptionPosition == 0) {
                    mCurrentPosition++
                    when {
                        mCurrentPosition <= mQuestionsList!!.size -> {
                            setQuestion()
                        }
                        else -> {
                            val intent = Intent(this, ResultActivity::class.java)
                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWERS, mCurrentAnswers)
                            intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList?.size)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    val question = mQuestionsList?.get(mCurrentPosition - 1)
                    if (question!!.correctAnswer != mSelectedOptionPosition) {
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    } else {
                        mCurrentAnswers++
                    }
                    answerView(question.correctAnswer, R.drawable.correct_option_border_bg)

                    if (mCurrentPosition == mQuestionsList!!.size) {
                        binding.btnSubmit.text = "FINISH"
                    } else {
                        binding.btnSubmit.text = "GO TO NEXT QUESTION"
                    }
                    mSelectedOptionPosition = 0
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int) =
        when (answer) {
            1 -> binding.tvOptionOne.background = ContextCompat.getDrawable(this, drawableView)
            2 -> binding.tvOptionTwo.background = ContextCompat.getDrawable(this, drawableView)
            3 -> binding.tvOptionThree.background = ContextCompat.getDrawable(this, drawableView)
            4 -> binding.tvOptionFour.background = ContextCompat.getDrawable(this, drawableView)
            else -> Unit
        }
}
