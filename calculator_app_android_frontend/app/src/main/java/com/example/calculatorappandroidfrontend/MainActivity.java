package com.example.calculatorappandroidfrontend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity
 * Simple offline calculator with grid keypad for basic arithmetic.
 * - Minimalistic light style.
 * - Handles: add, subtract, multiply, divide, decimal/integer, clear, backspace.
 */
public class MainActivity extends AppCompatActivity {

    private TextView display;

    // Holds the current input or result
    private String currentInput = "";
    // Holds the number prior to the operator
    private String storedValue = "";
    // Tracks which operation (+, -, *, /), or "" if none active
    private String pendingOperator = "";
    // To know if user is starting to type a new number (after equals or operator)
    private boolean startNewInput = true;
    // To keep track if last action was equals
    private boolean lastActionWasEquals = false;

    // PUBLIC_INTERFACE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.calc_display);

        // Set digit buttons
        int[] digitBtnIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
                R.id.btn_4, R.id.btn_5, R.id.btn_6,
                R.id.btn_7, R.id.btn_8, R.id.btn_9
        };
        View.OnClickListener digitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button)v;
                appendDigit(b.getText().toString());
            }
        };
        for (int id : digitBtnIds) {
            findViewById(id).setOnClickListener(digitListener);
        }
        // Set decimal point
        findViewById(R.id.btn_dot).setOnClickListener(view -> appendDot());
        // Set operator buttons
        findViewById(R.id.btn_add).setOnClickListener(view -> handleOperator("+"));
        findViewById(R.id.btn_subtract).setOnClickListener(view -> handleOperator("-"));
        findViewById(R.id.btn_multiply).setOnClickListener(view -> handleOperator("×"));
        findViewById(R.id.btn_divide).setOnClickListener(view -> handleOperator("÷"));

        // Set clear and backspace
        findViewById(R.id.btn_clear).setOnClickListener(view -> clearAll());
        findViewById(R.id.btn_back).setOnClickListener(view -> backspace());

        // Set equals
        findViewById(R.id.btn_equals).setOnClickListener(view -> handleEquals());

        updateDisplay("0"); // Initial display
    }

    // PUBLIC_INTERFACE
    /**
     * Appends a digit to the display.
     */
    private void appendDigit(String digit) {
        if (lastActionWasEquals) {
            currentInput = "";
            startNewInput = true;
            lastActionWasEquals = false;
        }
        if (startNewInput) {
            currentInput = "";
            startNewInput = false;
        }
        if (currentInput.equals("0")) {
            currentInput = ""; // remove leading zero
        }
        currentInput += digit;
        updateDisplay(currentInput);
    }

    // PUBLIC_INTERFACE
    /**
     * Appends a decimal point, if allowed.
     */
    private void appendDot() {
        if (lastActionWasEquals) {
            currentInput = "";
            startNewInput = true;
            lastActionWasEquals = false;
        }
        if (startNewInput) {
            currentInput = "0";
            startNewInput = false;
        }
        if (!currentInput.contains(".")) {
            // If empty, prepend 0
            if (currentInput.isEmpty()) {
                currentInput = "0";
            }
            currentInput += ".";
            updateDisplay(currentInput);
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Handles operator logic.
     */
    private void handleOperator(String operator) {
        if (!pendingOperator.isEmpty() && !startNewInput) {
            // Chain calculation
            performCalculation();
        } else if (!currentInput.isEmpty()) {
            storedValue = currentInput;
        }
        pendingOperator = operator;
        startNewInput = true;
        lastActionWasEquals = false;
    }

    // PUBLIC_INTERFACE
    /**
     * Handles Equals button.
     */
    private void handleEquals() {
        if (!pendingOperator.isEmpty()) {
            performCalculation();
            pendingOperator = "";
            lastActionWasEquals = true;
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Performs the arithmetic operation and updates the display.
     */
    private void performCalculation() {
        if (storedValue.isEmpty())
            storedValue = "0";
        String operand1 = storedValue;
        String operand2 = currentInput.isEmpty() ? storedValue : currentInput;
        double num1, num2;
        try {
            num1 = Double.parseDouble(operand1);
        } catch (NumberFormatException e) {
            num1 = 0;
        }
        try {
            num2 = Double.parseDouble(operand2);
        } catch (NumberFormatException e) {
            num2 = 0;
        }
        double result = 0;
        boolean error = false;
        switch (pendingOperator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "×":
                result = num1 * num2;
                break;
            case "÷":
                if (num2 == 0) {
                    error = true;
                } else {
                    result = num1 / num2;
                }
                break;
            default:
                result = num2;
                break;
        }

        if (error) {
            updateDisplay("Error");
            currentInput = "";
            storedValue = "";
        } else {
            String resStr = formatResult(result);
            updateDisplay(resStr);
            currentInput = resStr;
            storedValue = resStr;
        }
        startNewInput = true;
    }

    // PUBLIC_INTERFACE
    /**
     * Formats result, removes .0 from integer values
     */
    private String formatResult(double val) {
        if (Double.isNaN(val) || Double.isInfinite(val)) {
            return "Error";
        }
        if (val == (long) val)
            return String.format("%d", (long) val);
        else
            return String.format("%s", val);
    }

    // PUBLIC_INTERFACE
    /**
     * Clears everything.
     */
    private void clearAll() {
        currentInput = "";
        storedValue = "";
        pendingOperator = "";
        updateDisplay("0");
        startNewInput = true;
        lastActionWasEquals = false;
    }

    // PUBLIC_INTERFACE
    /**
     * Removes last character from input.
     */
    private void backspace() {
        if (!currentInput.isEmpty() && !lastActionWasEquals) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            if (currentInput.isEmpty()) {
                updateDisplay("0");
            } else {
                updateDisplay(currentInput);
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Updates the calculator display.
     */
    private void updateDisplay(String text) {
        display.setText(text);
    }
}
