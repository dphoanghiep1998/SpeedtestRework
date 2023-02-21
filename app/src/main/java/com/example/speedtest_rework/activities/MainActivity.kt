package com.example.speedtest_rework.activities

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.activity.BaseActivity
import com.example.speedtest_rework.common.custom_view.ConnectionType
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.createContext
import com.example.speedtest_rework.databinding.ActivityMainBinding
import com.example.speedtest_rework.receivers.ConnectivityListener
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: ActivityMainBinding
    private var navHostFragment: NavHostFragment? = null
    val viewModel: SpeedTestViewModel by viewModels()
    private lateinit var connectivityListener: ConnectivityListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor()
        binding = ActivityMainBinding.inflate(layoutInflater)
        initLocaleViewModel()
        AppSharePreference.getInstance(this).registerOnSharedPreferenceChangeListener(this)
        setContentView(binding.root)
        registerConnectivityListener()
        observeConnectivityChange()
        observePermissionChange()
        initNavController()
        loadServer()
    }

    override fun attachBaseContext(newBase: Context) = super.attachBaseContext(
        newBase.createContext(
            Locale(
                AppSharePreference.INSTANCE.getSavedLanguage(
                    Locale.getDefault().language
                )
            )
        )
    )

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (overrideConfiguration != null) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterConnectivityListener()
    }

    private fun loadServer() {
        viewModel.doMultiTask()
    }


    private fun onConnectivityChange() {
        if (NetworkUtils.isWifiConnected(this)) {
            viewModel.typeNetwork.postValue(ConnectionType.WIFI)
            viewModel.networkName.value = NetworkUtils.getNameWifi(this)
            loadServer()
        } else if (NetworkUtils.isMobileConnected(this)) {
            viewModel.typeNetwork.postValue(ConnectionType.MOBILE)
            val info = NetworkUtils.getInforMobileConnected(this)
            viewModel.networkName.value =
                if (info != null) info.typeName + " - " + info.subtypeName else "Mobile"
            loadServer()
        } else {
            viewModel.typeNetwork.postValue(ConnectionType.UNKNOWN)
        }
    }


    private fun initNavController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
    }

    private fun initLocaleViewModel() {
        viewModel.currentLanguage = AppSharePreference.getInstance(this).getSavedLanguage(
            Locale.getDefault().language
        )
    }

    private fun changeStatusBarColor() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray_700)
    }


    private fun registerConnectivityListener() {
        connectivityListener = ConnectivityListener(applicationContext)
        val filter = IntentFilter()
        filter.addAction(Constant.INTENT_FILER_SCAN_RESULT)
        filter.addAction(Constant.INTENT_FILER_CONNECTIVITY_CHANGE)
        viewModel.addIsConnectivityChangedSource(connectivityListener.isConnectivityChanged)
        viewModel.addDataCacheSource(connectivityListener.dataCache)
        viewModel.addIsWifiEnabledSource(connectivityListener.isWifiEnabled)
        registerReceiver(connectivityListener, filter)
    }

    private fun unregisterConnectivityListener() {
        viewModel.removeIsConnectivityChangedSource(connectivityListener.isConnectivityChanged)
        viewModel.removeDataCacheSource(connectivityListener.dataCache)
        viewModel.removeIsWifiEnabledSource(connectivityListener.isWifiEnabled)
        unregisterReceiver(connectivityListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                navHostFragment?.navController?.navigate(R.id.action_fragmentPermission_to_fragmentMain)
                viewModel.setIsPermissionGranted(true)
            }
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?, key: String?
    ) {
        val settingLanguageLocale = viewModel.currentLanguage
        val languageLocaleChanged = AppSharePreference.INSTANCE.getSavedLanguage(
            Locale.getDefault().language
        ) != settingLanguageLocale
        if (languageLocaleChanged) {
            viewModel.currentLanguage =
                AppSharePreference.INSTANCE.getSavedLanguage(Locale.getDefault().language)
            finish()
            startActivity(intent)
        }

    }

    private fun observeConnectivityChange() {
        viewModel.mConnectivityChanged.observe(this) {
            onConnectivityChange()
        }
    }

    private fun observePermissionChange() {
        viewModel.mPermissionGranted.observe(this) {
            if (NetworkUtils.isWifiConnected(this)) {
                viewModel.networkName.value = NetworkUtils.getNameWifi(this)
            } else if (NetworkUtils.isMobileConnected(this)) {
                val info = NetworkUtils.getInforMobileConnected(this)
                viewModel.networkName.value =
                    if (info != null) info.typeName + " - " + info.subtypeName else "Mobile"
            }
        }
    }


}