package com.example.buynest_admin.model

data class LocationResponse(val locations: List<Location>)

data class Location(
    val id: Long,
    val name: String,
    val address1: String?,
    val city: String?,
    val country: String?
)
