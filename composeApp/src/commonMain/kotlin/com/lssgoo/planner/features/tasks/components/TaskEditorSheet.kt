package com.lssgoo.planner.features.tasks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.features.tasks.models.TaskPriority
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpIdGenerator
import com.lssgoo.planner.util.KmpTimeUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskEditorSheet(
    task: Task?,
    goals: List<Goal>,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf(task?.dueDate) }
    var linkedGoalId by remember { mutableStateOf(task?.linkedGoalId) }
    var selectedTags by remember { mutableStateOf(task?.tags?.toSet() ?: emptySet()) }
    
    var showDatePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (task != null) "Edit Task" else "New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (task != null) {
                    IconButton(onClick = { onDelete(task.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            // Priority
            Text("Priority", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.displayName) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(p.color).copy(alpha = 0.2f),
                            selectedLabelColor = Color(p.color)
                        )
                    )
                }
            }

            // Tags
            Text("Tags", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.lssgoo.planner.features.tasks.models.TaskTags.ALL.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    val tagColor = Color(com.lssgoo.planner.features.tasks.models.TaskTags.getColorForTag(tag))
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                        },
                        label = { Text(tag) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tagColor.copy(alpha = 0.2f),
                            selectedLabelColor = tagColor
                        )
                    )
                }
            }

            // Deadline
            Text("Deadline", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Card(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Event, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = dueDate?.let { KmpDateFormatter.formatMediumDate(it) } ?: "Set completion date",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (dueDate != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { dueDate = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onSave(Task(
                        id = task?.id ?: KmpIdGenerator.generateId(),
                        title = title,
                        description = description,
                        priority = priority,
                        dueDate = dueDate,
                        linkedGoalId = linkedGoalId,
                        tags = selectedTags.toList(),
                        createdAt = task?.createdAt ?: KmpTimeUtils.currentTimeMillis()
                    ))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Confirm Task", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate ?: KmpTimeUtils.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
