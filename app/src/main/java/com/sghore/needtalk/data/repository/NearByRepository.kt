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
    // 다른 기기가 Host를 찾을 수 있도록 광고 및 데이터 송 수신 담당
    fun startAdvertising(userId: String, serviceId: String) = callbackFlow {
        val options = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_STAR)
            .build()
        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                trySend(ConnectionEvent.PayloadReceived(endpointId, payload))
            }

            override fun onPayloadTransferUpdate(
                endpointId: String,
                update: PayloadTransferUpdate
            ) {

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
                ).addOnFailureListener {
                    it.printStackTrace()
                    trySend(ConnectionEvent.AdvertisingFailure)
                }
                // TODO: 해당 콜백이 동작이 시작되고 나가거나하면 오류가 발생함
            }

            override fun onConnectionResult(
                endPointId: String,
                result: ConnectionResolution
            ) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> { // 기기 간의 연결이 되었으며 문제가 없는 경우
                        trySend(
                            ConnectionEvent.SuccessConnect(
                                endPointId,
                                result
                            )
                        )
                        Log.i("Check", "Host: Is Connected!")
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> { // 기기 간의 연결이 되었으며 문제가 발생한 경우
                        trySend(
                            ConnectionEvent.ConnectionResultError(
                                endPointId,
                                result
                            )
                        )
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
    fun startDiscovery(serviceId: String) = callbackFlow {
        val options = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_STAR)
            .build()
        val discoveryCallback = object : EndpointDiscoveryCallback() {
            // 근처 기기를 발견하였을 경우
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                trySend(ClientEvent.DiscoveryEndpointFound(endpointId, info))
            }

            override fun onEndpointLost(endPointId: String) {
                trySend(ClientEvent.DiscoveryEndpointLost(endPointId))
            }

        }

        // 찾기 시작
        connectionClient.startDiscovery(
            serviceId,
            discoveryCallback,
            options
        ).addOnFailureListener {
            it.printStackTrace()
            trySend(
                ClientEvent.DiscoveryFailure(
                    "기기를 찾는 과정에서 오류가 발생하였습니다."
                )
            )
        }

        awaitClose { // 모든 동작을 취소함
            stopDiscovery()
        }
    }

    // 상대 기기와의 연결 및 데이터 송 수신을 담당함
    fun connectToHost(userId: String, endpointId: String) = callbackFlow {
        val payloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                trySend(ClientEvent.PayloadReceived(endpointId, payload))
            }

            override fun onPayloadTransferUpdate(
                endpointId: String,
                result: PayloadTransferUpdate
            ) {
            }
        }
        val connectionCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endPointId: String,
                connectionInfo: ConnectionInfo
            ) {
                connectionClient.acceptConnection(
                    endPointId,
                    payloadCallback
                ).addOnFailureListener {
                    it.printStackTrace()
                    trySend(
                        ClientEvent.ClientConnectionFailure(
                            "연결 과정에서 오류가 발생하였습니다."
                        )
                    )
                }
            }

            override fun onConnectionResult(
                endPointId: String,
                connectionResolution: ConnectionResolution
            ) {
                when (connectionResolution.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        trySend(ClientEvent.SuccessConnect)
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

        // 상대에게 연결 요청
        connectionClient.requestConnection(
            userId,
            endpointId,
            connectionCallback
        ).addOnFailureListener {
            it.printStackTrace()
            trySend(ClientEvent.ClientConnectionFailure("연결 과정에서 오류가 발생하였습니다."))
        }

        awaitClose { // 모든 동작을 취소함
            connectionClient.stopAllEndpoints()
        }
    }

    fun sendPayload(
        payload: Payload,
        endpointId: String,
        onFailure: (Exception) -> Unit
    ) {
        connectionClient.sendPayload(endpointId, payload)
            .addOnFailureListener {
                it.printStackTrace()
                onFailure(it)
            }
    }

    fun disconnectOther(endpointId: String) {
        connectionClient.disconnectFromEndpoint(endpointId)
    }

    fun stopDiscovery() {
        connectionClient.stopDiscovery()
    }

    fun stopAllEndpoints() {
        connectionClient.stopAllEndpoints()
    }
}

sealed interface ConnectionEvent {
    data class PayloadReceived(val endpointId: String, val payload: Payload) : ConnectionEvent

    data class SuccessConnect(
        val endpointId: String,
        val connectionInfo: ConnectionResolution
    ) : ConnectionEvent

    data class ConnectionResultError(
        val endpointId: String,
        val connectionInfo: ConnectionResolution
    ) : ConnectionEvent

    data class Disconnected(
        val endpointId: String
    ) : ConnectionEvent

    data object AdvertisingFailure : ConnectionEvent
}

sealed interface ClientEvent {
    data class PayloadReceived(val endpointId: String, val payload: Payload) : ClientEvent
    data class DiscoveryEndpointFound(
        val endpointId: String,
        val info: DiscoveredEndpointInfo
    ) : ClientEvent

    data class DiscoveryEndpointLost(
        val endpointId: String
    ) : ClientEvent

    data class DiscoveryFailure(val errorMessage: String) : ClientEvent

    data object SuccessConnect : ClientEvent

    data class ClientConnectionFailure(val errorMessage: String) : ClientEvent
}