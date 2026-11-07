package com.example.week3_assignment.data

import com.example.week3_assignment.model.Device
import com.example.week3_assignment.model.DeviceCategory
import com.example.week3_assignment.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DeviceRepository {

    private val _devices = MutableStateFlow(initialDevices())
    val devices: StateFlow<List<Device>> = _devices.asStateFlow()

    /** Sadece online cihazların durumu değiştirilebilir. update{} thread-safe'tir. */
    fun toggleDevice(deviceId: Int) {
        _devices.update { list ->
            list.map { device ->
                if (device.id == deviceId && device.isOnline) {
                    device.copy(isActive = !device.isActive)
                } else {
                    device
                }
            }
        }
    }

    private fun initialDevices() = listOf(
        Device(1, "Köşe Lambası", DeviceCategory.LIGHT, Room.SALON, true, true, 15.0),
        Device(2, "Tavan Avizesi", DeviceCategory.LIGHT, Room.SALON, true, false, 60.0),
        Device(3, "Bahçe Kamerası", DeviceCategory.CAMERA, Room.BAHCE, true, true, 8.5),
        Device(4, "Giriş Kamerası", DeviceCategory.CAMERA, Room.ANTRE, true, true, 8.5),
        Device(5, "Merkezi Termostat", DeviceCategory.THERMOSTAT, Room.SALON, true, true, 12.0),
        Device(6, "Yatak Odası Kliması", DeviceCategory.THERMOSTAT, Room.YATAK_ODASI, true, false, 1200.0),
        Device(7, "Mutfak Spotları", DeviceCategory.LIGHT, Room.MUTFAK, true, false, 45.0),
        Device(8, "Roborock S7", DeviceCategory.VACUUM, Room.KORIDOR, true, true, 50.0),
        Device(9, "Balkon Aydınlatması", DeviceCategory.LIGHT, Room.BALKON, false, false, 25.0),
        Device(10, "Çalışma Odası Lambası", DeviceCategory.LIGHT, Room.OFIS, true, true, 12.0)
    )
}