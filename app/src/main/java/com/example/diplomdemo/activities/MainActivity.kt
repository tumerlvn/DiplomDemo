package com.example.diplomdemo.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomdemo.DemoApplication
import com.example.diplomdemo.R
import com.example.diplomdemo.adapters.ClickItemAdapter
import com.example.diplomdemo.items.ClickItem


import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.util.*

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    private val BLUETOOTH_PERMISSIONS_REQUEST_CODE = 200
    private val SETTINGS_INTENT_CODE = 1000

    private val BLUETOOTH_PERMISSIONS_SCAN = "android.permission.BLUETOOTH_SCAN"
    private val BLUETOOTH_PERMISSIONS_CONNECT = "android.permission.BLUETOOTH_CONNECT"
    private val BLUETOOTH_PERMISSIONS_ADVERTISE = "android.permission.BLUETOOTH_ADVERTISE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val clickItemList = generateList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ClickItemAdapter(clickItemList)
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        if (!hasBluetoothPermissions()) {
            requestBluetoothPermissions()
        } else {
            // Only initialize IPv8 if it has not been initialized yet.
            try {
                IPv8Android.getInstance()
            } catch (exception: Exception) {
                (application as DemoApplication).initIPv8()
            }
        }

//        val community = IPv8Android.getInstance().getOverlay<DemoCommunity>()!!
//        val peers = community.getPeers()
//        for (peer in peers) {
//            Log.d("DemoApplication", peer.mid)
//        }

        adapter.setOnItemClickListener(object : ClickItemAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        startActivity(Intent(this@MainActivity, ListOfContacts::class.java)).apply {

                        }
                    }
                    1 -> {
                        startActivity(Intent(this@MainActivity, ListOfAvailablePeers::class.java)).apply {

                        }
                    }
                    else -> Log.d("Demo.onItemClick", "Clicked on something else")
                }
            }
        })

//        lifecycleScope.launch {
//            while (isActive) {
//                community.broadcastGreeting()
//                delay(1000)
//            }
//        }
    }

    private fun generateList(): List<ClickItem> {
        val list = ArrayList<ClickItem>()

        list += ClickItem(R.drawable.ic_contacts, "Contacts")
        list += ClickItem(R.drawable.ic_add_contact, "Add Contact")

        return list
    }

    private fun hasBluetoothPermissions(): Boolean {
        return checkSelfPermission(BLUETOOTH_PERMISSIONS_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(BLUETOOTH_PERMISSIONS_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(BLUETOOTH_PERMISSIONS_SCAN) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermissions() {
        requestPermissions(
            arrayOf(
                BLUETOOTH_PERMISSIONS_ADVERTISE,
                BLUETOOTH_PERMISSIONS_CONNECT,
                BLUETOOTH_PERMISSIONS_SCAN
            ),
            BLUETOOTH_PERMISSIONS_REQUEST_CODE
        )
    }

}