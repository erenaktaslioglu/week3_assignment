package com.example.week3_assignment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.week3_assignment.model.Room
import com.example.week3_assignment.viewmodel.DeviceItemUi
import com.example.week3_assignment.viewmodel.RoomFilterUi
import com.example.week3_assignment.viewmodel.SmartHomeUiState

@Composable
fun SmartHomeScreen(
    uiState: SmartHomeUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRoomSelected: (Room?) -> Unit,
    onToggleDevice: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        EnergyCard(
            totalPowerText = uiState.totalPowerText,
            activeDeviceText = uiState.activeDeviceText
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Cihaz ara") },
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        RoomFilterRow(
            filters = uiState.roomFilters,
            onRoomSelected = onRoomSelected
        )

        Spacer(Modifier.height(8.dp))

        if (uiState.showEmptyState) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cihaz bulunamadı",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.devices,
                    key = { it.id }
                ) { device ->
                    DeviceCard(
                        device = device,
                        onToggle = { onToggleDevice(device.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnergyCard(
    totalPowerText: String,
    activeDeviceText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Anlık Toplam Tüketim",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = totalPowerText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = activeDeviceText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomFilterRow(
    filters: List<RoomFilterUi>,
    onRoomSelected: (Room?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = filters, key = { it.label }) { filter ->
            FilterChip(
                selected = filter.isSelected,
                onClick = { onRoomSelected(filter.room) },
                label = { Text(filter.label) }
            )
        }
    }
}

@Composable
private fun DeviceCard(
    device: DeviceItemUi,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isActive && device.isOnline)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${device.roomName} • ${device.categoryName}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${device.powerText} • ${device.statusText}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = onToggle,
                enabled = device.isOnline
            ) {
                Text(device.toggleButtonText)
            }
        }
    }
}
