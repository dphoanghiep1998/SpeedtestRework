package com.example.speedtest_rework.ui.wifi_detector

import android.annotation.SuppressLint
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentWifiDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.adapter.WifiDetectorAdapter
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import java.io.IOException
import java.net.ServerSocket


class FragmentWifiDetector : BaseFragment(), ItemDeviceHelper {
    private lateinit var binding: FragmentWifiDetectorBinding
    private lateinit var adapter: WifiDetectorAdapter
    private lateinit var nsdManager: NsdManager
    private lateinit var mRegistrationListener: NsdManager.RegistrationListener
    private lateinit var mServiceInfo: android.net.nsd.NsdServiceInfo
    private lateinit var mDiscoveryListener: NsdManager.DiscoveryListener
    var SERVICE_TYPE = "_http._tcp."
    var mServiceName = ""
    var mLocalPort = 0
    private lateinit var mServerSocket: ServerSocket
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWifiDetectorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun mdnsScanDhome(context: Context) {
        val nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager)
        nsdManager.discoverServices(
            "_services._dns-sd._udp",
            NsdManager.PROTOCOL_DNS_SD,
            object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    Log.e("TAG", "onStartDiscoveryFailed: $errorCode")
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                    Log.e("TAG", "onStopDiscoveryFailed: $errorCode")
                }

                override fun onDiscoveryStarted(serviceType: String) {
                    Log.d("TAG", "onDiscoveryStarted: "+ serviceType)
                }

                override fun onDiscoveryStopped(serviceType: String) {
                    Log.d("TAG", "onDiscoveryStopped: ")
                }

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    Log.d(
                        "TAG", """
                        onServiceFound: ${serviceInfo.serviceName}
                                    type:	${serviceInfo.serviceType}
                                                                    """.trimIndent()
                    )
                    if (serviceInfo.serviceName.contains("Dhome")) {
                        Log.d("TAG", "onServiceFound1: " + serviceInfo.serviceName)
                        nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                            override fun onResolveFailed(
                                serviceInfo: NsdServiceInfo,
                                errorCode: Int
                            ) {
                            }

                            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                                Log.d(
                                    "TAG",
                                    "onServiceResolved: " + serviceInfo.host + ":" + serviceInfo.port
                                )
                                //                            setSharedPreferences(context, TEMP_LOCAL_MQTT_SERVER_KEY, "tcp://" + serviceInfo.getHost() + ":1883");

                            }
                        })
                    }
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
            })
    }

    private fun initView() {
//        try {
//            mServerSocket = ServerSocket(8080)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        mLocalPort = mServerSocket.localPort
//        registerService(mLocalPort)
        mdnsScanDhome(requireContext())
        initButton()
        initRecycleView()
    }

    private fun initRecycleView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvDevice.layoutManager = linearLayoutManager
        adapter = WifiDetectorAdapter(this)
        binding.rcvDevice.adapter = adapter
    }

    private fun registerService(port: Int) {
        // Create the NsdServiceInfo object, and populate it.
        mServiceInfo = NsdServiceInfo()

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        mServiceInfo.serviceName = "NsdChat"
        mServiceInfo.serviceType = "_http._tcp."
        mServiceInfo.port = port
        nsdManager = requireContext().applicationContext
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

    @SuppressLint("MissingPermission")
    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnReload.setOnClickListener {
        }
    }

    private fun initializeRegistrationListener() {
        mRegistrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.serviceName
                Log.d("TAG", "Service name: $mServiceName")
                Log.d("TAG", "Port number: $mLocalPort")
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
                Log.d("TAG", "Service discovery success: $service")
                if (service.serviceType != SERVICE_TYPE) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d("TAG", "Unknown Service Type: " + service.serviceType)
                } else if (service.serviceName == mServiceName) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d("TAG", "Same machine: $mServiceName")
                } else if (service.serviceName.contains("NsdChat")) {
                    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            // TODO Auto-generated method stub
                            Log.d("TAG", "Resolving service...")
                        }

                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                            // TODO Auto-generated method stub
                            Log.d("TAG", "Service resolve failed!")
                        }
                    })
                }
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


    override fun onClickFlag(item: DeviceModel) {
    }
}