package com.sghore.needtalk.data.repository

import android.util.Log
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NearByRepository @Inject constructor(
    private val connectionClient: ConnectionsClient
) {
    // Host가 다른 기기가 찾을 수 있도록 광고를 시작 함
    fun startAdvertising(userId: String, serviceId: String) = callbackFlow {
        val options = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_STAR)
            .build()
        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(p0: String, p1: Payload) {

            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {

            }
        }
        val connectionCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endPointId: String,
                connectionInfo: ConnectionInfo
            ) {
                Log.i("Check", "Host: RequestConnection")
                connectionClient.acceptConnection(
                    endPointId,
                    payloadCallback
                ).addOnSuccessListener {
                    Log.i("Check", "Host: AcceptSuccess!")
                }.addOnFailureListener {
                    it.printStackTrace()
                }
                trySend(ConnectionEvent.ConnectionInitiated(endPointId, connectionInfo))
                // TODO: 해당 콜백이 동작이 시작되고 나가거나하면 오류가 발생함
            }

            override fun onConnectionResult(
                endPointId: String,
                result: ConnectionResolution
            ) {
                trySend(ConnectionEvent.ConnectionResult(endPointId, result))

                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Log.i("Check", "Host: Is Connected!")
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        Log.i("Check", "Host: Is Connect Error")
                    }

                    else -> {
                        Log.i("Check", "Host: Is Other")
                    }
                }
            }

            override fun onDisconnected(endPointId: String) {
                trySend(ConnectionEvent.Disconnected(endPointId))
                Log.i("Check", "Host: Disconnected")
            }
        }

        connectionClient.startAdvertising(
            userId,
            serviceId,
            connectionCallback,
            options
        ).addOnFailureListener {
            it.printStackTrace()
            trySend(ConnectionEvent.AdvertisingFailure)
        }

        awaitClose {
            connectionClient.stopAllEndpoints()
            connectionClient.stopAdvertising()
        }
    }

    // 사용자가 호스트가 뿌린 광고를 찾음
    fun startDiscovery(userId: String, serviceId: String) = callbackFlow {
        val options = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_STAR)
            .build()
        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(p0: String, p1: Payload) {

            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {

            }
        }
        val connectionCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endPointId: String,
                connectionInfo: ConnectionInfo
            ) {
                Log.i("Check", "Client: Requested!")
                connectionClient.acceptConnection(
                    endPointId,
                    payloadCallback
                ).addOnSuccessListener {
                    Log.i("Check", "Client: AcceptSuccess!")
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            }

            override fun onConnectionResult(
                endPointId: String,
                connectionResolution: ConnectionResolution
            ) {
                when (connectionResolution.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Log.i("Check", "Client: Is Connected")
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        Log.i("Check", "Client: Is Connect Error")
                    }

                    else -> {}
                }
            }

            override fun onDisconnected(endPointId: String) {
                Log.i("Check", "Client: Disconnected")
            }
        }
        val discoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                connectionClient.requestConnection(
                    userId,
                    endpointId,
                    connectionCallback
                ).addOnSuccessListener {
                    Log.i("Check", "Client: Request Success!")
                }.addOnFailureListener {
                    it.printStackTrace()
                }
                Log.i("Check", "Client: Is Found")
                // TODO: 해당 request 동작이 시작되고 나가거나하면 오류가 발생함
                trySend(DiscoveryEvent.EndpointFound(endpointId, info))
            }

            override fun onEndpointLost(endPointId: String) {
                trySend(DiscoveryEvent.EndpointLost(endPointId))
            }

        }

        connectionClient.startDiscovery(
            serviceId,
            discoveryCallback,
            options
        ).addOnSuccessListener {
            trySend(DiscoveryEvent.DiscoverySuccess)
        }.addOnFailureListener {
            it.printStackTrace()
            trySend(DiscoveryEvent.DiscoveryFailure)
        }

        awaitClose {
            connectionClient.stopAllEndpoints()
            connectionClient.stopDiscovery()
        }
    }
}

sealed interface ConnectionEvent {
    data class ConnectionInitiated(
        val endPointId: String,
        val connectionInfo: ConnectionInfo
    ) : ConnectionEvent

    data class ConnectionResult(
        val endPointId: String,
        val connectionInfo: ConnectionResolution
    ) : ConnectionEvent

    data class Disconnected(
        val endPointId: String
    ) : ConnectionEvent

    data object AdvertisingSuccess : ConnectionEvent
    data object AdvertisingFailure : ConnectionEvent
}

sealed interface DiscoveryEvent {
    data class EndpointFound(
        val endpointId: String,
        val info: DiscoveredEndpointInfo
    ) : DiscoveryEvent

    data class EndpointLost(
        val endPointId: String
    ) : DiscoveryEvent

    data object DiscoverySuccess : DiscoveryEvent
    data object DiscoveryFailure : DiscoveryEvent
}