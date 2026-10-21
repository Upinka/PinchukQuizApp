package ru.pinchuk.quizapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    // Таймер
    private var questionTimer: CountDownTimer? = null
    private val timePerQuestion = 20000L
    private val tickInterval = 1000L

    // Views
    private lateinit var rootView: ConstraintLayout
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var tvCounter: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var progressBar: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Инициализация Views
        rootView = findViewById(R.id.rootLayout)
        tvCounter = findViewById(R.id.tvQuestionCounter)
        tvQuestion = findViewById(R.id.tvQuestion)
        progressBar = findViewById(R.id.progressBar)
        btn1 = findViewById(R.id.btnOption1)
        btn2 = findViewById(R.id.btnOption2)
        btn3 = findViewById(R.id.btnOption3)
        btn4 = findViewById(R.id.btnOption4)

        loadQuestionsFromAssets()

        if (questionList.isNotEmpty()) {
            showQuestion(currentQuestionIndex)
        } else {
            tvQuestion.text = "Ошибка загрузки вопросов"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        questionTimer?.cancel()
    }

    private fun loadQuestionsFromAssets() {
        try {
            val inputStream = assets.open("questions.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val parts = line!!.split("|")
                if (parts.size == 6) {
                    val qText = parts[0].trim()
                    val opts = listOf(
                        parts[1].trim(), parts[2].trim(),
                        parts[3].trim(), parts[4].trim()
                    )
                    val cIndex = parts[5].trim().toInt()
                    questionList.add(Question(qText, opts, cIndex))
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showQuestion(index: Int) {
        questionTimer?.cancel()
        if (index >= questionList.size) return

        val q = questionList[index]

        // Сброс UI перед новым вопросом
        resetButtonsBackground()
        startQuestionTimer()

        // Обновление прогресса
        progressBar.max = questionList.size
        progressBar.progress = index + 1

        // Обновление текстов
        tvCounter.text = getString(R.string.question_counter_format, index + 1, questionList.size)
        tvQuestion.text = q.questionText

        btn1.text = q.options[0]
        btn2.text = q.options[1]
        btn3.text = q.options[2]
        btn4.text = q.options[3]

        setButtonsEnabled(true)

        btn1.setOnClickListener { handleAnswer(0) }
        btn2.setOnClickListener { handleAnswer(1) }
        btn3.setOnClickListener { handleAnswer(2) }
        btn4.setOnClickListener { handleAnswer(3) }
    }

    private fun handleAnswer(selectedIndex: Int) {
        setButtonsEnabled(false)
        questionTimer?.cancel()

        val correctIndex = questionList[currentQuestionIndex].correctIndex
        val buttons = listOf(btn1, btn2, btn3, btn4)

        // Визуальная обратная связь
        if (selectedIndex == correctIndex) {
            buttons[selectedIndex].setBackgroundResource(R.drawable.btn_correct_bg)
        } else {
            buttons[selectedIndex].setBackgroundResource(R.drawable.btn_wrong_bg)
            buttons[correctIndex].setBackgroundResource(R.drawable.btn_correct_bg)
        }

        // Пауза перед переходом
        rootView.postDelayed({
            resetButtonsBackground()
            checkAnswer(selectedIndex)
        }, 1000)
    }

    private fun startQuestionTimer() {
        questionTimer = object : CountDownTimer(timePerQuestion, tickInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000

                // Используем getString для безопасного форматирования (убирает warning)
                tvCounter.text = getString(
                    R.string.question_timer_format,
                    currentQuestionIndex + 1,
                    questionList.size,
                    secondsLeft
                )

                // Меняем цвет на красный, если осталось меньше 5 секунд
                if (secondsLeft <= 5) {
                    tvCounter.setTextColor(getColor(android.R.color.holo_red_light))
                } else {
                    tvCounter.setTextColor(getColor(android.R.color.white))
                }
            }

            override fun onFinish() {
                checkAnswer(-1) // -1 означает "время вышло"
            }
        }.start()
    }

    private fun checkAnswer(selectedIndex: Int) {
        val correctIndex = questionList[currentQuestionIndex].correctIndex

        if (selectedIndex == correctIndex) {
            score++
        }

        currentQuestionIndex++

        if (currentQuestionIndex < questionList.size) {
            performSmoothTransition {
                showQuestion(currentQuestionIndex)
            }
        } else {
            finishQuiz()
        }
    }

    private fun performSmoothTransition(onTransitionEnd: () -> Unit) {
        rootView.animate()
            .alpha(0f)
            .setDuration(250)
            .withEndAction {
                onTransitionEnd()
                rootView.alpha = 0f
                rootView.animate()
                    .alpha(1f)
                    .setDuration(250)
                    .start()
            }
            .start()
    }

    // ВОТ ЭТОЙ ФУНКЦИИ НЕ ХВАТАЛО!
    private fun resetButtonsBackground() {
        val defaultBg = R.drawable.button_background
        btn1.setBackgroundResource(defaultBg)
        btn2.setBackgroundResource(defaultBg)
        btn3.setBackgroundResource(defaultBg)
        btn4.setBackgroundResource(defaultBg)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        btn1.isEnabled = enabled
        btn2.isEnabled = enabled
        btn3.isEnabled = enabled
        btn4.isEnabled = enabled
    }

    private fun finishQuiz() {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("SCORE", score)
            putExtra("TOTAL", questionList.size)
        }
        startActivity(intent)
        finish()
    }
}