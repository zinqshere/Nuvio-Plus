package com.nuvio.app.core.ui

import android.app.Activity
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference

private var currentActivity: WeakReference<Activity>? = null

fun registerPlatformExitActivity(activity: Activity) {
    currentActivity = WeakReference(activity)
}

fun unregisterPlatformExitActivity(activity: Activity) {
    if (currentActivity?.get() === activity) {
        currentActivity = null
    }
}

actual fun platformExitApp() {
    currentActivity?.get()?.let { activity ->
        activity.runOnUiThread {
            if (!activity.isFinishing) {
                ActivityCompat.finishAffinity(activity)
            }
        }
    }
}
