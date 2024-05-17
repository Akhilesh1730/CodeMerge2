import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.FTG2024.hrms.R
import com.FTG2024.hrms.customers.model.Data
import java.text.SimpleDateFormat
import java.util.Locale

class CustomerAdapter : ListAdapter<Data, CustomerAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerNameTextView: TextView = itemView.findViewById(R.id.customerNameTextView)
        val customerDateTextView: TextView = itemView.findViewById(R.id.DateTextView)
        val customerPhoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val customertitleTextView: TextView = itemView.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val currentItem = getItem(position)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val parsedDate = if (!currentItem.CREATED_MODIFIED_DATE.isNullOrEmpty()) {
            dateFormat.parse(currentItem.CREATED_MODIFIED_DATE)
        } else {
            null
        }

        val formattedDate = if (parsedDate != null) {
            val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            dateFormatter.format(parsedDate)
        } else {
            ""
        }




        holder.customerNameTextView.text = "${currentItem.FIRST_NAME} ${currentItem.LAST_NAME}"
        holder.customerPhoneTextView.text = "+91 ${currentItem.MOBILE_NO}"
        holder.customertitleTextView.text =  "Title ${currentItem.ADDRESS}"
        holder.customerDateTextView.text = formattedDate
        holder.customerPhoneTextView.setOnClickListener {
            val phoneNumber = "+91 ${currentItem.MOBILE_NO}"
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            it.context.startActivity(intent)
        }

    }

}

class CustomerDiffCallback : DiffUtil.ItemCallback<Data>() {
    override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem.ID == newItem.ID
    }

    override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
        return oldItem == newItem
    }
}