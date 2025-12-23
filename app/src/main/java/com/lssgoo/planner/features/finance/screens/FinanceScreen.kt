package com.lssgoo.planner.features.finance.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.ui.components.*
import com.lssgoo.planner.ui.theme.*
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.financeStats.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val logs by viewModel.financeLogs.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    
    val tabs = listOf("Dashboard", "Transactions", "Budgets", "Audit Logs")
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Finance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = { /* Sync logic */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedTab == 2) {
                    GradientFAB(
                        onClick = { showAddBudgetDialog = true },
                        icon = Icons.Filled.PlaylistAdd,
                        gradientColors = GradientColors.purpleBlue
                    )
                }
                GradientFAB(
                    onClick = { showAddTransactionDialog = true },
                    icon = Icons.Filled.Add,
                    gradientColors = GradientColors.oceanBlue
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Tab Selector
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> FinanceDashboard(stats)
                    1 -> TransactionList(transactions, onUpdate = { viewModel.updateTransaction(it) }, onDelete = { viewModel.deleteTransaction(it) })
                    2 -> BudgetList(budgets, onRemove = { viewModel.removeBudget(it) })
                    3 -> AuditLogsList(logs)
                }
            }
        }
    }
    
    if (showAddTransactionDialog) {
        AddTransactionDialog(
            onDismiss = { showAddTransactionDialog = false },
            onAdd = { transaction ->
                viewModel.addTransaction(transaction)
                showAddTransactionDialog = false
            }
        )
    }
    
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            onDismiss = { showAddBudgetDialog = false },
            onAdd = { budget ->
                viewModel.addBudget(budget)
                showAddBudgetDialog = false
            }
        )
    }
}

@Composable
fun FinanceDashboard(stats: FinanceStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(GradientColors.purpleBlue))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "Total Balance",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        "₹${String.format("%.2f", stats.currentBalance)}",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Income", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                            Text("₹${String.format("%.1f", stats.totalIncome)}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Expense", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                            Text("₹${String.format("%.1f", stats.totalExpense)}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        // Debt/Lent Stats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatsCard(
                title = "Borrowed",
                value = "₹${String.format("%.1f", stats.totalBorrowed)}",
                icon = Icons.Default.ArrowDownward,
                gradientColors = listOf(FinanceColors.expense, FinanceColors.expenseLight),
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "Lent",
                value = "₹${String.format("%.1f", stats.totalLent)}",
                icon = Icons.Default.ArrowUpward,
                gradientColors = listOf(FinanceColors.income, FinanceColors.incomeLight),
                modifier = Modifier.weight(1f)
            )
        }
        
        SectionHeader(title = "Budget Overview", action = "View All")
        
        if (stats.budgetStatus.isEmpty()) {
            Text("No budgets set. Create one to track your spending!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            stats.budgetStatus.take(3).forEach { budget ->
                BudgetProgressItem(budget)
            }
        }
        
        SectionHeader(title = "Recent Transactions", action = "View All")
        
        stats.recentTransactions.forEach { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionList(transactions: List<Transaction>, onUpdate: (Transaction) -> Unit, onDelete: (String) -> Unit) {
    if (transactions.isEmpty()) {
        EmptyState(
            title = "No transactions yet",
            description = "Start tracking your finance movement",
            icon = Icons.Default.AccountBalanceWallet
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction, onClick = { /* Show details or edit */ })
            }
        }
    }
}

@Composable
fun BudgetList(budgets: List<Budget>, onRemove: (String) -> Unit) {
    if (budgets.isEmpty()) {
        EmptyState(
            title = "No budgets set",
            description = "Manage your monthly or weekly limits",
            icon = Icons.Default.PieChart
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(budgets) { budget ->
                BudgetProgressItem(budget, showDelete = true, onDelete = { onRemove(budget.id) })
            }
        }
    }
}

@Composable
fun AuditLogsList(logs: List<FinanceLog>) {
    if (logs.isEmpty()) {
        EmptyState(
            title = "History is clean",
            description = "Every move you make will be recorded here",
            icon = Icons.Default.History
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logs) { log ->
                LogItem(log)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: (() -> Unit)? = null) {
    val color = when (transaction.type) {
        TransactionType.INCOME -> FinanceColors.income
        TransactionType.EXPENSE -> FinanceColors.expense
        TransactionType.BORROWED -> FinanceColors.borrowed
        TransactionType.LENT -> FinanceColors.lent
    }
    
    val prefix = when (transaction.type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.BORROWED -> "+"
        TransactionType.LENT -> "-"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(transaction.category.icon, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.note.ifBlank { transaction.category.name.lowercase().capitalize() },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (transaction.personName != null) {
                    Text(
                        text = "Person: ${transaction.personName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Text(
                text = "$prefix ₹${String.format("%.1f", transaction.amount)}",
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun BudgetProgressItem(budget: Budget, showDelete: Boolean = false, onDelete: (() -> Unit)? = null) {
    val progress = (budget.spentAmount / budget.limitAmount).toFloat().coerceIn(0f, 1.2f)
    val color = if (progress > 1f) Color.Red else MaterialTheme.colorScheme.primary
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(budget.category?.icon ?: "📊", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        budget.category?.name?.lowercase()?.capitalize() ?: "Overall Budget",
                        fontWeight = FontWeight.Bold
                    )
                }
                if (showDelete) {
                    IconButton(onClick = { onDelete?.invoke() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                    }
                } else {
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Spent: ₹${budget.spentAmount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Limit: ₹${budget.limitAmount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LogItem(log: FinanceLog) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = when (log.action) {
            "ADD" -> Icons.Default.AddCircle
            "REMOVE" -> Icons.Default.RemoveCircle
            "UPDATE" -> Icons.Default.Edit
            else -> Icons.Default.Info
        }
        val iconColor = when (log.action) {
            "ADD" -> FinanceColors.income
            "REMOVE" -> FinanceColors.expense
            "UPDATE" -> FinanceColors.update
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(log.description, style = MaterialTheme.typography.bodySmall)
            Text(
                SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault()).format(Date(log.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddTransactionDialog(onDismiss: () -> Unit, onAdd: (Transaction) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.FOOD) }
    var personName by remember { mutableStateOf("") }
    var isGivenToSomeone by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Type Selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TransactionType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null
                )
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note / Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Category", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text("${category.icon} ${category.name.lowercase().capitalize()}") }
                        )
                    }
                }
                
                // Debt logic
                if (selectedType == TransactionType.EXPENSE || selectedType == TransactionType.LENT || selectedType == TransactionType.BORROWED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isGivenToSomeone, onCheckedChange = { isGivenToSomeone = it })
                        Text("Involve someone else?", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (isGivenToSomeone || selectedType == TransactionType.LENT || selectedType == TransactionType.BORROWED) {
                        OutlinedTextField(
                            value = personName,
                            onValueChange = { personName = it },
                            label = { Text("Person Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    val finalType = if (isGivenToSomeone && selectedType == TransactionType.EXPENSE) TransactionType.LENT else selectedType
                    onAdd(Transaction(
                        amount = amt,
                        type = finalType,
                        category = selectedCategory,
                        note = note,
                        personName = personName.ifBlank { null }
                    ))
                },
                enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddBudgetDialog(onDismiss: () -> Unit, onAdd: (Budget) -> Unit) {
    var limit by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = limit,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) limit = it },
                    label = { Text("Budget Limit (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Category (optional)", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Overall") }
                    )
                    TransactionCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text("${category.icon} ${category.name.lowercase().capitalize()}") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lim = limit.toDoubleOrNull() ?: 0.0
                    onAdd(Budget(
                        category = selectedCategory,
                        limitAmount = lim
                    ))
                },
                enabled = limit.isNotEmpty() && limit.toDoubleOrNull() != null
            ) {
                Text("Set Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}

fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
