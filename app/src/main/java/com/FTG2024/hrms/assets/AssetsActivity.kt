package com.FTG2024.hrms.assets

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.FTG2024.hrms.R
import com.FTG2024.hrms.assets.fragments.fragment_approved
import com.FTG2024.hrms.assets.fragments.fragment_pending
import com.FTG2024.hrms.assets.fragments.fragment_rejected
import com.FTG2024.hrms.login.model.Data
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AssetsActivity : AppCompatActivity() {
    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView
    private lateinit var tabThree: TextView
    private lateinit var fabButton: FloatingActionButton
    private var selectedTabNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_assets)
       /* WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, findViewById(R.id.main)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }*/
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.orange) // Replace with your desired orange color resource
            }
            insets
        }
        tabOne = findViewById(R.id.tabone)
        tabTwo = findViewById(R.id.tabTwo)
        tabThree = findViewById(R.id.tabThree)
        fabButton = findViewById(R.id.fab)

        fabButton.setOnClickListener{

            val customDialog = CustomDialog(this, this)
            customDialog.show()

        }
        tabOne.setOnClickListener { selectTab(1) }
        tabTwo.setOnClickListener { selectTab(2) }
        tabThree.setOnClickListener { selectTab(3) }

        selectTab(selectedTabNumber)
    }

    private fun selectTab(tabNumber: Int) {
        selectedTabNumber = tabNumber


        val whiteColor = ContextCompat.getColor(this, R.color.white)

        tabOne.setBackgroundResource(if (tabNumber == 1) R.drawable.active_tabbar_background else R.drawable.tabbar_background)
        tabOne.setTextColor(if (tabNumber == 1) whiteColor else ContextCompat.getColor(this, R.color.black))

        tabTwo.setBackgroundResource(if (tabNumber == 2) R.drawable.active_tabbar_background else R.drawable.tabbar_background)
        tabTwo.setTextColor(if (tabNumber == 2) whiteColor else ContextCompat.getColor(this, R.color.black))

        tabThree.setBackgroundResource(if (tabNumber == 3) R.drawable.active_tabbar_background else R.drawable.tabbar_background)
        tabThree.setTextColor(if (tabNumber == 3) whiteColor else ContextCompat.getColor(this, R.color.black))

        val fragment = when (tabNumber) {
            1 -> fragment_pending()
            2 -> fragment_approved()
            3 -> fragment_rejected()
            else -> fragment_pending()
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .commit()
        }
    }
    private fun getEmployeeData() : List<Data> {
        val sharedPref = getSharedPreferences("employee_detail_pref", Context.MODE_PRIVATE)
        val dataListJson = sharedPref.getString("employeeDataListKey", null)

        if (dataListJson != null) {
            val gson = Gson()
            val dataList: List<Data> =
                gson.fromJson(dataListJson, object : TypeToken<List<Data>>() {}.type)
            val user = dataList.get(0).UserData[0]

            Log.d("###", "g.etEmployeeData: ${dataList.get(0).UserData.get(0).EMP_ID}")
            return dataList
        }
        return listOf()
    }
}