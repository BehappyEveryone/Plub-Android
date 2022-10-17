package com.plub.plubandroid.di

import com.plub.data.api.IntroApi
import com.plub.data.api.PlubJwtTokenApi
import com.plub.data.api.SampleApi
import com.plub.plubandroid.util.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {
    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideHttpClient(
        authenticator: TokenAuthenticator,
        authenticationInterceptor: AuthenticationInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authenticationInterceptor)
            .authenticator(authenticator)
            .build()
    }

    @Singleton
    @Provides
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun providePlubJwtTokenApi(@AuthRetrofit retrofit: Retrofit): PlubJwtTokenApi{
        return retrofit.create(PlubJwtTokenApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSampleApi(@AuthRetrofit retrofit: Retrofit) : SampleApi{
        return retrofit.create(SampleApi::class.java)
    }
}