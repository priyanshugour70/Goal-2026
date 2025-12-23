package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for managing Finance data - logic for charts, debts, and reports
 */
class FinanceRepository(private val storage: LocalStorageManager) {

    fun getTransactions(): List<Transaction> = storage.getTransactions()
    fun getBudgets(): List<Budget> = storage.getBudgets()
    fun getLogs(): List<FinanceLog> = storage.getFinanceLogs()

    fun addTransaction(transaction: Transaction) {
        storage.addTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction) {
        storage.updateTransaction(transaction)
    }

    fun deleteTransaction(id: String) {
        storage.deleteTransaction(id)
    }

    fun addBudget(budget: Budget) {
        storage.addBudget(budget)
    }

    fun removeBudget(id: String) {
        storage.removeBudget(id)
    }

    /**
     * Settle a debt (Option 2)
     */
    fun settleDebt(id: String) {
        val transactions = getTransactions().toMutableList()
        val index = transactions.indexOfFirst { it.id == id }
        if (index != -1) {
            val debt = transactions[index]
            transactions[index] = debt.copy(isSettled = true)
            storage.saveTransactions(transactions)
            
            // Add a settlement log
            val logs = storage.getFinanceLogs().toMutableList()
            logs.add(0, FinanceLog(
                action = "SETTLED",
                entityType = "TRANSACTION",
                description = "Settled ${debt.type} of ₹${debt.amount} with ${debt.personName ?: "someone"}"
            ))
            storage.saveFinanceLogs(logs)
        }
    }

    fun getFinanceStats(): FinanceStats {
        val transactions = getTransactions()
        val budgets = getBudgets()
        
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val borrowed = transactions.filter { it.type == TransactionType.BORROWED && !it.isSettled }.sumOf { it.amount }
        val lent = transactions.filter { it.type == TransactionType.LENT && !it.isSettled }.sumOf { it.amount }
        
        // Option 1: Analytics Data
        val categoryMap = transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val dailyMap = transactions.filter { it.type == TransactionType.EXPENSE && it.date >= today - (30L * 24 * 60 * 60 * 1000) }
            .groupBy { getStartOfDay(it.date) }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val trendMap = transactions.filter { it.date >= today - (30L * 24 * 60 * 60 * 1000) }
            .groupBy { getStartOfDay(it.date) }
            .mapValues { (_, list) ->
                val inc = list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val exp = list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                Pair(inc, exp)
            }

        // Option 4: Recurring entries
        val recurring = transactions.filter { it.isRecurring }

        return FinanceStats(
            totalIncome = income,
            totalExpense = expense,
            currentBalance = income - expense + borrowed - lent,
            totalBorrowed = borrowed,
            totalLent = lent,
            budgetStatus = budgets,
            recentTransactions = transactions.take(10),
            categorySpending = categoryMap,
            dailySpending = dailyMap,
            incomeVsExpense = trendMap,
            upcomingRecurring = recurring
        )
    }

    /**
     * CSV Export logic (Option 7)
     */
    fun generateTransactionsCSV(): String {
        val transactions = getTransactions()
        val sb = StringBuilder()
        sb.append("ID,Date,Amount,Type,Category,Note,Person,IsSettled\n")
        
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        transactions.forEach { t ->
            sb.append("${t.id},")
            sb.append("${df.format(Date(t.date))},")
            sb.append("${t.amount},")
            sb.append("${t.type},")
            sb.append("${t.category},")
            sb.append("\"${t.note}\",")
            sb.append("${t.personName ?: ""},")
            sb.append("${t.isSettled}\n")
        }
        return sb.toString()
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
