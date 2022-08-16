package com.ponkratov.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ponkratov.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.calculator_container, CalculatorFragment())
            .addToBackStack("calculatorFragment")
            .commit()
    }
}