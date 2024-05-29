import com.google.gson.annotations.SerializedName



data class ServiceInterest(
    val ID: Int,
    val customerId: Int,
    val serviceId: Int,
    val AMOUNT: Double,
    val DESCRIPTION: String,
    val STATUS: Int,
    val interestedDatetime: String,
    val serviceName: String,
    val createdModifiedDate: String,
    val customerName: String,
    val IS_CONVERTED: String,


)
