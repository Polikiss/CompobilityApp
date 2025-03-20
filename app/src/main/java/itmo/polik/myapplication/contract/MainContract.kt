package itmo.polik.myapplication.contract

interface MainContract {

    interface View {
        fun updateResult(text: String)
        fun showResult(isVisible: Boolean)
    }

    interface Presenter {
        fun onButtonClicked(inputText: String)
        fun saveConfiguration(labelText: String)
        fun loadConfiguration()
        fun calculateCompatibilityInput(yourBirthDate: String, theirBirthDate: String, yourTimeOfBirth: String, theirTimeOfBirth: String)
    }
}