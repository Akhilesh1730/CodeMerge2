package com.FTG2024.hrms.leaves.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.attendancecalendar.model.AttendenceEmployeeModel
import com.FTG2024.hrms.leaves.model.LeavesApproval

class LeavesApprovalRejectedApprovedAdapter(private val dataList: MutableList<LeavesApproval>)
    : RecyclerView.Adapter<LeavesApprovalRejectedApprovedAdapter.LeavesApprovalRejectedViewHolder>() {
    class LeavesApprovalRejectedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLeavesApprovalName: TextView =
            itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_name)
        val textViewLeavesApprovalDate: TextView =
            itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_date)
        val textViewLeavesApprovalType: TextView =
            itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_leave_type)
        val textViewLeavesApprovalAppliedOn: TextView =
            itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_applied_on)
        val textViewLeavesApprovalRemark: TextView =
            itemView.findViewById<TextView>(R.id.textview_item_leaves_approval_remark)
        val labelLeavesApprovalRemark: TextView =
            itemView.findViewById<TextView>(R.id.label_item_leaves_approval_remark)
        val textViewLeavesApprovalStatus: TextView =
            itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_status)
        val textViewLeavesApprovalReason: TextView =
            itemView.findViewById<TextView>(R.id.textview_item_leaves_approval_leave_reason)
        val editTextLeavesApprovalRemark: TextView =
            itemView.findViewById<TextView>(R.id.edittext_item_leaves_approval_remark)
        val buttonApprove: TextView =
            itemView.findViewById(R.id.button_item_leaves_approval_approve)
        val buttonReject: TextView = itemView.findViewById(R.id.button_item_leaves_approval_reject)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeavesApprovalRejectedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaves_approval, parent, false)
        return LeavesApprovalRejectedViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: LeavesApprovalRejectedViewHolder, position: Int) {
        val model = dataList.get(position)
        holder.textViewLeavesApprovalName.text = model.name
        holder.textViewLeavesApprovalDate.text = model.date
        holder.textViewLeavesApprovalType.text = model.leaveType
        holder.textViewLeavesApprovalAppliedOn.text = model.appliedOn
        holder.textViewLeavesApprovalRemark.text = model.remark
        holder.textViewLeavesApprovalStatus.text = model.status
        holder.textViewLeavesApprovalReason.text = model.reason
    }
}