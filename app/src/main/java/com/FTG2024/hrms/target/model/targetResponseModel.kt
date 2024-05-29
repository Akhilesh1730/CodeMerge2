import com.FTG2024.hrms.target.model.Data

data class targetResponseModel(
    val code: Int,
    val count: Int,
    val `data`: List<Data>,
    val message: String,
    val pages: Int
)


