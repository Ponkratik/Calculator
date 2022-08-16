package com.ponkratov.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ponkratov.calculator.databinding.FragmentCalculatorBinding
import com.ponkratov.calculator.rpn.RPN
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding: FragmentCalculatorBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentCalculatorBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val numButtons = listOf(
                button0,
                button1,
                button2,
                button3,
                button4,
                button5,
                button6,
                button7,
                button8,
                button9,
                button0
            )

            val signButtons = listOf(
                buttonDivide,
                buttonMultiply,
                buttonMinus,
                buttonPlus
            )

            val defaultString = getString(R.string.default_string)

            numButtons.forEach { button ->
                button.setOnClickListener {
                    if (textviewResult.text.toString() == defaultString) {
                        textviewResult.text = button.text
                    } else {
                        textviewResult.append(button.text)
                    }
                }
            }

            signButtons.forEach { button ->
                button.setOnClickListener {
                    if (textviewResult.text.toString() == defaultString || textviewResult.text.split(
                            " "
                        ).last { it.isNotBlank() } == "("
                    ) {
                        Toast.makeText(
                            activity,
                            "You can not enter an operation now",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (textviewResult.text.split(" ").last { it.isNotBlank() } in setOf(
                            "+",
                            "-",
                            "*",
                            "/"
                        )) {
                        val newText = "${
                            textviewResult.text.subSequence(
                                0,
                                textviewResult.text.length - 3
                            )
                        } ${button.text} "
                        textviewResult.text = newText
                    } else {
                        textviewResult.append(" ${button.text} ")
                    }
                }
            }

            buttonPoint.setOnClickListener {
                val last = textviewResult.text.split(" ").last { it.isNotBlank() }
                if (textviewResult.text.toString() == defaultString) {
                    Toast.makeText(
                        activity,
                        "You can not enter an operation first",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (last !in setOf("+", "-", "*", "/") && !last.contains(buttonPoint.text)) {
                    textviewResult.append(buttonPoint.text)
                } else {
                    Toast.makeText(
                        activity,
                        "You can not enter point now",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            buttonClear.setOnClickListener {
                textviewResult.text = defaultString
            }

            buttonBackspace.setOnClickListener {
                textviewResult.text.trim()
                if (textviewResult.text.isNotEmpty()) {
                    textviewResult.text =
                        textviewResult.text.subSequence(0, textviewResult.text.length - 1)
                }
                textviewResult.text.trim()
            }

            buttonOpenBracket.setOnClickListener {
                if (textviewResult.text.split(" ").last { it.isNotBlank() } in setOf(
                        "+",
                        "-",
                        "*",
                        "/",
                        "("
                    )) {
                    textviewResult.append(" ${buttonOpenBracket.text} ")
                } else {
                    Toast.makeText(
                        activity,
                        "You can not enter bracket now",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            buttonCloseBracket.setOnClickListener {
                if ((textviewResult.text.split(" ").last { it.isNotBlank() } in setOf(
                        "+",
                        "-",
                        "*",
                        "/",
                        "("
                    ) || textviewResult.text == getString(R.string.default_string)) || textviewResult.text.count { it == '(' } <= textviewResult.text.count { it == ')' }) {
                    Toast.makeText(
                        activity,
                        "You can not enter bracket now",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    textviewResult.append(" ${buttonCloseBracket.text} ")
                }
            }

            buttonEquals.setOnClickListener {
                val bracketStack: Stack<Char> = Stack<Char>()
                textviewResult.text.forEach {
                    if (it == '(') bracketStack.push(it)
                    if (it == ')' && bracketStack.isNotEmpty()) bracketStack.pop()
                }

                if (bracketStack.isNotEmpty()) {
                    /*Toast.makeText(
                        activity,
                        "Please, verify count of your brackets",
                        Toast.LENGTH_LONG
                    ).show()*/

                    Snackbar.make(
                        requireActivity().findViewById(R.id.calculator_container),
                        "Please, verify count of your brackets",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("OK") {

                        }
                        .show()

                    return@setOnClickListener
                }

                val result = RPN.calculateRPN(RPN.getRPN(textviewResult.text.toString()))
                if (ceil(result) == floor(result)) {
                    textviewResult.text = result.toInt().toString()
                } else {
                    textviewResult.text = result.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}