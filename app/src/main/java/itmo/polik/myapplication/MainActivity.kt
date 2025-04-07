package itmo.polik.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import itmo.polik.myapplication.contract.MainContract
import itmo.polik.myapplication.databinding.ActivityMainBinding
import itmo.polik.myapplication.presenter.MainPresenter
import java.text.SimpleDateFormat
import java.util.* // Import Calendar and Locale

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainContract.Presenter

    // Calendars to store selected date/time
    private val yourCalendar = Calendar.getInstance()
    private val theirCalendar = Calendar.getInstance()

    // Formatters
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = MainPresenter(this, this)

        presenter.loadConfiguration()

        setupPickers() // Call function to set up listeners

        binding.findCompatibilityButton.setOnClickListener {
            val yourBirthDate = binding.yourBirthDateInput.text.toString()
            val theirBirthDate = binding.theirBirthDateInput.text.toString()
            val yourTimeOfBirth = binding.yourBirthTimeInput.text.toString()
            val theirTimeOfBirth = binding.theirBirthTimeInput.text.toString()

            // Basic validation: ensure fields are not empty before calculation
            if (yourBirthDate.isEmpty() || theirBirthDate.isEmpty() ||
                yourTimeOfBirth.isEmpty() || theirTimeOfBirth.isEmpty()) {
                updateResult("Пожалуйста, выберите все даты и время.")
                binding.heartProgress.setProgress(0f) // Reset progress
                return@setOnClickListener
            }

            presenter.calculateCompatibilityInput(yourBirthDate, theirBirthDate, yourTimeOfBirth, theirTimeOfBirth)
        }
    }

    private fun setupPickers() {
        // --- Your Pickers ---
        binding.yourBirthDateInput.setOnClickListener {
            showDatePickerDialog(binding.yourBirthDateInput, yourCalendar)
        }

        binding.yourBirthTimeInput.setOnClickListener {
            showTimePickerDialog(binding.yourBirthTimeInput, yourCalendar)
        }

        // --- Their Pickers ---
        binding.theirBirthDateInput.setOnClickListener {
            showDatePickerDialog(binding.theirBirthDateInput, theirCalendar)
        }

        binding.theirBirthTimeInput.setOnClickListener {
            showTimePickerDialog(binding.theirBirthTimeInput, theirCalendar)
        }
    }

    private fun showDatePickerDialog(targetEditText: EditText, calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            targetEditText.setText(dateFormat.format(calendar.time))
        }, year, month, day).show()
    }

    private fun showTimePickerDialog(targetEditText: EditText, calendar: Calendar) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            // Set seconds to 0 explicitly if needed by API
            calendar.set(Calendar.SECOND, 0)
            targetEditText.setText(timeFormat.format(calendar.time))
        }, hour, minute, true).show() // true for 24-hour format
    }


    override fun updateResult(text: String) {
        binding.resultTextView.text = text
        binding.resultTextView.visibility = View.VISIBLE

        // Improved percentage extraction and progress update
        val percent = text.substringAfter("Совместимость: ")
            .substringBefore("%")
            .trim()
            .toFloatOrNull() ?: 0f

        // Ensure percent is within 0-100 range for progress calculation
        val clampedPercent = percent.coerceIn(0f, 100f)
        val progress = clampedPercent / 100f
        println("Updating progress to: $progress (from text: '$text', parsed percent: $percent)")

        binding.heartProgress.animateProgress(progress)
    }


    override fun showResult(isVisible: Boolean) {
        binding.resultTextView.visibility = if (isVisible) View.VISIBLE else View.GONE
        // Optionally update heart progress when loading saved state
        if (isVisible && binding.resultTextView.text.isNotEmpty()) {
            updateResult(binding.resultTextView.text.toString()) // Re-trigger progress update
        } else if (!isVisible) {
            binding.heartProgress.setProgress(0f) // Reset progress if result hidden
        }
    }
}