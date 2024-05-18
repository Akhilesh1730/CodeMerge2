package com.FTG2024.hrms.attendancecalendar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.application.TokenManager
import com.FTG2024.hrms.application.TokenManagerImpl
import com.FTG2024.hrms.attendancecalendar.adapter.DatesAdapter
import com.FTG2024.hrms.attendancecalendar.adapter.DayAdapter
import com.FTG2024.hrms.attendancecalendar.fragment.DateBottomSheetFragment
import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarRequest
import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarResponse
import com.FTG2024.hrms.attendancecalendar.model.AttendanceDateModel
import com.FTG2024.hrms.attendancecalendar.model.Data
import com.FTG2024.hrms.attendancecalendar.model.Holiday
import com.FTG2024.hrms.attendancecalendar.repo.AttendanceApiService
import com.FTG2024.hrms.attendancecalendar.repo.AttendanceRepository
import com.FTG2024.hrms.attendancecalendar.viewmodel.AttendanceViewModel
import com.FTG2024.hrms.attendancecalendar.viewmodel.AttendanceViewModelFactory
import com.FTG2024.hrms.base.BaseActivity
import com.FTG2024.hrms.databinding.ActivityAttendanceCalendarBinding
import com.FTG2024.hrms.dialog.ProgressDialog
import com.FTG2024.hrms.retrofit.RetrofitHelper
import com.FTG2024.hrms.uidata.Response
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class AttendanceCalendarActivity : BaseActivity() {
    private lateinit var binding: ActivityAttendanceCalendarBinding
    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var dateRecyclerView: RecyclerView
    private lateinit var selectTimeButton : ImageView
    private lateinit var selectedTimeText : TextView
    private lateinit var viewModel: AttendanceViewModel
    private lateinit var retrofitHelper : Retrofit
    private lateinit var tokenManager: TokenManager
    private lateinit var progressDialog : ProgressDialog
    private var month: Int = 0
    private var year: Int = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceCalendarBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)
        val empID = getEmployeeData().get(0).UserData.get(0).EMP_ID
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        progressDialog = ProgressDialog(this, "Loading Data")
        progressDialog.show()
        tokenManager = TokenManagerImpl(getSharedPreferences("user_prefs", MODE_PRIVATE))
        retrofitHelper = RetrofitHelper.getRetrofitInstance(tokenManager)
        viewModel = ViewModelProvider(this, AttendanceViewModelFactory(AttendanceRepository(retrofitHelper.create(AttendanceApiService::class.java))))
            .get(AttendanceViewModel::class.java)
        selectTimeButton = binding.buttonAttendanceCalcSelectTime
        selectedTimeText = binding.textviewAttendanceCalcSelectedTime
        selectTimeButton.setOnClickListener {
            val bottomSheetFragment = DateBottomSheetFragment.newInstance(object : DateBottomSheetFragment.OnDateSelectedListener {
                override fun onDateSelected(selectedMonth: Int, selectedYear: Int) {
                    month = selectedMonth
                    year = selectedYear
                    progressDialog.show()
                    viewModel.getAttendanceCalendarData(AttendanceCalendarRequest(empID, formatDateToString(selectedMonth + 1), selectedYear.toString()))
                }
            })
            bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetTheme)
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        setDayRecyclerView()
        setObservers()
        month = getCurrentMonth()
        year = getCurrentYear()
        viewModel.getAttendanceCalendarData(AttendanceCalendarRequest(empID, formatDateToString(month + 1), year.toString()))
    }

    private fun setObservers() {
        viewModel.getAttendanceCalendarLiveData().observe(this, Observer {
            it.getContentIfNotHandled().let {response ->
                when(response) {
                    is Response.Success -> {
                        val attendanceCalendarResponse = response.data as AttendanceCalendarResponse
                        Log.d("####", "setObservers: ${attendanceCalendarResponse.data}")
                        if (attendanceCalendarResponse.data.isNullOrEmpty()) {
                            setDatesRecyclerView(null, listOf(), month, year)
                        } else {
                            val attendanceData = attendanceCalendarResponse.data[0]
                            extractData(attendanceData)
                            setDatesRecyclerView(attendanceData, attendanceCalendarResponse.holidays,month, year)
                        }
                        progressDialog.dismiss()
                    }
                    is Response.Exception -> {
                        progressDialog.dismiss()
                        showToast("Failed")
                    }
                    else -> return@Observer
                }
            }
        })
    }

    private fun extractData(data: Data) {

    }

    private fun formatDateToString(month : Int) : String {
        return String.format(Locale.US, "%02d", month)
    }

    private fun getCurrentMonth() : Int {
        return  Calendar.getInstance().get(Calendar.MONTH)
    }

    private fun getCurrentYear() : Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }
    private fun setDayRecyclerView() {
        dayRecyclerView = binding.recyclycerviewCalendarDays
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val adapter = DayAdapter(dayNames)
        val gridLayoutManager = GridLayoutManager(this, 7)
        dayRecyclerView.layoutManager = gridLayoutManager
        dayRecyclerView.adapter = adapter
    }

    private fun setDatesRecyclerView(data: Data?, holiday: List<Holiday>, month: Int, year: Int) {
        dateRecyclerView = binding.recyclycerviewCalendarDates
        binding.textviewAttendanceCalcSelectedTime.text = getMontName(month) + " " + year.toString()
        val dayOfWeekValue = getDayOfWeekForFirstOfMonth(month, year)
        val daysInMonth = getNumberOfDaysInMonth(month, year)
        val tot = daysInMonth + dayOfWeekValue - 1
        var currentValue = 0
        var listOfDates = mutableListOf<AttendanceDateModel>()
        var holidaySet = mutableSetOf<Int>()
        if (holiday.isNotEmpty()) {
            holidaySet = convertToSet(holiday)
        }
        if (data == null) {
            for (i in 0 until tot) {
                if (i < dayOfWeekValue - 1) {
                    listOfDates.add(AttendanceDateModel("00:00", "00:00", "","empty"))
                } else {
                    currentValue++
                    listOfDates.add(AttendanceDateModel("", "", currentValue.toString(), "empty"))
                }
            }
        } else {
            for (i in 0 until tot) {
                if (i < dayOfWeekValue - 1) {
                    listOfDates.add(AttendanceDateModel("00:00", "00:00", "","empty"))
                } else {
                    currentValue++
                    Log.d("####", "setDatesRecyclerView: $currentValue $tot")
                    val pair = monthsData(data, currentValue)
                    if (pair?.first != null || pair?.second != null) {
                        val secondValue = pair.second ?: ""
                        listOfDates.add(AttendanceDateModel(pair!!.first, secondValue, currentValue.toString(), "present"))
                    } else if (holidaySet.contains(currentValue) && holidaySet.isNotEmpty()) {
                        listOfDates.add(AttendanceDateModel("", "", currentValue.toString(), "holiday"))
                    } else if (isSunday(currentValue, month + 1, year)) {
                        listOfDates.add(AttendanceDateModel("", "", currentValue.toString(), "holiday"))
                    } else if (month < getCurrentMonth() && pair?.first.isNullOrEmpty() && pair?.second.isNullOrEmpty()) {
                        listOfDates.add(AttendanceDateModel("", "", currentValue.toString(), "absent"))
                    } else if (pair?.first.isNullOrEmpty() && pair?.second.isNullOrEmpty() && currentValue < getCurrentDayOfMonth()) {
                        listOfDates.add(AttendanceDateModel("", "", currentValue.toString(), "absent"))
                    } else {
                        listOfDates.add(AttendanceDateModel("", "", currentValue.toString(), "empty"))
                    }
                }
            }
        }


        var dateValue = 1


        val adapter = DatesAdapter(listOfDates, this)
        val gridLayoutManager = GridLayoutManager(this, 7)
        dateRecyclerView.layoutManager = gridLayoutManager
        dateRecyclerView.adapter = adapter
    }

    fun getDay(date : String): Int {
        return LocalDate.parse(date).dayOfMonth
    }

    fun getCurrentDayOfMonth() : Int {
        val currentDate = LocalDate.now()
        return currentDate.dayOfMonth
    }
    private fun convertToSet(holiday: List<Holiday>?): MutableSet<Int> {
        var holidaySet = mutableSetOf<Int>()
        for (s in holiday!!) {
            holidaySet.add(getDay(s.DATE))
        }
        return holidaySet
    }
    fun isSunday(day : Int, month : Int, year: Int) : Boolean {
        return LocalDate.of(year, month, day).dayOfWeek == DayOfWeek.SUNDAY
    }
    private fun getMontName(month: Int): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
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

    private fun monthsData(data: Data?, day: Int) : Pair<String, String>? {
        if (data == null) return null
        return when (day) {
            1 -> Pair(data.DAYIN_1, data.DAYOUT_1)
            2 -> Pair(data.DAYIN_2, data.DAYOUT_2)
            3 -> Pair(data.DAYIN_3, data.DAYOUT_3)
            4 -> Pair(data.DAYIN_4, data.DAYOUT_4)
            5 -> Pair(data.DAYIN_5, data.DAYOUT_5)
            6 -> Pair(data.DAYIN_6, data.DAYOUT_6)
            7 -> Pair(data.DAYIN_7, data.DAYOUT_7)
            8 -> Pair(data.DAYIN_8, data.DAYOUT_8)
            9 -> Pair(data.DAYIN_9, data.DAYOUT_9)
            10 -> Pair(data.DAYIN_10, data.DAYOUT_10)
            11 -> Pair(data.DAYIN_11, data.DAYOUT_11)
            12 -> Pair(data.DAYIN_12, data.DAYOUT_12)
            13 -> Pair(data.DAYIN_13, data.DAYOUT_13)
            14 -> Pair(data.DAYIN_14, data.DAYOUT_14)
            15 -> Pair(data.DAYIN_15, data.DAYOUT_15)
            16 -> Pair(data.DAYIN_16, data.DAYOUT_16)
            17 -> Pair(data.DAYIN_17, data.DAYOUT_17)
            18 -> Pair(data.DAYIN_18, data.DAYOUT_18)
            19 -> Pair(data.DAYIN_19, data.DAYOUT_19)
            20 -> Pair(data.DAYIN_20, data.DAYOUT_20)
            21 -> Pair(data.DAYIN_21, data.DAYOUT_21)
            22 -> Pair(data.DAYIN_22, data.DAYOUT_22)
            23 -> Pair(data.DAYIN_23, data.DAYOUT_23)
            24 -> Pair(data.DAYIN_24, data.DAYOUT_24)
            25 -> Pair(data.DAYIN_25, data.DAYOUT_25)
            26 -> Pair(data.DAYIN_26, data.DAYOUT_26)
            27 -> Pair(data.DAYIN_27, data.DAYOUT_27)
            28 -> Pair(data.DAYIN_28, data.DAYOUT_28)
            29 -> Pair(data.DAYIN_29, data.DAYOUT_29)
            30 -> Pair(data.DAYIN_30, data.DAYOUT_30)
            31 -> Pair(data.DAYIN_31, data.DAYOUT_31)
            else -> throw IllegalArgumentException("Invalid day: $day")
        }
    }

    private fun getEmployeeData() : List<com.FTG2024.hrms.login.model.Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)
        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<com.FTG2024.hrms.login.model.Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<com.FTG2024.hrms.login.model.Data>>() {}.type)
            Log.d("#####", "getEmployeeData: ${dataList[0].UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }
}