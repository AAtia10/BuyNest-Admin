package com.example.buynest_admin.model

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow

object NetworkStatusMonitor {
    val isConnected = MutableStateFlow(true)

    fun register(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        isConnected.value = activeNetwork != null && activeNetwork.isConnected
        val builder = NetworkRequest.Builder()

        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected.value = true
                }

                override fun onLost(network: Network) {
                    isConnected.value = false
                }
            }
        )
    }
}
