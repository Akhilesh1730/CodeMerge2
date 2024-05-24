package com.FTG2024.hrms.leaves.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.leaves.model.LeavesApproval
import org.w3c.dom.Text

class LeavesApprovalAdapter(private val isPending : Boolean, private val dataList : List<LeavesApproval>) : RecyclerView.Adapter<LeavesApprovalAdapter.LeavesApprovalViewHolder>()  {

    class LeavesApprovalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLeavesApprovalName : TextView = itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_name)
        val textViewLeavesApprovalDate : TextView = itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_date)
        val textViewLeavesApprovalType : TextView = itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_leave_type)
        val textViewLeavesApprovalAppliedOn : TextView = itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_applied_on)
        val textViewLeavesApprovalRemark : TextView = itemView.findViewById<TextView>(R.id.textview_item_leaves_approval_remark)
        val labelLeavesApprovalRemark : TextView = itemView.findViewById<TextView>(R.id.label_item_leaves_approval_remark)
        val textViewLeavesApprovalStatus : TextView = itemView.findViewById<TextView>(R.id.textView_item_leaves_approval_status)
        val editTextLeavesApprovalRemark : TextView = itemView.findViewById<TextView>(R.id.edittext_item_leaves_approval_remark)
        val buttonApprove : Button = itemView.findViewById(R.id.button_item_leaves_approval_approve)
        val buttonReject : Button = itemView.findViewById(R.id.button_item_leaves_approval_reject)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeavesApprovalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_leaves_approval, parent, false)
        return LeavesApprovalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    override fun onBindViewHolder(holder: LeavesApprovalViewHolder, position: Int) {
        val model = dataList.get(position)
        holder.textViewLeavesApprovalName.text = model.name
        holder.textViewLeavesApprovalDate.text = model.date
        holder.textViewLeavesApprovalType.text = model.leaveType
        holder.textViewLeavesApprovalAppliedOn.text = model.appliedOn
        holder.textViewLeavesApprovalRemark.text = model.remark
        holder.textViewLeavesApprovalStatus.text = model.status

        if (isPending) {
            holder.textViewLeavesApprovalRemark.visibility = View.GONE
            holder.textViewLeavesApprovalStatus.visibility = View.GONE
            holder.labelLeavesApprovalRemark.visibility = View.GONE
            holder.editTextLeavesApprovalRemark.visibility = View.VISIBLE
            holder.buttonApprove.visibility = View.VISIBLE
            holder.buttonReject.visibility = View.VISIBLE
        } else {

        }
    }
}