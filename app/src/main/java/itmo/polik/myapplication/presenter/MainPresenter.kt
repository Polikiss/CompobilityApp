package itmo.polik.myapplication.presenter
import android.content.Context
import itmo.polik.myapplication.contract.MainContract
import itmo.polik.myapplication.model.AstroRequest
import itmo.polik.myapplication.model.Config
import itmo.polik.myapplication.model.MainModel
import itmo.polik.myapplication.model.PersonAstroData
import itmo.polik.myapplication.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainPresenter(private val view: MainContract.View, private val context: Context) : MainContract.Presenter {

    private val model = MainModel(context)

    override fun onButtonClicked(inputText: String) {
        view.updateResult(inputText)
    }

    override fun saveConfiguration(resultText: String) {
        model.saveResult(resultText)
        model.saveResultVisibility(true)
    }

    override fun loadConfiguration() {
        val resultText = model.loadResult()
        val isVisible = model.loadResultVisibility()
        view.updateResult(resultText)
        view.showResult(isVisible)

    }

    override fun calculateCompatibilityInput(
        yourBirthDate: String,
        theirBirthDate: String,
        yourTimeOfBirth: String,
        theirTimeOfBirth: String
    ) {
        val latitude = 18.933
        val longitude = 72.8166
        val timezone = 3.0
        val seconds = 0
        val config = Config("topocentric", "lahiri")

        val yourDate = validateAndParseDate(yourBirthDate, "вашей даты рождения") ?: return
        val (yourYear, yourMonth, yourDay) = yourDate

        val yourTime = validateAndParseTime(yourTimeOfBirth, "вашего времени рождение") ?: return
        val (yourHours, yourMinutes) = yourTime

        val theirDate = validateAndParseDate(theirBirthDate, "даты рождения другого человка") ?: return
        val (theirYear, theirMonth, theirDay) = theirDate

        val theirTime = validateAndParseTime(theirTimeOfBirth, "времени рождения друого человека") ?: return
        val (theirHours, theirMinutes) = theirTime
        val request1 = AstroRequest(
            yourYear, yourMonth, yourDay, yourHours, yourMinutes, seconds, latitude, longitude, timezone,
            config
        )

        val request2 = AstroRequest(
            theirYear, theirMonth, theirDay, theirHours, theirMinutes, seconds,
            latitude, longitude, timezone,
            config
        )

        calculateCompatibilityFromApi(request1, request2)
    }

    private fun calculateCompatibility(person1: PersonAstroData, person2: PersonAstroData): Double {
        var compatibilityScore = 0.0

        val keyPlanets = listOf("1", "2", "6", "3")


        for (planet in keyPlanets) {
            val planet1 = person1.planets[planet]
            val planet2 = person2.planets[planet]
            println(planet1)
            println(planet2)
            if(planet1 != null && planet2 != null){
                if (planet1.current_sign== planet2.current_sign) {
                    compatibilityScore += 10
                }
            }

        }

        val sun1 = person1.planets["1"]!!
        val moon2 = person2.planets["2"]!!
        if(sun1.normDegree != null && moon2.normDegree != null){
            if (abs(sun1.normDegree - moon2.normDegree) % 120 < 5) {
                compatibilityScore += 5
            }
        }


        val moon1 = person1.planets["2"]!!
        val sun2 = person2.planets["1"]!!
        if (moon1.normDegree != null && sun2.normDegree != null){
            if (abs(moon1.normDegree - sun2.normDegree) % 120 < 5) {
                compatibilityScore += 5
            }
        }

        val mars1 = person1.planets["3"]!!
        val saturn2 = person2.planets["7"]!!
        if(mars1.normDegree != null &&  saturn2.normDegree!= null) {
            if (abs(mars1.normDegree - saturn2.normDegree) % 90 < 5) {
                compatibilityScore -= 5
            }
        }

        for (planet in keyPlanets) {
            if (person2.planets[planet]!!.isRetro == "true") {
                compatibilityScore -= 3
            }
        }

        val maxScore = 100.0
        var currScore = compatibilityScore / maxScore * 100
        val minScore = -100.0
        currScore = min(maxScore, currScore)
        currScore = max(minScore, currScore)

        return currScore
    }

    private fun calculateCompatibilityFromApi(request1: AstroRequest, request2: AstroRequest){
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response1 = RetrofitClient.astroApiService.getPlanets(
                    request1
                )

                val response2 = RetrofitClient.astroApiService.getPlanets(
                    request2
                )

                val person1 = PersonAstroData(response1.output[0])
                val person2 = PersonAstroData(response2.output[0])


                val compatibilityScore = calculateCompatibility(person1, person2)
                println(compatibilityScore)

                val resultText = "Совместимость: ${compatibilityScore.toInt()}%"

                model.saveResult(resultText)
                model.saveResultVisibility(true)

                withContext(Dispatchers.Main) {
                    view.updateResult(resultText)
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                println("Error: $errorBody")
                withContext(Dispatchers.Main) {
                    view.updateResult("Error: $errorBody")
                }
            } catch (e: IOException) {
                println("Network error: ${e.message}")
                withContext(Dispatchers.Main) {
                    view.updateResult("Network error: ${e.message}")
                }
            }
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    withContext(Dispatchers.Main) {
                        view.updateResult("Ошибка: ${e.message}")
                    }
                    println("Error: ${e.message}")
                }
            }
        }
    }

    private fun validateAndParseDate(dateString: String, fieldName: String): Triple<Int, Int, Int>? {
        val dateParts = dateString.split("-")
        if (dateParts.size != 3) {
            view.updateResult("Некорректный формат даты в поле $fieldName. Ожидается формат ГГГГ-ММ-ДД.")
            return null
        }

        val year = dateParts[0].toIntOrNull() ?: run {
            view.updateResult("Некорректный год в поле $fieldName.")
            return null
        }

        val month = dateParts[1].toIntOrNull() ?: run {
            view.updateResult("Некорректный месяц в поле $fieldName.")
            return null
        }

        val day = dateParts[2].toIntOrNull() ?: run {
            view.updateResult("Некорректный день в поле $fieldName.")
            return null
        }

        if (month !in 1..12) {
            view.updateResult("Месяц должен быть от 1 до 12 в поле $fieldName.")
            return null
        }
        if (day !in 1..31) {
            view.updateResult("День должен быть от 1 до 31 в поле $fieldName.")
            return null
        }

        return Triple(year, month, day)
    }

    private fun validateAndParseTime(timeString: String, fieldName: String): Pair<Int, Int>? {
        val timeParts = timeString.split(":")
        if (timeParts.size != 2) {
            view.updateResult("Некорректный формат времени в поле $fieldName. Ожидается формат ЧЧ:ММ.")
            return null
        }

        val hours = timeParts[0].toIntOrNull() ?: run {
            view.updateResult("Некорректные часы в поле $fieldName.")
            return null
        }

        val minutes = timeParts[1].toIntOrNull() ?: run {
            view.updateResult("Некорректные минуты в поле $fieldName.")
            return null
        }

        if (hours !in 0..23) {
            view.updateResult("Часы должны быть от 0 до 23 в поле $fieldName.")
            return null
        }

        if (minutes !in 0..59) {
            view.updateResult("Минуты должны быть от 0 до 59 в поле $fieldName.")
            return null
        }

        return Pair(hours, minutes)
    }
}