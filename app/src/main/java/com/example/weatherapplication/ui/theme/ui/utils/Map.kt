package com.example.weatherapplication.ui.theme.ui.utils

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class MyUpdatedLocation(
    val myLocation: LatLng? = null,
    val isLocationPermissionGranted: Boolean,
)

/**
 * @param maxBatchWaitTime Sets the maximum time when batched location updates are delivered.
 * Updates may be delivered sooner than this interval.
 * @param fastestRefreshInterval Sets the fastest rate for active location updates. This
 * interval is exact, and your application will never receive updates more frequently than
 * this value.
 * @param refreshInterval Sets the desired interval for active location updates. This interval
 * is inexact. You may not receive updates at all if no location sources are available, or you
 * may receive them less frequently than requested. You may also receive updates more frequently
 * than requested if other applications are requesting location at a more frequent interval.
 * IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of targetSdkVersion)
 * may receive updates less frequently than this interval when the app is no longer in the
 * foreground.
 */
data class MyUpdatedLocationArgs(
    val myDefaultLocation: LatLng? = null,
    val maxBatchWaitTime: Duration = 2.minutes,
    val refreshInterval: Duration = 60.seconds,
    val fastestRefreshInterval: Duration = 30.seconds,
    val shouldRequestPermission: Boolean = true,
    val onMyUpdatedLocationChanged: (LatLng) -> Unit,
    val onLocationPermissionChanged: (PermissionGranted) -> Unit,
)

/**
 * Requests and returns frequent location updates provided location permissions are granted.
 */
@SuppressLint("MissingPermission")
@Composable
fun rememberMyUpdatedLocation(args: MyUpdatedLocationArgs): MyUpdatedLocation {
    val permissionState = requestLocationPermission(
        shouldRequestPermission = args.shouldRequestPermission,
        onLocationPermissionChanged = args.onLocationPermissionChanged,
    )

    val isLocationPermissionEnabled by remember(permissionState) {
        derivedStateOf {
            permissionState.allPermissionsGranted
        }
    }

    val myUpdatedLocationSaver = run {
        val latitudeKey = "latitude"
        val longitudeKey = "longitude"
        mapSaver(
            save = {
                if (it.myLocation == null) {
                    emptyMap<String, Double>()
                } else {
                    mapOf(
                        latitudeKey to it.myLocation.latitude,
                        longitudeKey to it.myLocation.longitude,
                    )
                }
            },
            restore = {
                MyUpdatedLocation(
                    myLocation = if (it.isEmpty()) {
                        null
                    } else {
                        LatLng(
                            it[latitudeKey] as Double,
                            it[longitudeKey] as Double,
                        )
                    },
                    isLocationPermissionGranted = isLocationPermissionEnabled,
                )
            },
        )
    }

    var myUpdatedLocation by rememberSaveable(
        args.myDefaultLocation,
        isLocationPermissionEnabled,
        stateSaver = myUpdatedLocationSaver,
    ) {
        mutableStateOf(MyUpdatedLocation(args.myDefaultLocation, isLocationPermissionEnabled))
    }

    if (!isLocationPermissionEnabled) {
        // Do not continue receiving location updates if required location permissions are not
        // granted.
        return myUpdatedLocation
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val myLocation = locationResult.lastLocation.run {
                LatLng(latitude, longitude).also {
                    args.onMyUpdatedLocationChanged.invoke(it)
                }
            }
            myUpdatedLocation = myUpdatedLocation.copy(myLocation = myLocation)
        }
    }

    val context = LocalContext.current
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    DisposableEffect(
        args.refreshInterval,
        args.maxBatchWaitTime,
        args.fastestRefreshInterval,
        fusedLocationProviderClient,
    ) {
        val locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(args.refreshInterval.inWholeSeconds)

            fastestInterval =
                TimeUnit.SECONDS.toMillis(args.fastestRefreshInterval.inWholeSeconds)

            maxWaitTime = TimeUnit.MINUTES.toMillis(args.maxBatchWaitTime.inWholeMinutes)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
        onDispose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    return myUpdatedLocation
}