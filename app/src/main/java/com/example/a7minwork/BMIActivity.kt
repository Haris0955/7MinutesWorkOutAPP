package com.example.a7minwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minwork.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    //Added variables for METRIC and US UNITS views and a variable for displaying the current
    // selected view..
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW" // Metric Unit View
        private const val US_UNITS_VIEW = "US_UNIT_VIEW" // US Unit View
    }

    // A variable to hold a value to make a selected view visible
    private var currentVisibleView: String = METRIC_UNITS_VIEW

    private var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBmiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarBmi)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = " CALCULATE BMI"
        }
        binding?.toolBarBmi?.setNavigationOnClickListener {
            onBackPressed()
        }

        makeVisibleMetricUnitsView()

        binding?.RGUnits?.setOnCheckedChangeListener { radioGroup, checkedId: Int ->

            if (checkedId == R.id.RB_MetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleUsUnitsView()
            }

        }

        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }

    }

    //Function is used to make the METRIC UNITS VIEW visible and hide the US UNITS VIEW.
    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW // Current View is updated here.
        binding?.TILMetricUnitWeight?.visibility =
            View.VISIBLE // METRIC  Height UNITS VIEW is Visible
        binding?.TILMetricUnitHeight?.visibility =
            View.VISIBLE // METRIC  Weight UNITS VIEW is Visible
        binding?.TILUSMetricUnitWeight?.visibility = View.GONE // make weight view Gone.
        binding?.TILUSMetricUnitFeet?.visibility = View.GONE // make height feet view Gone.
        binding?.TILUSMetricUnitInches?.visibility = View.GONE // make height inch view Gone.

        binding?.ETMetricUnitHeight?.text!!.clear() // height value is cleared if it is added.
        binding?.ETMetricUnitWeight?.text!!.clear() // weight value is cleared if it is added.

        binding?.llDiplayBMIResult?.visibility = View.INVISIBLE
    }

    // function to make the US UNITS view visible.

    private fun makeVisibleUsUnitsView() {
        currentVisibleView = US_UNITS_VIEW // Current View is updated here.
        binding?.TILMetricUnitHeight?.visibility =
            View.INVISIBLE // METRIC  Height UNITS VIEW is InVisible
        binding?.TILMetricUnitWeight?.visibility =
            View.INVISIBLE // METRIC  Weight UNITS VIEW is InVisible
        binding?.TILUSMetricUnitWeight?.visibility = View.VISIBLE // make weight view visible.
        binding?.TILUSMetricUnitFeet?.visibility = View.VISIBLE // make height feet view visible.
        binding?.TILUSMetricUnitInches?.visibility = View.VISIBLE // make height inch view visible.

        binding?.ETUSMetricUnitPounds?.text!!.clear() // weight value is cleared.
        binding?.ETUSMetricUnitFeet?.text!!.clear() // height feet value is cleared.
        binding?.ETUSMetricUnitInches?.text!!.clear() // height inch is cleared.

        binding?.llDiplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun calculateUnits() {
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetricUnits()) {
                val heightValue: Float =
                    (binding?.ETMetricUnitHeight?.text.toString().toFloat()) / 100
                val weightValue: Float = (binding?.ETMetricUnitWeight?.text.toString().toFloat())
                val bmi = weightValue / (heightValue * heightValue)
                displayBMIResult(bmi)
            } else {
                Toast.makeText(this@BMIActivity, "Please enter valid values.", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            if (validateUsUnits()) {

                val heightInFeet: String = binding?.ETUSMetricUnitFeet?.text.toString()
                val heightInInches: String = binding?.ETUSMetricUnitInches?.text.toString()
                // Here the Height Feet and Inch values are merged and multiplied by 12 for converting it to inches.
                val heightValue: Float = ((heightInFeet.toFloat() * 12) + heightInInches.toFloat())
                val weightValue: Float = binding?.ETUSMetricUnitPounds?.text.toString().toFloat()

                val bmi = 703 * (weightValue / (heightValue * heightValue))
                // Displaying the result into UI
                displayBMIResult(bmi)
            } else {
                Toast.makeText(this@BMIActivity, "Please enter valid values.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = true
        if (binding?.ETMetricUnitHeight?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.ETMetricUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    private fun validateUsUnits(): Boolean {
        var isValid = true

        when {
            binding?.ETUSMetricUnitPounds?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.ETUSMetricUnitFeet?.text.toString().isEmpty() -> {
                isValid = false
            }

            binding?.ETUSMetricUnitInches?.text.toString().isEmpty() -> {
                isValid = false
            }
        }

        return isValid
    }

    private fun displayBMIResult(bmi: Float) {

        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        //Use to set the result layout visible
        binding?.llDiplayBMIResult?.visibility = View.VISIBLE

        // This is used to round the result value to 2 decimal values after "."
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.tvBMIValue?.text = bmiValue // Value is set to TextView
        binding?.tvBMIType?.text = bmiLabel // Label is set to TextView
        binding?.tvBMIDescription?.text = bmiDescription // Description is set to TextView
    }

    override fun onDestroy() {
        super.onDestroy()
        //  reset the binding to null to avoid memory leak
        binding = null

    }
}