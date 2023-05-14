package com.example.diplomdemo



import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import nl.tudelft.ipv8.util.*
import android.app.Application
import android.bluetooth.BluetoothManager
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.example.diplomdemo.messages.DbMessageStore
import com.squareup.sqldelight.android.AndroidSqliteDriver
import nl.tudelft.ipv8.IPv8Configuration
import nl.tudelft.ipv8.Overlay
import nl.tudelft.ipv8.OverlayConfiguration
import nl.tudelft.ipv8.android.IPv8Android
import nl.tudelft.ipv8.android.keyvault.AndroidCryptoProvider
import nl.tudelft.ipv8.android.messaging.bluetooth.BluetoothLeDiscovery
import nl.tudelft.ipv8.android.peerdiscovery.NetworkServiceDiscovery
import nl.tudelft.ipv8.attestation.trustchain.*
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainSQLiteStore
import nl.tudelft.ipv8.attestation.trustchain.store.TrustChainStore
import nl.tudelft.ipv8.attestation.trustchain.validation.TransactionValidator
import nl.tudelft.ipv8.attestation.trustchain.validation.ValidationResult
import nl.tudelft.ipv8.keyvault.PrivateKey
import nl.tudelft.ipv8.keyvault.defaultCryptoProvider
import nl.tudelft.ipv8.messaging.tftp.TFTPCommunity
import nl.tudelft.ipv8.peerdiscovery.DiscoveryCommunity
import nl.tudelft.ipv8.peerdiscovery.strategy.PeriodicSimilarity
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomChurn
import nl.tudelft.ipv8.peerdiscovery.strategy.RandomWalk
import nl.tudelft.ipv8.sqldelight.Database
import nl.tudelft.ipv8.util.hexToBytes
import nl.tudelft.ipv8.util.toHex

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        val BUILD_VERSION_CODE_S = 31
        if (Build.VERSION.SDK_INT < BUILD_VERSION_CODE_S) {
            initIPv8()
        }
    }

    fun initIPv8() {
        val demoCommunity = OverlayConfiguration(
//            Overlay.Factory(DemoCommunity::class.java),
            DemoCommunity.Factory(DbMessageStore.getInstance(this),this),
            listOf(RandomWalk.Factory())
        )

        val config = IPv8Configuration(overlays = listOf(
            createDiscoveryCommunity(),
            demoCommunity
        ))

        IPv8Android.Factory(this)
            .setConfiguration(config)
            .setPrivateKey(getPrivateKey())
            .init()
    }

    private fun createDiscoveryCommunity(): OverlayConfiguration<DiscoveryCommunity> {
        val randomWalk = RandomWalk.Factory()

        val nsd = NetworkServiceDiscovery.Factory(getSystemService()!!)
        val bluetoothManager = getSystemService<BluetoothManager>()
            ?: throw IllegalStateException("BluetoothManager not available")
        val strategies = mutableListOf(
            randomWalk, nsd
        )
        if (bluetoothManager.adapter != null && Build.VERSION.SDK_INT >= 24) {
            val ble = BluetoothLeDiscovery.Factory()
            strategies += ble
        }

        return OverlayConfiguration(
            DiscoveryCommunity.Factory(),
            strategies
        )
    }

    private fun getPrivateKey(): PrivateKey {
        // Load a key from the shared preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val privateKey = prefs.getString(Companion.PREF_PRIVATE_KEY, null)
        return if (privateKey == null) {
            // Generate a new key on the first launch
            val newKey = AndroidCryptoProvider.generateKey()
            prefs.edit()
                .putString(Companion.PREF_PRIVATE_KEY, newKey.keyToBin().toHex())
                .apply()
            newKey
        } else {
            AndroidCryptoProvider.keyFromPrivateBin(privateKey.hexToBytes())
        }
    }

    companion object {
        private const val PREF_PRIVATE_KEY = "private_key"
    }
}