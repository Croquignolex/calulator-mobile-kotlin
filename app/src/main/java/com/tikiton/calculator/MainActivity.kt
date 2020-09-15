package com.tikiton.calculator

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

const val EMPTY_STRING = ""
const val ERROR_STRING = "Error"
const val SIGN_PLUS_VALUE = "+"
const val SIGN_MINUS_VALUE = "-"
const val SIGN_TIMES_VALUE = "x"
const val SIGN_DIVIDE_VALUE = "/"
const val SIGN_EQUALS_VALUE = "="

class MainActivity : AppCompatActivity() {
    // Data
    private var backPressedTime: Long = 0
    private var isEqualActive: Boolean = false
    private var isOneOperatorActive: Boolean = false
    private var previousOperand: String = EMPTY_STRING
    private var previousOperator: String = EMPTY_STRING

    // Operations
    private val addition = {a: Double, b: Double -> (a + b).toString()}
    private val division = {a: Double, b: Double -> (a / b).toString()}
    private val subtraction = {a: Double, b: Double -> (a - b).toString()}
    private val multiplication = {a: Double, b: Double -> (a * b).toString()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Handle back button pressed
    override fun onBackPressed() {
        if((backPressedTime + 2000) > System.currentTimeMillis()) {
            // Can exit if there is 2 seconds between the back button double tap
            super.onBackPressed()
        } else {
            // Neither toast call to action message
            Toast.makeText(
                applicationContext,
                "Appuiyez à nouveau pour quitter",
                Toast.LENGTH_SHORT
            ).show()
        }
        // Update user back pressed time
        backPressedTime = System.currentTimeMillis()
    }

    // Fired when digit is clicked
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onDigit(view: View) {
        // Get digit value
        val digitValue = (view as Button).text

        if(isEqualActive) reset()

        // append while different from 0
        if((displayScreen.text.toString() == keyZero.text.toString()) || isOneOperatorActive) displayScreen.text = digitValue
        else displayScreen.append(digitValue)
        isOneOperatorActive = false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onClear(view: View) {
        reset()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onDecimal(view: View) {
        if(isEqualActive) reset()

        if(!displayScreen.text.toString().contains(".")) displayScreen.append((view as Button).text)
        isOneOperatorActive = false
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onSignOperation(view: View) {
        val operationButton = (view as Button)
        val operationButtonValue = operationButton.text.toString()
        if(isOneOperatorActive) {
            previousOperator = operationButtonValue
            highlightOperator(operationButton)
        }
        else {
            when(operationButtonValue) {
                SIGN_PLUS_VALUE, SIGN_MINUS_VALUE, SIGN_TIMES_VALUE, SIGN_DIVIDE_VALUE -> proceedToSignOperation(operationButton)
                SIGN_EQUALS_VALUE -> {
                    if(previousOperator != EMPTY_STRING) {
                        val result = proceedToOperation(previousOperand, displayScreen.text.toString(), previousOperator);
                        displayScreen.text = trimTrailingZero(result)
                        isEqualActive = true
                        previousOperator = EMPTY_STRING
                        resetOperatorsHighlight()
                    }
                }
            }
        }
    }

    private fun operation(a: Double, b: Double, opt: (Double, Double) -> String) = opt(a, b)

    private fun proceedToOperation(previous: String, current: String, operator: String): String {
        // Operations members
        val previousValue: Double? = previous.toDoubleOrNull()
        val currentValue: Double? = current.toDoubleOrNull()
        // Proceed while cast is OK
        if(previousValue is Double && currentValue is Double) {
            when(operator) {
                SIGN_PLUS_VALUE -> return operation(previousValue, currentValue, addition)
                SIGN_DIVIDE_VALUE -> return operation(previousValue, currentValue, division)
                SIGN_MINUS_VALUE -> return operation(previousValue, currentValue, subtraction)
                SIGN_TIMES_VALUE -> return operation(previousValue, currentValue, multiplication)
            }
        }
        return ERROR_STRING
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun proceedToSignOperation(operationButton: Button) {
        if(previousOperator == EMPTY_STRING) {
            // Keep data
            previousOperand = displayScreen.text.toString()
            previousOperator = operationButton.text.toString()
            highlightOperator(operationButton)
        } else {
            // Proceed & keep
            val result = proceedToOperation(previousOperand, displayScreen.text.toString(), previousOperator);
            displayScreen.text = trimTrailingZero(result)
            previousOperand = result
            previousOperator = operationButton.text.toString()
            highlightOperator(operationButton)
        }
        isEqualActive = false
        isOneOperatorActive = true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun highlightOperator(button: Button) {
        resetOperatorsHighlight()
        button.setTextColor(ContextCompat.getColorStateList(this, R.color.colorDark));
        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorClair);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun reset() {
        // Zero on screen
        displayScreen.text = keyZero.text
        // Reset data
        isEqualActive = false
        isOneOperatorActive = false
        previousOperand = EMPTY_STRING
        previousOperator = EMPTY_STRING
        // Reset buttons colors
        resetOperatorsHighlight()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun resetOperatorsHighlight() {
        signPlus.setTextColor(ContextCompat.getColorStateList(this, R.color.colorWhite));
        signPlus.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorThemeDark);
        signMinus.setTextColor(ContextCompat.getColorStateList(this, R.color.colorWhite));
        signMinus.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorThemeDark);
        signTimes.setTextColor(ContextCompat.getColorStateList(this, R.color.colorWhite));
        signTimes.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorThemeDark);
        signDivide.setTextColor(ContextCompat.getColorStateList(this, R.color.colorWhite));
        signDivide.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorThemeDark);
    }

    // Trim trailing zero for UI
    private fun trimTrailingZero(value: String?): String? {
        return if (!value.isNullOrEmpty()) {
            if (value.indexOf(".") < 0) value
            else value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        } else value
    }
}