package com.example.emsapp.attendancecalendar.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.example.emsapp.R
import com.example.emsapp.databinding.FragmentDateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar


class DateBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDateBottomSheetBinding
    private lateinit var monthPicker: NumberPicker
    private lateinit var yearPicker: NumberPicker

    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDateBottomSheetBinding.inflate(layoutInflater, container, false)
        monthPicker = binding.monthPickerFragmentDateBottomSheet
        yearPicker = binding.yearPickerFragmentDateBottomSheet
        setupPickers()
        return binding.root
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

    companion object {
        @JvmStatic
        fun newInstance() =
            DateBottomSheetFragment()
    }
}