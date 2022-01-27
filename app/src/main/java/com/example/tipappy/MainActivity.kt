package com.example.tipappy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.ceil

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_TIP_VALUE = "0.00"

class MainActivity : AppCompatActivity() {
    private lateinit var billAmountUserInput: EditText
    private lateinit var percentageText: TextView
    private lateinit var tipValue: TextView
    private lateinit var totalValue: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var seekBarPercentage: SeekBar
    private lateinit var roundUpSwitch: Switch


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        billAmountUserInput = findViewById(R.id.billAmountUserInput)
        percentageText = findViewById(R.id.percentageText)
        tipValue = findViewById(R.id.tipValue)
        totalValue = findViewById(R.id.totalValue)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        seekBarPercentage = findViewById(R.id.seekBarPercentage)
        roundUpSwitch = findViewById(R.id.roundUpSwitch)


        seekBarPercentage.progress = INITIAL_TIP_PERCENT
        percentageText.text = "$INITIAL_TIP_PERCENT%"
        tipValue.text = INITIAL_TIP_VALUE
        totalValue.text = INITIAL_TIP_VALUE
        updateTipDescription(INITIAL_TIP_PERCENT)



        seekBarPercentage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                percentageText.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        billAmountUserInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }

        })
        roundUpSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                var gotText = tipValue.text.toString().toDouble()
                gotText = ceil(gotText)
                tipValue.text = "%.2f".format(gotText)
            } else {
                computeTipAndTotal()
            }
        }


    }

    private fun updateTipDescription(tipPercent: Int) {
        var whatIsThePercentage = when (tipPercent) {
            in 0..9 -> "MEH \uD83E\uDD21"
            in 10..14 -> "OK \uD83D\uDDFF"
            in 15..19 -> "GOOD \uD83D\uDE11"
            in 20..25 -> "ABOVE AVERAGE \uD83D\uDE0A"
            else -> "INSANE \uD83E\uDD11"


        }
        tvTipDescription.text = whatIsThePercentage
        val color = ArgbEvaluator().evaluate(
            (tipPercent.toFloat() / seekBarPercentage.max),
            ContextCompat.getColor(this, R.color.worst_color),
            ContextCompat.getColor(this, R.color.best_color)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (billAmountUserInput.text.isEmpty()) {
            return
        }
        //1. Get the value of the base tip and percent
        val baseAmount = billAmountUserInput.text.toString().toDouble()
        val tipPercent = seekBarPercentage.progress
        //2. Compute the tip and total

        var tipAmount = (baseAmount * tipPercent / 100)
        val totalAmount = tipAmount + baseAmount
        if (roundUpSwitch.isChecked) {
            tipAmount = ceil(tipAmount)
        }

        //3. Update The UI
        tipValue.text = "%.2f".format(tipAmount)
        totalValue.text = "%.2f".format(totalAmount)

    }
}