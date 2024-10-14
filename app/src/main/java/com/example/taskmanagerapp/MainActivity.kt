package com.example.taskmanagerapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.adapter.TaskAdapter
import com.example.taskmanagerapp.databinding.ActivityMainBinding
import com.example.taskmanagerapp.model.Priority
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        setupRecyclerView()

        // Observe tasks LiveData
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let {
                taskAdapter.setTasks(it)
            }
        })

        // Handle FloatingActionButton click to add new task
        binding.fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }

        // Add swipe-to-delete functionality
        setupSwipeToDelete()
    }

    // Setup RecyclerView
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList()) { task ->
            // Handle task item click (e.g., edit)
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("task_id", task.id)
            startActivity(intent)
        }
        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    // Setup swipe-to-delete functionality
    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Not implementing move functionality
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskAdapter.getTasks()[position]
                taskViewModel.delete(task)

                // Show Snack bar with Undo option
                Snackbar.make(binding.recyclerViewTasks, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        taskViewModel.insert(task)
                    }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewTasks)
    }

    // Create menu options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Setup Search
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Search Tasks"

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // No action on submit
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTasks(newText)
                return true
            }
        })

        return true
    }

    // Handle menu item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_priority -> {
                taskViewModel.getTasksByPriority().observe(this, Observer { tasks ->
                    taskAdapter.setTasks(tasks)
                })
                return true
            }
            R.id.action_sort_deadline -> {
                taskViewModel.getTasksByDeadline().observe(this, Observer { tasks ->
                    taskAdapter.setTasks(tasks)
                })
                return true
            }
            R.id.action_filter_high_priority -> {
                taskViewModel.getTasksByPriorityFilter(Priority.HIGH).observe(this, Observer { tasks ->
                    taskAdapter.setTasks(tasks)
                })
                return true
            }
            R.id.action_filter_medium_priority -> {
                taskViewModel.getTasksByPriorityFilter(Priority.MEDIUM).observe(this, Observer { tasks ->
                    taskAdapter.setTasks(tasks)
                })
                return true
            }
            R.id.action_filter_low_priority -> {
                taskViewModel.getTasksByPriorityFilter(Priority.LOW).observe(this, Observer { tasks ->
                    taskAdapter.setTasks(tasks)
                })
                return true
            }
            R.id.action_clear_filter -> {
                taskViewModel.allTasks.observe(this, Observer { tasks ->
                    taskAdapter.setTasks(tasks)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Filter tasks based on search query
    private fun filterTasks(query: String?) {
        if (query.isNullOrEmpty()) {
            taskViewModel.allTasks.observe(this, Observer { tasks ->
                taskAdapter.setTasks(tasks)
            })
        } else {
            val filteredTasks = taskViewModel.allTasks.value?.filter {
                it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
            }
            taskAdapter.setTasks(filteredTasks ?: emptyList())
        }
    }
}
