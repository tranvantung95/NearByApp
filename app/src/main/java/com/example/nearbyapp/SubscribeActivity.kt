package com.example.nearbyapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class SubscribeActivity : AppCompatActivity() {
    var friendEndpointId = ""
    private val connectionsClient: ConnectionsClient by lazy {
        Nearby.getConnectionsClient(this@SubscribeActivity)
    }

    private val discoveryOptions: DiscoveryOptions by lazy {
        DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
    }
    private val btSend: AppCompatButton by lazy {
        findViewById(R.id.btSend)
    }
    private  val tvMessage  : AppCompatTextView by  lazy {
        findViewById(R.id.tvMessage)
    }
    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, connectionInfo: Payload) {
            connectionInfo.asBytes()?.let {
                tvMessage.text = String(it)
            }
        }

        override fun onPayloadTransferUpdate(
            p0: String,
            payloadTransferUpdate: PayloadTransferUpdate
        ) {

        }
    }
    val edtMess: AppCompatEditText by lazy { findViewById(R.id.edtMess) }

    val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient.acceptConnection(endPointId, payloadCallback)
            friendEndpointId = endPointId
        }

        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
            when (p1.status.statusCode) {
                ConnectionsStatusCodes.SUCCESS -> {
                    connectionsClient.stopDiscovery()
                    edtMess.visibility = View.VISIBLE
                    Toast.makeText(this@SubscribeActivity, "Connect Success", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        override fun onDisconnected(p0: String) {
            Toast.makeText(this@SubscribeActivity, "SubscribeActivity ${p0}", Toast.LENGTH_SHORT)
                .show()
            Log.d("tung", "SubscribeActivity onDisconnected: ${p0} ")
        }
    }

    private val endPointDiscoverCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection(
                Build.MODEL,
                endpointId,
                connectionLifecycleCallback
            )
        }

        override fun onEndpointLost(p0: String) {
            Toast.makeText(this@SubscribeActivity, p0, Toast.LENGTH_LONG).show()
        }
    }

    private val btSubscribe: AppCompatButton by lazy {
        findViewById(R.id.btSubscribe)
    }
    private val btUnSubscribe: AppCompatButton by lazy {
        findViewById(R.id.btUnSubscribe)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        btSubscribe.setOnClickListener {
            startDiscover()
        }

        btUnSubscribe.setOnClickListener {
            disconnect()
        }
        btSend.setOnClickListener {
            sendMess()
        }
    }

    private fun disconnect() {
        connectionsClient.stopAllEndpoints()
    }

    private fun sendMess() {
        val payload = Payload.fromBytes(edtMess.text.toString().toByteArray())
        connectionsClient.sendPayload(friendEndpointId, payload)
            .addOnSuccessListener {
                Toast.makeText(
                    this@SubscribeActivity,
                    "Send payload success",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { ex ->
                Toast.makeText(this@SubscribeActivity, "${ex.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun startDiscover() {
        connectionsClient.startDiscovery(
            packageName, endPointDiscoverCallback,
            discoveryOptions
        )
    }

}
