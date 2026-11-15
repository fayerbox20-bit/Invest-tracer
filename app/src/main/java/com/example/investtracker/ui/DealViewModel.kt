package com.example.investtracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.investtracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Totals(
    val invested: Double = 0.0,
    val income: Double = 0.0,
    val commission: Double = 0.0,
    val tax: Double = 0.0,
    val net: Double = 0.0
)

class DealViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dealDao()

    val deals: StateFlow<List<Deal>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(deal: Deal) = viewModelScope.launch { dao.insert(deal) }
    fun delete(deal: Deal) = viewModelScope.launch { dao.delete(deal) }

    fun totalsFor(list: List<Deal>, currency: Currency): Totals {
        val filtered = list.filter { it.currency == currency }
        return Totals(
            invested   = filtered.sumOf { it.invested },
            income     = filtered.sumOf { it.income },
            commission = filtered.sumOf { it.commission },
            tax        = filtered.sumOf { it.taxAmount },
            net        = filtered.sumOf { it.netIncome }
        )
    }
}
