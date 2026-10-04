package com.vihansri.habithandle.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vihansri.habithandle.TaskViewModel
import com.vihansri.habithandle.dialogs.DurationPickerDialog
import com.vihansri.habithandle.helpers.formatReadable
import com.vihansri.habithandle.helpers.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(vm: TaskViewModel, onTaskClick: (Int) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var totalSeconds by rememberSaveable { mutableLongStateOf(0L) }
    var showPicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("My Tasks") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Task name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (totalSeconds > 0) "⏰  " + formatReadable(totalSeconds)
                    else "⏰  Select time"
                )
            }
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val total = totalSeconds
                    when {
                        name.isBlank() -> error = "Enter a task name"
                        total <= 0L -> error = "Enter a time greater than 0"
                        else -> {
                            vm.addTask(name.trim(), total)
                            name = ""; totalSeconds = 0L; error = null
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add Task") }

            if (showPicker) {
                DurationPickerDialog(
                    initialSeconds = totalSeconds,
                    onDismiss = { showPicker = false },
                    onConfirm = { totalSeconds = it; showPicker = false }
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()

            if (vm.tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks yet. Add one above.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(vm.tasks, key = { it.id }) { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !task.completed) { onTaskClick(task.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(task.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(formatTime(task.totalSeconds))
                                }
                                Text(if (task.completed) "✅ Done" else "▶ Start")
                            }
                        }
                    }
                }
            }
        }
    }
}
