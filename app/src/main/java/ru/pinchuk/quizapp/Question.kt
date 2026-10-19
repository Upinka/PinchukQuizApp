package ru.pinchuk.quizapp

// data class автоматически создает getters, setters, equals и toString
data class Question(
    val questionText: String,   // Текст самого вопроса
    val options: List<String>,  // Список вариантов ответов (будет 4 штуки)
    val correctIndex: Int       // Индекс правильного ответа (0, 1, 2 или 3)
)