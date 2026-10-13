package ua.kpi.smartgrid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import ua.kpi.smartgrid.domain.Calculator
import ua.kpi.smartgrid.domain.Validator

/** Перевірка валідації та формул. */
class ValidatorTest {

    @Test
    fun email_isValidated() {
        assertNull(Validator.validateEmail("operator@grid.ua"))
        assertNotNull(Validator.validateEmail("operator"))
    }

    @Test
    fun password_complexity() {
        assertNotNull(Validator.validatePassword("12345678"))   // немає літер
        assertNull(Validator.validatePassword("grid2026"))
    }

    @Test
    fun number_range() {
        assertNull(Validator.validateNumber("0,9", 0.0, 1.0))
        assertNotNull(Validator.validateNumber("1.5", 0.0, 1.0))
    }

    @Test
    fun power_formula() {
        assertEquals(62.35, Calculator.calculatePower(0.4, 100.0, 0.9), 0.01)
        assertEquals(3.0, Calculator.calculateLosses(100.0, 0.1), 0.001)
    }
}
