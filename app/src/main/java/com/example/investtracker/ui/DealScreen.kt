package com.example.investtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.investtracker.data.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealScreen(vm: DealViewModel = viewModel()) {
    val deals by vm.deals.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val fmt = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Инвестиции", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, "Добавить") }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad).fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { SummaryCards(vm, deals) }

            item {
                Text("Сделки", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            if (deals.isEmpty()) {
                item {
                    Text("Пока нет записей. Нажми + чтобы добавить.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            items(deals, key = { it.id }) { d ->
                DealRow(d, fmt) { vm.delete(d) }
            }
        }
    }

    if (showDialog) {
        AddDealDialog(
            onDismiss = { showDialog = false },
            onSave = { vm.add(it); showDialog = false }
        )
    }
}

@Composable
fun SummaryCards(vm: DealViewModel, deals: List<Deal>) {
    Currency.entries.forEach { cur ->
        val t = vm.totalsFor(deals, cur)
        if (t.invested == 0.0 && t.income == 0.0) return@forEach
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF43A047)))
                ).padding(16.dp)
            ) {
                Column {
                    Text("Итоги ${cur.symbol}", color = Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Инвестировано: ${"%.2f".format(t.invested)} ${cur.symbol}", color = Color.White)
                    Text("Доход: ${"%.2f".format(t.income)} ${cur.symbol}", color = Color.White)
                    Text("Комиссии: ${"%.2f".format(t.commission)} ${cur.symbol}", color = Color.White)
                    Text("Налог: ${"%.2f".format(t.tax)} ${cur.symbol}", color = Color.White)
                    Divider(Modifier.padding(vertical = 6.dp), color = Color.White.copy(alpha = 0.4f))
                    Text("Чистый доход: ${"%.2f".format(t.net)} ${cur.symbol}",
                        color = Color(0xFFFFF176), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DealRow(d: Deal, fmt: SimpleDateFormat, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${d.project}  •  ${typeLabel(d.type)}", fontWeight = FontWeight.Bold)
                Text(fmt.format(Date(d.date)), fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (d.payoutPeriod != PayoutPeriod.NONE)
                    Text("Выплаты: ${periodLabel(d.payoutPeriod)}", fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                if (d.invested > 0) Text("Вложено: ${"%.2f".format(d.invested)} ${d.currency.symbol}", fontSize = 13.sp)
                if (d.income > 0) Text(
                    "Доход: ${"%.2f".format(d.income)} ${d.currency.symbol}  " +
                    "(налог ${d.taxPercent}% = ${"%.2f".format(d.taxAmount)})", fontSize = 13.sp)
                if (d.commission > 0) Text("Комиссия: ${"%.2f".format(d.commission)} ${d.currency.symbol}", fontSize = 13.sp)
                Text("Чистыми: ${"%.2f".format(d.netIncome)} ${d.currency.symbol}",
                    fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun typeLabel(t: DealType) = when (t) {
    DealType.STOCK_BUY -> "Покупка акции"
    DealType.BOND_BUY -> "Покупка облигации"
    DealType.DIVIDEND -> "Дивиденды"
    DealType.COUPON -> "Купон"
}

fun periodLabel(p: PayoutPeriod) = when (p) {
    PayoutPeriod.MONTHLY -> "раз в месяц"
    PayoutPeriod.QUARTERLY -> "раз в квартал"
    PayoutPeriod.NONE -> "—"
}
