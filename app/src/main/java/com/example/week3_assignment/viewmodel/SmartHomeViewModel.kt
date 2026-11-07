package com.example.week3_assignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week3_assignment.data.DeviceRepository
import com.example.week3_assignment.model.Device
import com.example.week3_assignment.model.DeviceCategory
import com.example.week3_assignment.model.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

// ---------- UI'ın göreceği hazır (işlenmiş) modeller ----------

data class DeviceItemUi(
    val id: Int,
    val name: String,
    val roomName: String,
    val categoryName: String,
    val powerText: String,
    val statusText: String,
    val toggleButtonText: String,
    val isActive: Boolean,
    val isOnline: Boolean
)

data class RoomFilterUi(
    val room: Room?,          // null = "Tümü"
    val label: String,
    val isSelected: Boolean
)

data class SmartHomeUiState(
    val totalPowerText: String = "0.0 W",
    val activeDeviceText: String = "0 aktif cihaz",
    val roomFilters: List<RoomFilterUi> = emptyList(),
    val devices: List<DeviceItemUi> = emptyList(),
    val showEmptyState: Boolean = false
)

class SmartHomeViewModel(
    private val repository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    // Arama kutusu için ayrı, senkron StateFlow (yazarken imleç/gecikme sorunu olmasın)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRoom = MutableStateFlow<Room?>(null)

    val uiState: StateFlow<SmartHomeUiState> = combine(
        repository.devices,
        _searchQuery,
        _selectedRoom
    ) { devices, query, selectedRoom ->
        buildUiState(devices, query, selectedRoom)
    }
        .flowOn(Dispatchers.Default) // filtreleme + toplama Main thread'de çalışmaz
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SmartHomeUiState()
        )

    // ---------- UI olayları ----------

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onRoomSelected(room: Room?) {
        _selectedRoom.value = room
    }

    fun onToggleDevice(deviceId: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.toggleDevice(deviceId)
        }
    }

    // ---------- İş mantığı ----------

    private fun buildUiState(
        devices: List<Device>,
        query: String,
        selectedRoom: Room?
    ): SmartHomeUiState {
        // Enerji sayacı: filtreden bağımsız, tüm evde ONLINE + AKTİF cihazların toplamı
        val running = devices.filter { it.isOnline && it.isActive }
        val totalWatt = running.sumOf { it.powerWatt }

        // Oda + arama filtresi
        val trimmedQuery = query.trim()
        val filtered = devices
            .filter { selectedRoom == null || it.room == selectedRoom }
            .filter { trimmedQuery.isEmpty() || it.name.contains(trimmedQuery, ignoreCase = true) }

        val roomFilters = buildList {
            add(RoomFilterUi(null, "Tümü", selectedRoom == null))
            Room.entries.forEach { room ->
                add(RoomFilterUi(room, room.displayName, selectedRoom == room))
            }
        }

        return SmartHomeUiState(
            totalPowerText = formatWatt(totalWatt),
            activeDeviceText = "${running.size} aktif cihaz",
            roomFilters = roomFilters,
            devices = filtered.map { it.toUi() },
            showEmptyState = filtered.isEmpty()
        )
    }

    private fun Device.toUi() = DeviceItemUi(
        id = id,
        name = name,
        roomName = room.displayName,
        categoryName = category.toDisplayName(),
        powerText = formatWatt(powerWatt),
        statusText = when {
            !isOnline -> "Çevrimdışı"
            isActive -> "Açık"
            else -> "Kapalı"
        },
        toggleButtonText = if (isActive) "Kapat" else "Aç",
        isActive = isActive,
        isOnline = isOnline
    )

    private fun DeviceCategory.toDisplayName() = when (this) {
        DeviceCategory.LIGHT -> "Aydınlatma"
        DeviceCategory.CAMERA -> "Kamera"
        DeviceCategory.THERMOSTAT -> "Termostat"
        DeviceCategory.VACUUM -> "Süpürge"
    }

    private fun formatWatt(value: Double) = String.format(Locale.US, "%.1f W", value)
}
