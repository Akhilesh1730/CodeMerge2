package com.example.emsapp.attendancecalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emsapp.R
import com.example.emsapp.attendancecalendar.adapter.DatesAdapter
import com.example.emsapp.attendancecalendar.adapter.DayAdapter
import com.example.emsapp.attendancecalendar.fragment.DateBottomSheetFragment
import com.example.emsapp.attendancecalendar.model.AttendanceDateModel
import com.example.emsapp.databinding.ActivityAttendanceCalendarBinding
import java.util.Calendar

class AttendanceCalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceCalendarBinding
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var selectTimeButton : ImageView
    private lateinit var selectedTimeText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceCalendarBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)
        selectTimeButton = binding.buttonAttendanceCalcSelectTime
        selectedTimeText = binding.textviewAttendanceCalcSelectedTime
        selectTimeButton.setOnClickListener {
            val bottomSheetFragment = DateBottomSheetFragment.newInstance()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        setDayRecyclerView()
        setDatesRecyclerView()
    }

    private fun setDayRecyclerView() {
        dayRecyclerView = binding.recyclycerviewCalendarDays
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val adapter = DayAdapter(dayNames)
        val gridLayoutManager = GridLayoutManager(this, 7)
        dayRecyclerView.layoutManager = gridLayoutManager
        dayRecyclerView.adapter = adapter
    }

    private fun setDatesRecyclerView() {
        dateRecyclerView = binding.recyclycerviewCalendarDates
        val month = Calendar.JANUARY
        val year = 2024
        val dayOfWeekValue = getDayOfWeekForFirstOfMonth(month, year)
        val daysInMonth = getNumberOfDaysInMonth(month, year)
        var listOfDates = mutableListOf<AttendanceDateModel>()
        val tot = daysInMonth + dayOfWeekValue - 1
        var currentValue = 0
        for (i in 0 until tot) {
            if (i < dayOfWeekValue - 1) {
                listOfDates.add(AttendanceDateModel("00:00", "00:00", ""))
            } else {
                currentValue++
                listOfDates.add(AttendanceDateModel("00:00", "00:00", currentValue.toString()))
            }
        }

        val adapter = DatesAdapter(listOfDates)
        val gridLayoutManager = GridLayoutManager(this, 7)
        dateRecyclerView.layoutManager = gridLayoutManager
        dateRecyclerView.adapter = adapter
    }

    private fun getDayOfWeekForFirstOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek
    }

    private fun getNumberOfDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month) // Months are 0-indexed in Calendar
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}