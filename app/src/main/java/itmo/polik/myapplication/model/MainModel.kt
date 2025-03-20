package itmo.polik.myapplication.model

import android.content.Context
import android.content.SharedPreferences

class MainModel(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE)

    fun saveResult(resultText: String) {
        val editor = sharedPreferences.edit()
        editor.putString("resultText", resultText)
        editor.apply()
    }

    fun loadResult(): String {
        return sharedPreferences.getString("resultText", "") ?: ""
    }

    fun saveResultVisibility(isVisible: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("resultVisibility", isVisible)
        editor.apply()
    }

    fun loadResultVisibility(): Boolean {
        return sharedPreferences.getBoolean("resultVisibility", false)
    }
}
