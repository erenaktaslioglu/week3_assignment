package com.example.week3_assignment.model

enum class DeviceCategory { LIGHT, CAMERA, THERMOSTAT, VACUUM }

enum class Room(val displayName: String) {
    SALON("Salon"),
    MUTFAK("Mutfak"),
    BAHCE("Bahçe"),
    ANTRE("Antre"),
    YATAK_ODASI("Yatak Odası"),
    KORIDOR("Koridor"),
    BALKON("Balkon"),
    OFIS("Ofis")
}

data class Device(
    val id: Int,
    val name: String,
    val category: DeviceCategory,
    val room: Room,
    val isOnline: Boolean,
    val isActive: Boolean,
    val powerWatt: Double
)
