package ru.pinchuk.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 0)

        // Оценка
        val percentage = (score.toFloat() / total * 100).toInt()
        val grade = when {
            percentage >= 90 -> "Отлично! 🏆"
            percentage >= 70 -> "Хорошо! 👍"
            percentage >= 50 -> "Неплохо 🙂"
            else -> "Попробуй еще раз 💪"
        }

        findViewById<TextView>(R.id.tvScore).text =
            "Результат: $score из $total ($percentage%)\n$grade"

        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            // Перезапуск викторины
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}