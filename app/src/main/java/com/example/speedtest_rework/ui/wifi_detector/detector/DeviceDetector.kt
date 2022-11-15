package com.example.speedtest_rework.ui.wifi_detector.detector

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.speedtest_rework.ui.wifi_detector.FragmentWifiDetector

class DeviceDetector(private val fragment: FragmentWifiDetector) {
    private val mFragment: Fragment
    private lateinit var mServiceInfo: NsdServiceInfo
    private lateinit var nsdManager: NsdManager
    private val serviceType = "._http._tcp"
    private var serviceName: String
    private lateinit var mRegistrationListener: NsdManager.RegistrationListener
    private lateinit var mDiscoveryListener: NsdManager.DiscoveryListener

    init {
        mFragment = fragment
        serviceName = "My Device"
    }

    fun registerService(port: Int) {
        // Create the NsdServiceInfo object, and populate it.
        mServiceInfo = NsdServiceInfo()
        mServiceInfo.serviceName = serviceName
        mServiceInfo.serviceType = serviceType
        mServiceInfo.port = port
        nsdManager = mFragment.requireContext().applicationContext
            .getSystemService(Context.NSD_SERVICE) as NsdManager
        initializeRegistrationListener()
        initializeDiscoveryListener()
        nsdManager.registerService(
            mServiceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener
        )
        nsdManager.discoverServices(
            "_http._tcp", NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener
        )

    }

    private fun initializeRegistrationListener() {
        mRegistrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d("TAG", "Registration Failed! Error code: $errorCode")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d("TAG", "Service unregistered.")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.d("TAG", "Unregistration failed!")
            }
        }
    }

    private fun initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = object : NsdManager.DiscoveryListener {
            //  Called as soon as service discovery begins.
            override fun onDiscoveryStarted(regType: String) {
                Log.d("TAG", "Service discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                // A service was found!  Do something with it.
                Log.d("TAG", "Service discovery success: ${service}")


                nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        Log.d("TAG", "Resolving service..." + serviceInfo)
                    }

                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        Log.d("TAG", "Service resolve failed!")
                    }
                })


            }

            override fun onServiceLost(service: NsdServiceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("TAG", "service lost: $service")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i("TAG", "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.i("TAG", "Discovery failed: Error code: $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("TAG", "Discovery failed: Error code: $errorCode")
                nsdManager.stopServiceDiscovery(this)
            }
        }
    }


}