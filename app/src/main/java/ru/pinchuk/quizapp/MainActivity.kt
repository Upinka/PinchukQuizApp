package ru.pinchuk.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ищем кнопку по ID. Убедись, что в activity_main.xml у кнопки есть android:id="@+id/btnStartQuiz"
        val btnStartQuiz = findViewById<Button>(R.id.btnStartQuiz)

        // Проверяем, нашлась ли кнопка, чтобы не вылетало
        btnStartQuiz?.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}