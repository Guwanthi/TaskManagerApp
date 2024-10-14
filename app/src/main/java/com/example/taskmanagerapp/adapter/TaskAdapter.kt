package com.example.taskmanagerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.databinding.ItemTaskBinding
import com.example.taskmanagerapp.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private var tasks: List<Task>, // Make this mutable to allow modifications
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder class using ViewBinding
    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.textViewTaskName.text = task.name
            binding.textViewTaskDescription.text = task.description
            binding.textViewPriority.text = "Priority: ${task.priority}"
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.textViewDeadline.text = "Deadline: ${formatter.format(task.deadline)}"

            // Handle item click
            binding.root.setOnClickListener {
                onItemClick(task)
            }
        }
    }

    // Inflate item layout and create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    // Return item count
    override fun getItemCount(): Int = tasks.size

    // Update the task list
    fun setTasks(newTasks: List<Task>) {
        tasks = newTasks.toMutableList() // Convert to mutable list
        notifyDataSetChanged()
    }

    // Function to delete a task and notify the adapter
//    fun deleteTask(task: Task) {
//        tasks.remove(task)
//        notifyDataSetChanged()
//    }
    fun getTasks(): List<Task> = tasks
}
