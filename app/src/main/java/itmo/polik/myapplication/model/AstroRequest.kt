package itmo.polik.myapplication.model

data class AstroRequest(
    val year: Int,
    val month: Int,
    val date: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double,
    val config: Config
)
