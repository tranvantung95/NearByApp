package com.example.nearbyapp

data class PairDevice(var id : String?,var name: String? = null, var pairStatus: PairStatus? = null)
enum class PairStatus {
    Discover, Connect, Disconnect
}
