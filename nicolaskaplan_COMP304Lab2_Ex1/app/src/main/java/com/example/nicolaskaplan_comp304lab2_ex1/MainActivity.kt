package com.example.nicolaskaplan_comp304lab2_ex1


import android.app.Activity
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.Nicolaskaplan_COMP304Lab2_Ex1Theme
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.lightModePink
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.darkModePink
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.lightModePurple
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.darkModePurple
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.lightModePurpleGrey
import com.example.nicolaskaplan_comp304lab2_ex1.ui.theme.darkModePurpleGrey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale

class TaskViewModel : ViewModel()
{
    val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

}
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val dueDate: String?,
    var isCompleted: Boolean)


class MainActivity : ComponentActivity() {

    private var showEditScreen by mutableStateOf(false)
    private var selectedTask by mutableStateOf<Task?>(null)
    private var showCreateTaskScreen by mutableStateOf(false)

    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            Nicolaskaplan_COMP304Lab2_Ex1Theme {
                MainScreen(
                    showCreateTaskScreen = showCreateTaskScreen,
                    showEditScreen = showEditScreen,
                    selectedTask = selectedTask,
                    onNavigateToCreateTask = { showCreateTaskScreen = true },
                    onTaskSelected = { task ->
                        selectedTask = task
                        showEditScreen = true
                    },
                    onTaskUpdated = { updatedTask ->
                        val tasksList = taskViewModel.tasks.value
                        val index = tasksList.indexOfFirst { it.id == updatedTask.id }
                        if (index != -1) {
                            val updatedTasks = tasksList.toMutableList()
                            updatedTasks[index] = updatedTask
                            taskViewModel._tasks.value = updatedTasks
                        }
                        showEditScreen = false
                    },
                    onNavigateBack = {
                        showCreateTaskScreen = false
                        showEditScreen = false
                    },
                    onTaskCheckedChange = { task, isChecked ->
                        val tasksList = taskViewModel.tasks.value
                        val index = tasksList.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            val updatedTasks = tasksList.toMutableList()
                            updatedTasks[index] = task.copy(isCompleted = isChecked)
                            taskViewModel._tasks.value = updatedTasks
                        }
                    },
                    context = this

                )
            }
        }
    }
}

    @Composable
    fun MainScreen(
        showCreateTaskScreen: Boolean,
        showEditScreen: Boolean,
        selectedTask: Task?,
        onNavigateToCreateTask: () -> Unit,
        onTaskSelected: (Task) -> Unit,
        onTaskUpdated: (Task) -> Unit,
        onNavigateBack: () -> Unit,
        onTaskCheckedChange: (Task, Boolean) -> Unit,
        context: Context
    ) {

        val taskViewModel: TaskViewModel = viewModel()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
                HomePage(
                    onNavigateToCreateTask = onNavigateToCreateTask,
                    onTaskSelected = onTaskSelected,
                    onTaskCheckedChange = onTaskCheckedChange,
                    context = LocalContext.current
                )
            if (showCreateTaskScreen) {
                CreateTask(
                    onTaskCreated = { newTask ->
                        val currentTasks = taskViewModel._tasks.value.toMutableList()
                        currentTasks.add(newTask)
                        taskViewModel._tasks.value = currentTasks},
                    onNavigateBack = onNavigateBack
                )
            }

            if (showEditScreen && selectedTask != null) {
                ViewOrEditTask(
                    task = selectedTask,
                    onTaskUpdated = onTaskUpdated,
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavigateToCreateTask: () -> Unit,
    onTaskSelected: (Task) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    context: Context
) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks by taskViewModel.tasks.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Home Page") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(tasks) { task ->
                ItemTask(task, onTaskSelected, onTaskCheckedChange)
            }
        }
    }
}
@Composable
fun ItemTask(task: Task, onTaskSelected: (Task) -> Unit, onTaskCheckedChange: (Task, Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskSelected(task) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = task.title, style = MaterialTheme.typography.headlineSmall)
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description.take(50) + if (task.description.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(text = "Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked -> onTaskCheckedChange(task, isChecked) }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTask(
    onTaskCreated: (Task) -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            val datePickerState = rememberDatePickerState()

            OutlinedButton(onClick = { showDialog = true }) {
                Text("Select Due Date")
            }

            if (showDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formattedDate =
                                datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                    formatter.format(selectedDateMillis)
                                }
                            dueDate = formattedDate
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val newTask = Task(
                        id = 0,
                        title = title,
                        description = description,
                        dueDate = dueDate,
                        isCompleted = false
                    )
                    onTaskCreated(newTask)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Task")
            }
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ViewOrEditTask(
        task: Task,
        onTaskUpdated: (Task) -> Unit,
        onNavigateBack: () -> Unit
    ) {
        var title by remember { mutableStateOf(task.title) }
        var description by remember { mutableStateOf(task.description) }
        var dueDate by remember { mutableStateOf(task.dueDate) }
        var showDialog by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("View/Edit Task") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(onClick = { showDialog = true }) {
                    Text("Select Due Date")
                }
                if (showDialog) {
                    DatePickerDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            Button(onClick = {
                                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val formattedDate =
                                    datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                                        formatter.format(selectedDateMillis)
                                    }
                                dueDate = formattedDate
                                showDialog = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val updatedTask = task.copy(
                            title = title,
                            description = description,
                            dueDate = dueDate
                        )
                        onTaskUpdated(updatedTask)
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }

