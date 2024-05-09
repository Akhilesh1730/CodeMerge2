package com.example.emsapp.attendancecalendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emsapp.R
import com.example.emsapp.attendancecalendar.model.AttendanceDateModel

class DatesAdapter(val listOfAttendance : MutableList<AttendanceDateModel>) : RecyclerView.Adapter<DatesAdapter.DateViewHolder>() {
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
        holder.dateTextView.text = model.date
        holder.dayInTextView.text = model.daysIn
        holder.dayOutTextView.text = model.daysOut
    }
}