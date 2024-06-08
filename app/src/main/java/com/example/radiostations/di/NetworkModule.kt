package com.example.radiostations.di

import android.content.Context
import com.example.radiostations.core.framework.ConnectivityMonitor
import com.example.radiostations.stations.framework.apiservice.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  private const val BASE_URL = "https://de1.api.radio-browser.info/"

  @Provides
  @Singleton
  fun provideCertificatePinner(): CertificatePinner =
    CertificatePinner.Builder()
      .add("de1.api.radio-browser.info", "sha256/+pDuKtY5CXkMe5Yi/Ffp8gn/h4/pSHaTdBpky57QmIY=")
      .build()


  @Provides
  @Singleton
  fun provideCache(@ApplicationContext context: Context): Cache {
    val cacheSize = 10 * 1024 * 1024 // 10 MB
    return Cache(context.cacheDir, cacheSize.toLong())
  }

  @Provides
  @Singleton
  fun provideOnlineInterceptor(): Interceptor = Interceptor { chain ->
    val response = chain.proceed(chain.request())
    val maxAge = 60 // read from cache for 1 minute
    response.newBuilder()
      .header("Cache-Control", "public, max-age=$maxAge")
      .build()
  }

  @Singleton
  @Provides
  fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return loggingInterceptor
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(
    certificatePinner: CertificatePinner,
    cache: Cache,
    onlineInterceptor: Interceptor,
    loggingInterceptor: HttpLoggingInterceptor
  ): OkHttpClient =
    OkHttpClient.Builder()
      .cache(cache)
      .certificatePinner(certificatePinner)
      .addNetworkInterceptor(onlineInterceptor)
      .addInterceptor(loggingInterceptor)
      .build()

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder().baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

  @Provides
  @Singleton
  fun provideConnectivityManager(@ApplicationContext context: Context) =
    ConnectivityMonitor.getInstance(context)
}