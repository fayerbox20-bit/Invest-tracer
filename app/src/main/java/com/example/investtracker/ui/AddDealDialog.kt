package com.example.investtracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.investtracker.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDealDialog(onDismiss: () -> Unit, onSave: (Deal) -> Unit) {
    var project by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(DealType.STOCK_BUY) }
    var currency by remember { mutableStateOf(Currency.RUB) }
    var invested by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var commission by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("13") }
    var period by remember { mutableStateOf(PayoutPeriod.NONE) }

    var typeMenu by remember { mutableStateOf(false) }
    var curMenu by remember { mutableStateOf(false) }
    var perMenu by remember { mutableStateOf(false) }

    val isBond = type == DealType.BOND_BUY || type == DealType.COUPON

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая запись") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(project, { project = it },
                    label = { Text("Проект / тикер") }, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = typeMenu, onExpandedChange = { typeMenu = it }) {
                    OutlinedTextField(
                        value = typeLabel(type), onValueChange = {},
                        readOnly = true, label = { Text("Тип") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeMenu) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(typeMenu, { typeMenu = false }) {
                        DealType.entries.forEach {
                            DropdownMenuItem(text = { Text(typeLabel(it)) },
                                onClick = { type = it; typeMenu = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = curMenu, onExpandedChange = { curMenu = it }) {
                    OutlinedTextField(
                        value = "${currency.name} ${currency.symbol}", onValueChange = {},
                        readOnly = true, label = { Text("Валюта") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(curMenu) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(curMenu, { curMenu = false }) {
                        Currency.entries.forEach {
                            DropdownMenuItem(text = { Text("${it.name} ${it.symbol}") },
                                onClick = { currency = it; curMenu = false })
                        }
                    }
                }

                if (isBond) {
                    ExposedDropdownMenuBox(expanded = perMenu, onExpandedChange = { perMenu = it }) {
                        OutlinedTextField(
                            value = periodLabel(period), onValueChange = {},
                            readOnly = true, label = { Text("Периодичность выплат") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(perMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(perMenu, { perMenu = false }) {
                            listOf(PayoutPeriod.MONTHLY, PayoutPeriod.QUARTERLY).forEach {
                                DropdownMenuItem(text = { Text(periodLabel(it)) },
                                    onClick = { period = it; perMenu = false })
                            }
                        }
                    }
                }

                OutlinedTextField(invested, { invested = it.filterChar() },
                    label = { Text("Сумма вложения") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(income, { income = it.filterChar() },
                    label = { Text("Дивиденды / купон") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(commission, { commission = it.filterChar() },
                    label = { Text("Комиссия") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(taxPercent, { taxPercent = it.filterChar() },
                    label = { Text("Налог, %") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (project.isBlank()) return@TextButton
                onSave(
                    Deal(
                        date = System.currentTimeMillis(),
                        project = project,
                        type = type,
                        currency = currency,
                        invested = invested.toDoubleOrNull() ?: 0.0,
                        income = income.toDoubleOrNull() ?: 0.0,
                        commission = commission.toDoubleOrNull() ?: 0.0,
                        taxPercent = taxPercent.toDoubleOrNull() ?: 0.0,
                        payoutPeriod = if (isBond) period else PayoutPeriod.NONE
                    )
                )
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private fun String.filterChar(): String =
    filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
