package com.FTG2024.hrms.attendancecalendar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.attendancecalendar.model.AttendenceEmployeeModel

class EmployeeAdapter (private val dataList: List<AttendenceEmployeeModel>) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>(){
    private var onClickListener: OnClickListener? = null

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textView_item_atteendence_employee_name)
        val idTextView : TextView = itemView.findViewById(R.id.textView_item_atteendence_employee_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendenc_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {

        val model = dataList.get(position)
        Log.d("####", "onBindViewHolder: ")
        holder.nameTextView.text = model.name
        holder.idTextView.text = "Emp. ID. ${model.id}"
        holder.itemView.setOnClickListener {
            onClickListener!!.onClicked(model)
        }
    }

    interface OnClickListener {
        fun onClicked(model: AttendenceEmployeeModel)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}