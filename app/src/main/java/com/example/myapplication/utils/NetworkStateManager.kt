package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages network state changes and provides network status information
 */
@Singleton
class NetworkStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            val state = when {
                capabilities == null -> NetworkState.Unavailable
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        NetworkState.Connected
                    } else {
                        NetworkState.CaptivePortal
                    }
                }
                else -> NetworkState.Unavailable
            }
            _networkState.postValue(state)
        }

        override fun onLost(network: Network) {
            _networkState.postValue(NetworkState.Unavailable)
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            val state = when {
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                    if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        NetworkState.Connected
                    } else {
                        NetworkState.CaptivePortal
                    }
                }
                else -> NetworkState.Unavailable
            }
            _networkState.postValue(state)
        }
    }

    init {
        registerNetworkCallback()
        updateInitialState()
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun updateInitialState() {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }
        
        val state = when {
            network == null || capabilities == null -> NetworkState.Unavailable
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    NetworkState.Connected
                } else {
                    NetworkState.CaptivePortal
                }
            }
            else -> NetworkState.Unavailable
        }
        _networkState.value = state
    }

    fun getNetworkStateMessage(): String {
        return when (networkState.value) {
            NetworkState.Connected -> "Network connected"
            NetworkState.Unavailable -> "Network unavailable"
            NetworkState.CaptivePortal -> "Network captive portal detected"
            null -> "Network state unknown"
        }
    }
}

/**
 * Represents the current state of the network connection
 */
enum class NetworkState {
    Connected,
    Unavailable,
    CaptivePortal
}
