package com.FTG2024.hrms.leaves.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.attendancecalendar.adapter.DatesAdapter
import com.FTG2024.hrms.leaves.model.LeavesApproved

class LeavesApprovedAdapter(val dataList : List<LeavesApproved>) :
    RecyclerView.Adapter<LeavesApprovedAdapter.LeavesApprovedViewHolder>() {

    class LeavesApprovedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val leavesTypeTextView: TextView = itemView.findViewById(R.id.textView_item_leaves_type)
            val leavesDateTextView: TextView = itemView.findViewById(R.id.textView_item_leaves_date)
            val leavesAppliedOnTextView: TextView = itemView.findViewById(R.id.textView_item_leaves_applied_on)
            val leavesReasonTextView: TextView = itemView.findViewById(R.id.textView_item_leaves_reason)
            val leavesByTextView: TextView = itemView.findViewById(R.id.textview_item_leaves_by)
            val leavesOnTextView: TextView = itemView.findViewById(R.id.textview_item_leaves_on)
            val leavesRemarkTextView: TextView = itemView.findViewById(R.id.textView_item_leaves_remark)
            val labelLeavesByTextView: TextView = itemView.findViewById(R.id.label_item_leaves_by)
            val labelLeavesOnTextView: TextView = itemView.findViewById(R.id.label_item_leaves_on)
            val labelLeavesRemarkTextView: TextView = itemView.findViewById(R.id.label_item_leaves_remark)
            val view : View = itemView.findViewById(R.id.view2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeavesApprovedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaves_status, parent, false)
        return LeavesApprovedViewHolder(view)
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    override fun onBindViewHolder(holder: LeavesApprovedViewHolder, position: Int) {
        holder.labelLeavesOnTextView.visibility = View.VISIBLE
        holder.labelLeavesByTextView.visibility = View.VISIBLE
        holder.labelLeavesRemarkTextView.visibility = View.VISIBLE
        holder.leavesOnTextView.visibility = View.VISIBLE
        holder.leavesByTextView.visibility = View.VISIBLE
        holder.leavesRemarkTextView.visibility = View.VISIBLE
        holder.view.visibility = View.VISIBLE
        holder.labelLeavesOnTextView.text = "Approved On"
        holder.labelLeavesByTextView.text = "Approved By"
        val model = dataList.get(position)
        holder.leavesTypeTextView.text = model.leaveType
        holder.leavesDateTextView.text = model.date
        holder.leavesReasonTextView.text = model.reason
        holder.leavesAppliedOnTextView.text = model.appliedOn
        holder.leavesByTextView.text = model.approvedBy
        holder.leavesOnTextView.text = model.approvedOn
        holder.leavesRemarkTextView.text = model.remark
    }
}