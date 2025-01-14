package com.sdevprem.runtrack

import android.app.Application
import com.example.run_core.utils.ShareUtils
import com.google.firebase.FirebaseApp
import com.sdevprem.runtrack.core.tracking.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RunTrackApp : Application() {
    @Inject
    lateinit var notificationHelper: NotificationHelper
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        notificationHelper.createNotificationChannel()

        FirebaseApp.initializeApp(this)
        ShareUtils.init(this)
    }
}