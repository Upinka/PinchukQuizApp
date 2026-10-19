package ru.pinchuk.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0 // <-- ВОТ ЭТОЙ СТРОКИ НЕ ХВАТАЛО!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        loadQuestionsFromAssets()

        if (questionList.isNotEmpty()) {
            showQuestion(currentQuestionIndex)
        } else {
            findViewById<TextView>(R.id.tvQuestion).text = "Ошибка загрузки вопросов"
        }
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
                    val opts = listOf(parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim())
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
        if (index >= questionList.size) return

        val q = questionList[index]

        // Обновляем ProgressBar
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = questionList.size
        progressBar.progress = index + 1

        // Обновляем тексты
        findViewById<TextView>(R.id.tvQuestionCounter).text = "Вопрос ${index + 1} из ${questionList.size}"
        findViewById<TextView>(R.id.tvQuestion).text = q.questionText

        // Находим кнопки
        val btn1 = findViewById<Button>(R.id.btnOption1)
        val btn2 = findViewById<Button>(R.id.btnOption2)
        val btn3 = findViewById<Button>(R.id.btnOption3)
        val btn4 = findViewById<Button>(R.id.btnOption4)

        // Устанавливаем текст
        btn1.text = q.options[0]
        btn2.text = q.options[1]
        btn3.text = q.options[2]
        btn4.text = q.options[3]

        // Обработчики нажатий
        btn1.setOnClickListener { checkAnswer(0) }
        btn2.setOnClickListener { checkAnswer(1) }
        btn3.setOnClickListener { checkAnswer(2) }
        btn4.setOnClickListener { checkAnswer(3) }
    }

    private fun checkAnswer(selectedIndex: Int) {
        val correctIndex = questionList[currentQuestionIndex].correctIndex

        if (selectedIndex == correctIndex) {
            score++
        }

        currentQuestionIndex++

        if (currentQuestionIndex < questionList.size) {
            showQuestion(currentQuestionIndex)
        } else {
            // Переход к результатам
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("SCORE", score)
                putExtra("TOTAL", questionList.size)
            }
            startActivity(intent)
            finish()
        }
    }
}