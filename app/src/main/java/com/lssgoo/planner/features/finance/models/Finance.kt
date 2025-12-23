package com.lssgoo.planner.features.finance.models

import java.util.UUID

/**
 * Finance models for tracking money, budgets, and debts
 */

enum class TransactionType {
    INCOME,    // Money coming in (Credit)
    EXPENSE,   // Money going out (Debit)
    BORROWED,  // Money taken from someone
    LENT       // Money given to someone
}

enum class TransactionCategory(val icon: String) {
    FOOD("🍕"),
    TRANSPORT("🚗"),
    SHOPPING("🛍️"),
    ENTERTAINMENT("🎬"),
    HEALTH("💊"),
    EDUCATION("📚"),
    SALARY("💰"),
    INVESTMENT("📈"),
    BILL("📄"),
    RENT("🏠"),
    GIFT("🎁"),
    OTHER("✨"),
    DEBT_REPAYMENT("🤝")
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val note: String = "",
    val personName: String? = null, // Used for Debt/Lent
    val date: Long = System.currentTimeMillis(),
    val isSettled: Boolean = false, // For debts
    val receiptUri: String? = null, // For receipts (Option 5)
    val isRecurring: Boolean = false, // For recurring transactions (Option 4)
    val recurringPeriod: BudgetPeriod? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class Budget(
    val id: String = UUID.randomUUID().toString(),
    val category: TransactionCategory?, // null means overall budget
    val limitAmount: Double,
    val spentAmount: Double = 0.0,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val notifiedAt: Long? = null // For budget alerts (Option 6)
)

enum class BudgetPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class FinanceLog(
    val id: String = UUID.randomUUID().toString(),
    val action: String, // "ADD", "REMOVE", "UPDATE", "SETTLED"
    val entityType: String, // "TRANSACTION", "BUDGET"
    val timestamp: Long = System.currentTimeMillis(),
    val description: String
)

data class FinanceStats(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val currentBalance: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val totalLent: Double = 0.0,
    val budgetStatus: List<Budget> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    // New fields for Analytics & Charts (Option 1)
    val categorySpending: Map<TransactionCategory, Double> = emptyMap(),
    val dailySpending: Map<Long, Double> = emptyMap(), // Date to amount
    val incomeVsExpense: Map<Long, Pair<Double, Double>> = emptyMap(),
    val upcomingRecurring: List<Transaction> = emptyList() // (Option 4)
)
