package ua.kpi.smartgrid.domain

/**
 * Перевірка введених даних (заняття 6).
 * Кожна функція повертає текст помилки або null, якщо значення правильне.
 */
object Validator {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateRequired(value: String, fieldName: String): String? {
        return if (value.isBlank()) "Поле «$fieldName» обов'язкове" else null
    }

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Введіть електронну пошту"
        if (!emailRegex.matches(email.trim())) return "Формат: name@domain.com"
        return null
    }

    // Складність пароля: щонайменше 8 символів, літера і цифра
    fun validatePassword(password: String): String? {
        if (password.length < 8) return "Щонайменше 8 символів"
        if (!password.any { it.isLetter() }) return "Додайте хоча б одну літеру"
        if (!password.any { it.isDigit() }) return "Додайте хоча б одну цифру"
        return null
    }

    fun validateConfirm(password: String, confirm: String): String? {
        return if (password != confirm) "Паролі не збігаються" else null
    }

    // Число в допустимому діапазоні
    fun validateNumber(value: String, min: Double, max: Double): String? {
        val number = value.replace(',', '.').toDoubleOrNull() ?: return "Введіть число"
        if (number < min || number > max) return "Допустимо від $min до $max"
        return null
    }
}
