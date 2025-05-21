package com.example.myapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.example.myapplication.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages network state and connectivity status
 */
@Singleton
class NetworkStateManager @Inject constructor(
    @ApplicationContext context: Context
) : ConnectivityManager.NetworkCallback() {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Available)
    val networkState: StateFlow<NetworkState> = _networkState

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    init {
        // Register network callback
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    /**
     * Check if the device is currently connected to the internet
     */
    fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    /**
     * Check if the device is connected to a metered network
     */
    fun isMeteredNetwork(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) ||
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        _networkState.value = NetworkState.Available
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        _networkState.value = NetworkState.Unavailable
    }

    override fun onCapabilitiesChanged(
        network: Network,
        networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        when {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> {
                _networkState.value = NetworkState.Available
            }
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL) -> {
                _networkState.value = NetworkState.CaptivePortal
            }
            else -> {
                _networkState.value = NetworkState.Unavailable
            }
        }
    }

    /**
     * Sealed class representing different network states
     */
    sealed class NetworkState {
        object Available : NetworkState()
        object Unavailable : NetworkState()
        object CaptivePortal : NetworkState()
        
        fun isConnected(): Boolean = this is Available
        
        fun getMessageResId(): Int = when (this) {
            is Available -> R.string.network_connected
            is Unavailable -> R.string.network_unavailable
            is CaptivePortal -> R.string.network_captive_portal
        }
    }
}
