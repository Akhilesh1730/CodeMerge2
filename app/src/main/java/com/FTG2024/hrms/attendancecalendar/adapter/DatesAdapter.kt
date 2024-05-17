package com.FTG2024.hrms.attendancecalendar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.attendancecalendar.model.AttendanceDateModel

class DatesAdapter(val listOfAttendance : MutableList<AttendanceDateModel>, val context: Context) : RecyclerView.Adapter<DatesAdapter.DateViewHolder>() {
    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayInTextView: TextView = itemView.findViewById(R.id.textview_datetime_dayin)
        val dayOutTextView: TextView = itemView.findViewById(R.id.textview_datetime_dayout)
        val dateTextView : TextView= itemView.findViewById(R.id.textview_datetime_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
        return DateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfAttendance.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val model = listOfAttendance[position]
        if (model.date == "") {
            holder.dateTextView.setBackgroundResource(R.drawable.shape_attendance_calc_label_blank)
            holder.dayInTextView.visibility = View.GONE
            holder.dayOutTextView.visibility = View.GONE
        } else {
            holder.dateTextView.text = model.date
            if (model.status == "empty") {
                holder.dateTextView.setBackgroundResource(R.drawable.shape_attendance_calc_label_blank)
                holder.dateTextView.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
            } else if (model.status == "holiday") {
                holder.dateTextView.setBackgroundResource(R.drawable.shape_attendance_calc_label_holiday)
            } else if (model.status == "absent") {
                holder.dateTextView.setBackgroundResource(R.drawable.shape_attendance_calc_label_absent)
            }
            else {
                holder.dateTextView.setBackgroundResource(R.drawable.shape_attendance_calc_label_present)
                holder.dayInTextView.text = model.daysIn
                holder.dayOutTextView.text = model.daysOut
            }

        }
    }
}