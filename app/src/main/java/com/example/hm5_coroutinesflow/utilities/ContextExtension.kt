package com.example.hm5_coroutinesflow.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.core.content.getSystemService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@OptIn(ExperimentalCoroutinesApi::class)
val Context.networkState: Flow<Boolean>
    get() = callbackFlow {

        val connectManager = getSystemService<ConnectivityManager>()
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectManager?.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    trySend(false)
                }
            })
        awaitClose()
    }


//private fun networkState() {
//    val connectManager = requireContext().getSystemService<ConnectivityManager>()
//    val request = NetworkRequest.Builder()
//        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//        .build()
//
//    connectManager?.registerNetworkCallback(
//        request,
//        object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: Network) {
//                super.onAvailable(network)
//                Toast.makeText(requireContext(), "AVAILABLE CONNECTION", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//            override fun onLost(network: Network) {
//                super.onLost(network)
//                Toast.makeText(requireContext(), "Lost connection!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    )
//}