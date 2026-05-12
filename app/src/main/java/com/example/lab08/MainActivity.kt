package com.example.lab08
import androidx.compose.material.icons.filled.Edit
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab08.ui.theme.Lab08Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @SuppressLint("ViewModelConstructorInComposable")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            Lab08Theme {

                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                val taskDao = db.taskDao()

                val viewModel = TaskViewModel(taskDao)

                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {

    val tasks by viewModel.tasks.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var newTaskDescription by remember {
        mutableStateOf("")
    }
    var searchText by remember {
        mutableStateOf("")
    }
    var filter by remember {
        mutableStateOf("Todas")
    }

    val filteredTasks = when (filter) {

        "Pendientes" -> tasks.filter {
            !it.isCompleted
        }

        "Completadas" -> tasks.filter {
            it.isCompleted
        }

        else -> tasks
    }.filter {
        it.description.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        containerColor = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "✨ Mis Tareas",
                style = MaterialTheme.typography.headlineMedium,
                color = androidx.compose.ui.graphics.Color(0xFF8E7DBE)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = newTaskDescription,

                onValueChange = {
                    newTaskDescription = it
                },

                label = {
                    Text("Nueva tarea")
                },

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color(0xFFF8F4FF),
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFFF8F4FF),
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color(0xFFD6C6F5),
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color(0xFFD6C6F5)
                ),

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {

                    if (newTaskDescription.isNotEmpty()) {

                        viewModel.addTask(newTaskDescription)

                        newTaskDescription = ""
                    }
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFDCCEF9)
                ),

                modifier = Modifier.fillMaxWidth()

            ) {

                Text(
                    "Agregar tarea",
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = searchText,

                onValueChange = {
                    searchText = it
                },

                label = {
                    Text("Buscar tarea")
                },

                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color(0xFFF8F4FF),
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFFF8F4FF)
                ),

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button(
                    onClick = {
                        filter = "Todas"
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFFFE5EC)
                    )
                ) {
                    Text(
                        "Todas",
                        color = androidx.compose.ui.graphics.Color.Black
                    )
                }

                Button(
                    onClick = {
                        filter = "Pendientes"
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFE2F0CB)
                    )
                ) {
                    Text(
                        "Pendientes",
                        color = androidx.compose.ui.graphics.Color.Black
                    )
                }

                Button(
                    onClick = {
                        filter = "Completadas"
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFFBDE0FE)
                    )
                ) {
                    Text(
                        "Completadas",
                        color = androidx.compose.ui.graphics.Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(filteredTasks) { task ->
                    var showDialog by remember {
                        mutableStateOf(false)
                    }

                    var editedText by remember {
                        mutableStateOf(task.description)
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFFFDF7FF)
                        ),

                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ){
                    if (showDialog) {

                        AlertDialog(

                            onDismissRequest = {
                                showDialog = false
                            },

                            confirmButton = {

                                Button(
                                    onClick = {

                                        viewModel.editTask(
                                            task,
                                            editedText
                                        )

                                        showDialog = false
                                    }
                                ) {
                                    Text("Guardar")
                                }
                            },

                            dismissButton = {

                                Button(
                                    onClick = {
                                        showDialog = false
                                    }
                                ) {
                                    Text("Cancelar")
                                }
                            },

                            title = {
                                Text("Editar tarea")
                            },

                            text = {

                                TextField(
                                    value = editedText,

                                    onValueChange = {
                                        editedText = it
                                    }
                                )
                            }
                        )
                    }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),

                            horizontalArrangement = Arrangement.SpaceBetween,

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = task.description,

                                style = MaterialTheme.typography.bodyLarge,

                                color = androidx.compose.ui.graphics.Color.Black,

                                modifier = Modifier.weight(1f)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Checkbox(
                                    checked = task.isCompleted,

                                    onCheckedChange = {
                                        viewModel.toggleTaskCompletion(task)
                                    },

                                    colors = CheckboxDefaults.colors(
                                        checkedColor = androidx.compose.ui.graphics.Color(0xFFB5EAD7),
                                        uncheckedColor = androidx.compose.ui.graphics.Color(0xFFCDB4DB)
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        showDialog = true
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",

                                        tint = androidx.compose.ui.graphics.Color(0xFF90CAF9)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteTask(task)
                                    }
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",

                                        tint = androidx.compose.ui.graphics.Color(0xFFFF8FAB)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {

                    coroutineScope.launch {

                        viewModel.deleteAllTasks()
                    }
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFFFC8DD)
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)

            ) {

                Text(
                    "Eliminar todas las tareas",
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }
        }
    }
}