package com.FTG2024.hrms.attendancecalendar.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import com.FTG2024.hrms.R
import com.FTG2024.hrms.databinding.FragmentDateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar
import java.util.Locale


class DateBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDateBottomSheetBinding
    private lateinit var monthPicker: NumberPicker
    private lateinit var yearPicker: NumberPicker

    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDateBottomSheetBinding.inflate(layoutInflater, container, false)
        monthPicker = binding.monthPickerFragmentDateBottomSheet
        yearPicker = binding.yearPickerFragmentDateBottomSheet
        setupPickers()
        setListener()
        return binding.root
    }

    private fun setListener() {
        binding.submitButton.setOnClickListener {
            val month = monthPicker.value - 1
            val year = yearPicker.value
            onDateSelectedListener?.onDateSelected(month, year)
            dismiss()
        }
    }
    private fun setupPickers() {
        val months = resources.getStringArray(R.array.months)
        monthPicker.minValue = 1
        monthPicker.maxValue = months.size
        monthPicker.displayedValues = months
        monthPicker.value = currentMonth

        yearPicker.minValue = 2000
        yearPicker.maxValue = currentYear
        yearPicker.value = currentYear
    }

    public interface OnDateSelectedListener {
        fun onDateSelected(month: Int, year: Int)
    }

    private fun getMonthValue(month: String): Int {
        return when (month.lowercase(Locale.ROOT)) { // Ensure case-insensitive matching
            "january" -> 0
            "february" -> 1
            "march" -> 2
            "april" -> 3
            "may" -> 4
            "june" -> 5
            "july" -> 6
            "august" -> 7
            "september" -> 8
            "october" -> 9
            "november" -> 10
            "december" -> 11
            else -> -1 // Return -1 for invalid month names
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(listener: OnDateSelectedListener): DateBottomSheetFragment {
            val fragment = DateBottomSheetFragment()
            fragment.onDateSelectedListener = listener
            return fragment
        }
    }

    private var onDateSelectedListener: OnDateSelectedListener? = null
}