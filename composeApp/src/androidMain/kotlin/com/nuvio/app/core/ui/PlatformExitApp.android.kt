package com.nuvio.app.core.ui

import android.app.Activity
import androidx.core.app.ActivityCompat

private var currentActivity: Activity? = null

fun registerPlatformExitActivity(activity: Activity) {
    currentActivity = activity
}

fun unregisterPlatformExitActivity(activity: Activity) {
    if (currentActivity === activity) {
        currentActivity = null
    }
}

actual fun platformExitApp() {
    val activity = currentActivity
    if (activity != null) {
        activity.runOnUiThread {
            ActivityCompat.finishAffinity(activity)
        }
    }
}
