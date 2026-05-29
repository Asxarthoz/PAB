package com.example.tourtest.core

import android.content.Context
import android.content.SharedPreferences
import com.example.tourtest.core.data.UserSession
import com.example.tourtest.database.notification.AppDatabase
import com.example.tourtest.feature.favorite.manager.FavoriteManager
import com.example.tourtest.feature.homepage.manager.HomepageManager
import com.example.tourtest.feature.itinerary.manager.ItineraryManager
import com.example.tourtest.feature.notification.dataaccess.NotificationDao
import com.example.tourtest.feature.notification.manager.NotificationManager
import com.example.tourtest.feature.profile.manager.PasswordManager
import com.example.tourtest.feature.profile.manager.ProfileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    @Provides
    @Singleton
    fun provideUserSession(@ApplicationContext context: Context): UserSession = UserSession(context)

    @Provides
    @Singleton
    @Named("HomePrefs")
    fun provideHomePrefs(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences("home_search_prefs",
        Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("FavoritePrefs")
    fun provideFavoritePrefs(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences("fav_search_prefs",
        Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named("ItineraryPrefs")
    fun provideItineraryPrefs(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences("itinerary_search_prefs",
        Context.MODE_PRIVATE)

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
    fun provideProfileManager(@ApplicationContext context: Context): ProfileManager = ProfileManager(context)

    @Provides
    @Singleton
    fun providePasswordManager(@ApplicationContext context: Context): PasswordManager = PasswordManager(context)
}