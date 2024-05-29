package com.FTG2024.hrms.customers

import com.FTG2024.hrms.customers.dialogs.EditServiceInterestDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.customers.dialogs.ChangeStatusDialog
import java.text.SimpleDateFormat
import java.util.Locale


class CustomerServiceInterestAdapter(private var items: List<ServiceInterest>) :
    RecyclerView.Adapter<CustomerServiceInterestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.serviceNameEditTextView)
        val amount: TextView = itemView.findViewById(R.id.amountEditText)
        val description: TextView = itemView.findViewById(R.id.descriptionEdittext)
        val date: TextView = itemView.findViewById(R.id.DateTextView)
        val editButton: TextView = itemView.findViewById(R.id.openEditDialog)
        val ChangeButton: TextView = itemView.findViewById(R.id.chngeStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_service_intrest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val date = inputFormat.parse(item.interestedDatetime)
        val formattedDate = if (date != null) outputFormat.format(date) else ""

        holder.serviceName.text = item.serviceName
        holder.amount.text = "RS.${item.amount}"
        holder.description.text = item.description
        holder.date.text = formattedDate


        holder.ChangeButton.setOnClickListener {
            Log.d("ServiceInterestAdapter change", item.id.toString())
            Log.d("ServiceInterestAdapter change", item.id.toString())
             ChangeStatusDialog( holder.itemView.context, item).show()
        }

        holder.editButton.setOnClickListener {
            Log.d("ServiceInterestAdapter", item.id.toString())
            EditServiceInterestDialog(holder.itemView.context, item).show()
        }

        holder.amount.visibility = if (item.status == 0) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount() = items.size

    fun getItem(position: Int): ServiceInterest {
        return items[position]
    }

    fun setItems(newItems: List<ServiceInterest>) {
        items = newItems
        notifyDataSetChanged()
    }
}