package itmo.polik.myapplication.model

import java.io.Serializable

class ApiResponse(
    val statusCode: Int,
    val input: AstroRequest,
    val output: List<Map<String, PlanetData>>
): Serializable
