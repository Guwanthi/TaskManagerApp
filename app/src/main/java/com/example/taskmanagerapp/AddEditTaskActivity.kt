package com.example.taskmanagerapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.taskmanagerapp.databinding.ActivityAddEditTaskBinding
import com.example.taskmanagerapp.model.Priority
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var selectedDate: Date? = null
    private var taskId: Int? = null
    private var isEditMode = false
    private var existingTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if editing an existing task
        taskId = intent.getIntExtra("task_id", -1)
        if (taskId != -1) {
            isEditMode = true
            loadTask(taskId!!)
        }

        // Handle deadline selection
        binding.textViewDeadline.setOnClickListener {
            showDatePicker()
        }

        // Handle Save button click
        binding.buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun loadTask(id: Int) {
        taskViewModel.getTaskById(id).observe(this, Observer { task ->
            task?.let {
                existingTask = it
                binding.editTextName.setText(it.name)
                binding.editTextDescription.setText(it.description)
                when (it.priority) {
                    Priority.LOW -> binding.radioLow.isChecked = true
                    Priority.MEDIUM -> binding.radioMedium.isChecked = true
                    Priority.HIGH -> binding.radioHigh.isChecked = true
                }
                selectedDate = it.deadline
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.textViewDeadline.text = formatter.format(it.deadline)
            }
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedDate?.let { calendar.time = it }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            selectedDate = calendar.time
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.textViewDeadline.text = formatter.format(selectedDate!!)
        }, year, month, day)

        datePicker.show()
    }

    private fun saveTask() {
        val name = binding.editTextName.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val priority = when {
            binding.radioLow.isChecked -> Priority.LOW
            binding.radioMedium.isChecked -> Priority.MEDIUM
            binding.radioHigh.isChecked -> Priority.HIGH
            else -> Priority.LOW
        }

        if (TextUtils.isEmpty(name)) {
            binding.textInputLayoutName.error = "Task name is required"
            return
        }

        if (selectedDate == null) {
            Toast.makeText(this, "Please select a deadline", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(
            id = existingTask?.id ?: 0,
            name = name,
            description = description,
            priority = priority,
            deadline = selectedDate!!
        )

        if (isEditMode) {
            taskViewModel.update(task)
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
        } else {
            taskViewModel.insert(task)
            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
