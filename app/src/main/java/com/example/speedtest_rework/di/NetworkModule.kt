package com.example.speedtest_rework.di


import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.data.apis.AddressInfoApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
    val READ_TIMEOUT = "READ_TIMEOUT"
    val WRITE_TIMEOUT = "WRITE_TIMEOUT"

    //    @Provides
//    fun provideCustomerAPI(@Named("MainSite") retrofit: Retrofit): CustomerAPI {
//        return retrofit.create(CustomerAPI::class.java)
//    }
    @Provides
    fun provideAddressInfo(@Named("AddressInfoSite") retrofit: Retrofit): AddressInfoApi {
        return retrofit.create(AddressInfoApi::class.java)
    }

    @Provides
    @Singleton
    @Named("AddressInfoSite")
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {

        return Retrofit.Builder().addConverterFactory(moshiConverterFactory)
            .baseUrl(Constant.SERVER_LIST)
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideOKHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder =
            OkHttpClient.Builder()
        builder.interceptors().add(httpLoggingInterceptor)

        return builder.callTimeout(5000,TimeUnit.MILLISECONDS).build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        return MoshiConverterFactory.create(moshi)
    }


}