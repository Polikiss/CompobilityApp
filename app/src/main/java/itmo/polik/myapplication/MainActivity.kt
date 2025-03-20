package itmo.polik.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import itmo.polik.myapplication.contract.MainContract
import itmo.polik.myapplication.databinding.ActivityMainBinding
import itmo.polik.myapplication.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = MainPresenter(this, this)

        presenter.loadConfiguration()

        binding.findCompatibilityButton.setOnClickListener {
            val yourBirthDate = binding.yourBirthDateInput.text.toString()
            val theirBirthDate = binding.theirBirthDateInput.text.toString()
            val yourTimeOfBirth = binding.yourBirthTimeInput.text.toString() ?: "0:0:0"
            val theirTimeOfBirth = binding.theirBirthTimeInput.text.toString() ?: "0:0:0"

            presenter.calculateCompatibilityInput(yourBirthDate, theirBirthDate, yourTimeOfBirth, theirTimeOfBirth)
        }
    }

    override fun updateResult(text: String) {
        binding.resultTextView.text = text
        binding.resultTextView.visibility = View.VISIBLE

        val percent = text.filter { it.isDigit() }.takeIf { it.isNotEmpty() }?.toFloat() ?: 0f
        val progress = percent / 100
        println(progress)

        binding.heartProgress.animateProgress(progress)
    }


    override fun showResult(isVisible: Boolean) {
        binding.resultTextView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}