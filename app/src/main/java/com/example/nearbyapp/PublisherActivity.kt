package com.example.nearbyapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class PublisherActivity : AppCompatActivity() {

    companion object {
        const val TTL_IN_SECONDS = 3 * 60
    }

    var friendEndpointId = ""
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, connectionInfo: Payload) {
            Toast.makeText(
                this@PublisherActivity,
                String(connectionInfo.asBytes() ?: byteArrayOf()),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onPayloadTransferUpdate(
            p0: String,
            payloadTransferUpdate: PayloadTransferUpdate
        ) {

        }
    }
    val connectionsClient: ConnectionsClient by lazy {
        Nearby.getConnectionsClient(this@PublisherActivity)
    }
    private val advertisingOptions: AdvertisingOptions by lazy {
        AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointId: String, connectionInfo: ConnectionInfo) {
            friendEndpointId = endPointId
            this@PublisherActivity.deviceAdapter.addItem(
                endPointId,
                connectionInfo.endpointName,
                PairStatus.Discover
            )
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.SUCCESS -> {
                    connectionsClient.stopDiscovery()
                    connectionsClient.stopAdvertising()
                    this@PublisherActivity.deviceAdapter.addItem(
                        id = endpointId,
                        status = PairStatus.Connect
                    )
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    this@PublisherActivity.deviceAdapter.addItem(
                        id = endpointId,
                        status = PairStatus.Disconnect
                    )
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    this@PublisherActivity.deviceAdapter.addItem(
                        id = endpointId,
                        status = PairStatus.Disconnect
                    )
                }
            }
        }

        override fun onDisconnected(p0: String) {
            Toast.makeText(this@PublisherActivity, "PublisherActivity ${p0}", Toast.LENGTH_SHORT)
                .show()
            friendEndpointId = ""
        }

        override fun onBandwidthChanged(p0: String, p1: BandwidthInfo) {
            super.onBandwidthChanged(p0, p1)
            Log.d("tung", "onBandwidthChanged: ")
        }
    }


    val deviceAdapter: DeviceAdapter by lazy {
        DeviceAdapter()
    }
    private val btAdvertiser: AppCompatButton by lazy {
        findViewById(R.id.btAdvertiser)
    }
    private val rcDevice: RecyclerView by lazy {
        findViewById(R.id.rcDevice)
    }
    private val btSendPayload: AppCompatButton by lazy {
        findViewById(R.id.btSendPayload)
    }

    private val txtContent: AppCompatEditText by lazy {
        findViewById(R.id.tvMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publisher)
        initView()
        initEvent()
    }

    private fun startAdvertising() {
        connectionsClient.startAdvertising(
            Build.MODEL, this@PublisherActivity.packageName, connectionLifecycleCallback,
            advertisingOptions
        )
    }

    private fun initView() {
        rcDevice.adapter = deviceAdapter
        deviceAdapter.onConnectClick = {
            connectionsClient.acceptConnection(it?.id.orEmpty(), payloadCallback)
        }
    }

    private fun initEvent() {
        btSendPayload.setOnClickListener {
            sendPayload(friendEndpointId)
        }

        btAdvertiser.setOnClickListener {
            startAdvertising()
        }
    }

    private fun sendPayload(endPointId: String) {
        val payload = Payload.fromBytes(txtContent.text.toString().toByteArray())
        connectionsClient.sendPayload(endPointId, payload)
            .addOnSuccessListener {
                Toast.makeText(
                    this@PublisherActivity,
                    "Send payload success",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { ex ->
                Toast.makeText(this@PublisherActivity, "${ex.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}