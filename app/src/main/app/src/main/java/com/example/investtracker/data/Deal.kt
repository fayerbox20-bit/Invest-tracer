package com.example.investtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Currency(val symbol: String) {
    RUB("₽"), USD("$"), CNY("¥")
}

enum class DealType {
    STOCK_BUY, BOND_BUY, DIVIDEND, COUPON
}

enum class PayoutPeriod {
    NONE, MONTHLY, QUARTERLY
}

@Entity(tableName = "deals")
data class Deal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val project: String,
    val type: DealType,
    val currency: Currency,
    val invested: Double,
    val income: Double,
    val commission: Double,
    val taxPercent: Double,
    val payoutPeriod: PayoutPeriod = PayoutPeriod.NONE
) {
    val taxAmount: Double get() = income * taxPercent / 100.0
    val netIncome: Double get() = income - taxAmount - commission
}
