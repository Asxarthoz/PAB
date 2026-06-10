package com.example.tourtest.core.di

import android.content.Context
import android.content.SharedPreferences
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.core.network.NetworkApiManager
import com.example.tourtest.core.network.TourizmeApiService
import com.example.tourtest.database.notification.AppDatabase
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.notification.dataaccess.NotificationDao
import com.example.tourtest.feature.notification.manager.NotificationHelper
import com.example.tourtest.feature.notification.manager.NotificationManager
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.Companion.getDatabase(context)

    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao =
        database.notificationDao()

    @Provides
    @Singleton
    fun provideUserSession(@ApplicationContext context: Context): UserSession =
        UserSession(context)

    @Provides
    @Singleton
    @Named("HomePrefs")
    fun provideHomePrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("home_search_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("FavoritePrefs")
    fun provideFavoritePrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("fav_search_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("ItineraryPrefs")
    fun provideItineraryPrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("itinerary_search_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideHomepageManager(): HomepageManager = HomepageManager

    @Provides
    @Singleton
    fun provideFavoriteManager(): FavoriteManager = FavoriteManager

    @Provides
    @Singleton
    fun provideItineraryManager(): ItineraryManager = ItineraryManager

    @Provides
    @Singleton
    fun provideNotificationManager(): NotificationManager = NotificationManager

    @Provides
    @Singleton
    fun provideNotificationHelper(): NotificationHelper = NotificationHelper()

    @Provides
    @Singleton
    fun provideProfileManager(@ApplicationContext context: Context): ProfileManager =
        ProfileManager(context)

    @Provides
    @Singleton
    fun providePasswordManager(@ApplicationContext context: Context): PasswordManager =
        PasswordManager(context)

    // ── Network (untuk NetworkDetailViewModel — WIP) ──────────────────────────

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideTourizmeApiService(client: OkHttpClient): TourizmeApiService =
        Retrofit.Builder()
            .baseUrl("https://tourizme.example.com/") // ganti dengan base URL server kalian
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TourizmeApiService::class.java)

    @Provides
    @Singleton
    fun provideNetworkApiManager(apiService: TourizmeApiService): NetworkApiManager =
        NetworkApiManager(apiService)
}