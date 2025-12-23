package com.lssgoo.planner.features.finance.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.lssgoo.planner.features.finance.components.*
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
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    
    val tabs = listOf("Dashboard", "Analysis", "Transactions", "Budgets", "Debts")
    val context = androidx.compose.ui.platform.LocalContext.current

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
                    IconButton(onClick = { 
                        val csv = viewModel.exportFinanceCSV()
                        // Option 7: Simple share for CSV
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, csv)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Export Report"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (selectedTab == 3) {
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
                    1 -> FinanceAnalysis(stats)
                    2 -> TransactionList(transactions, onDelete = { viewModel.deleteTransaction(it) })
                    3 -> BudgetList(budgets, onRemove = { viewModel.removeBudget(it) })
                    4 -> DebtList(
                        transactions.filter { (it.type == TransactionType.BORROWED || it.type == TransactionType.LENT) && !it.isSettled },
                        onSettle = { viewModel.settleDebt(it) }
                    )
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
fun FinanceAnalysis(stats: FinanceStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Option 1: Pie Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                SpendingPieChart(data = stats.categorySpending)
            }
        }

        // Option 1: Line Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Expense Trend (30 Days)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                ExpenseLineChart(data = stats.dailySpending)
            }
        }

        // Option 6: Daily Average
        val daysInMonth = 30
        val dailyAvg = stats.totalExpense / daysInMonth
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Daily Average", style = MaterialTheme.typography.labelSmall)
                    Text("₹${dailyAvg.toInt()} / day", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FinanceDashboard(stats: FinanceStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Current Balance", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                Text(
                    "₹${String.format("%,.0f", stats.currentBalance)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowDownward, null, modifier = Modifier.size(12.dp), tint = FinanceColors.income)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Income", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                        }
                        Text("₹${stats.totalIncome.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, null, modifier = Modifier.size(12.dp), tint = FinanceColors.expense)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Expense", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                        }
                        Text("₹${stats.totalExpense.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }

        // Option 4: Upcoming Bills
        if (stats.upcomingRecurring.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Upcoming Bills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(stats.upcomingRecurring) { bill: Transaction ->
                        Card(
                            modifier = Modifier.width(160.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(bill.category.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(bill.category.name.lowercase().replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("₹${bill.amount.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                                Text(bill.recurringPeriod?.name ?: "Monthly", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Option 6: Savings Goal Integration
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Savings Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Annual Savings 2026", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text("45%", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { 0.45f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                        Text("Saved ₹2,25,000 of ₹5,00,000", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Stats Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatsCompactCard("Borrowed", "₹${stats.totalBorrowed.toInt()}", FinanceColors.expense, Modifier.weight(1f))
            StatsCompactCard("Lent", "₹${stats.totalLent.toInt()}", FinanceColors.income, Modifier.weight(1f))
        }

        // Recent Transactions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = { /* Could auto-switch tab */ }) {
                Text("See All", color = MaterialTheme.colorScheme.primary)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.recentTransactions.take(3).forEach { transaction ->
                TransactionItem(transaction)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
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
fun TransactionList(transactions: List<Transaction>, onDelete: (String) -> Unit) {
    if (transactions.isEmpty()) {
        EmptyState(
            title = "No transactions yet",
            description = "Start tracking your finance movement",
            icon = Icons.Default.AccountBalanceWallet
        )
    } else {
        // Option 3: Grouping by date
        val grouped = remember(transactions) {
            transactions.sortedByDescending { it.date }.groupBy { 
                val cal = Calendar.getInstance()
                val today = cal.timeInMillis
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = cal.timeInMillis
                
                when {
                    isSameDay(it.date, today) -> "Today"
                    isSameDay(it.date, yesterday) -> "Yesterday"
                    else -> SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(it.date))
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            grouped.forEach { (header, items) ->
                item {
                    Text(
                        header,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(items) { transaction ->
                    TransactionItem(transaction, onClick = { /* Option 5: Show receipt if exists */ })
                }
            }
        }
    }
}

private fun isSameDay(d1: Long, d2: Long): Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return fmt.format(Date(d1)) == fmt.format(Date(d2))
}

@Composable
fun DebtList(debts: List<Transaction>, onSettle: (String) -> Unit) {
    if (debts.isEmpty()) {
        EmptyState(
            title = "No active debts",
            description = "You are all settled up! Great job.",
            icon = Icons.Default.Handshake
        )
    } else {
        val totalOwed = debts.filter { it.type == TransactionType.BORROWED }.sumOf { it.amount }
        val totalToReceive = debts.filter { it.type == TransactionType.LENT }.sumOf { it.amount }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = FinanceColors.expenseLight.copy(alpha = 0.2f))) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("You Owe", style = MaterialTheme.typography.labelSmall)
                            Text("₹${totalOwed.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FinanceColors.expense)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = FinanceColors.incomeLight.copy(alpha = 0.2f))) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Receive", style = MaterialTheme.typography.labelSmall)
                            Text("₹${totalToReceive.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FinanceColors.income)
                        }
                    }
                }
            }

            items(debts) { debt ->
                DebtItem(debt, onSettle)
            }
        }
    }
}

@Composable
fun DebtItem(debt: Transaction, onSettle: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(
                    if (debt.type == TransactionType.BORROWED) FinanceColors.expense.copy(alpha = 0.1f) else FinanceColors.income.copy(alpha = 0.1f)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (debt.type == TransactionType.BORROWED) "↗️" else "↙️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(debt.personName ?: "Unknown", fontWeight = FontWeight.Bold)
                Text(if (debt.type == TransactionType.BORROWED) "You borrowed ₹${debt.amount}" else "You lent ₹${debt.amount}", style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = { onSettle(debt.id) },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Text("Settle", fontSize = 12.sp)
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
                if (transaction.isRecurring) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.TopEnd)
                            .padding(2.dp),
                        tint = color
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.note.ifBlank { transaction.category.name.lowercase().capitalize() },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (transaction.receiptUri != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Text(
                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
    val remaining = (budget.limitAmount - budget.spentAmount).coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(budget.category?.icon ?: "💰", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        budget.category?.name?.lowercase()?.capitalize() ?: "Overall Budget",
                        fontWeight = FontWeight.Bold
                    )
                }
                if (showDelete) {
                    IconButton(onClick = { onDelete?.invoke() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Spent: ₹${budget.spentAmount.toInt()}", style = MaterialTheme.typography.labelSmall)
                Text("Remaining: ₹${remaining.toInt()}", style = MaterialTheme.typography.labelSmall, color = if (remaining < budget.limitAmount * 0.1) Color.Red else Color.Unspecified)
            }

            // Option 6: Budget Alert
            if (progress > 0.8f) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(14.dp), tint = if (progress > 1f) Color.Red else Color(0xFFFF9800))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (progress > 1f) "Budget Exceeded!" else "Approaching Limit",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (progress > 1f) Color.Red else Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold
                    )
                }
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
    var isRecurring by remember { mutableStateOf(false) }
    var recurringPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var receiptUri by remember { mutableStateOf<String?>(null) }

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
                
                // Option 4: Recurring
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRecurring, onCheckedChange = { isRecurring = it })
                    Text("Recurring transaction?", style = MaterialTheme.typography.bodyMedium)
                }
                if (isRecurring) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BudgetPeriod.entries.forEach { period ->
                            FilterChip(
                                selected = recurringPeriod == period,
                                onClick = { recurringPeriod = period },
                                label = { Text(period.name, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                // Option 5: Receipt
                Button(
                    onClick = { receiptUri = "mock_uri_${System.currentTimeMillis()}" },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (receiptUri == null) "Choose Receipt" else "Receipt Attached")
                }

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
                        personName = personName.ifBlank { null },
                        isRecurring = isRecurring,
                        recurringPeriod = if (isRecurring) recurringPeriod else null,
                        receiptUri = receiptUri
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
@Composable
fun StatsCompactCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = color)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
