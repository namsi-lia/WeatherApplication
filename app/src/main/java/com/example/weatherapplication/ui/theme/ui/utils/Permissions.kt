package com.example.weatherapplication.ui.theme.ui.utils

import android.Manifest
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.example.weatherapplication.R
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@JvmInline
value class PermissionGranted(val value: Boolean)

@Composable
fun requestLocationPermission(
    vararg permissions: String,
    shouldRequestPermission: Boolean = true,
    onLocationPermissionChanged: (PermissionGranted) -> Unit = {},
): MultiplePermissionsState {
    var showRationale by rememberSaveable { mutableStateOf(true) }

    val permissionState = rememberMultiplePermissionsState(
        permissions = permissions.toList().ifEmpty {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        },
    )

    if (!shouldRequestPermission) {
        return permissionState
    }

    if (!permissionState.allPermissionsGranted) {
        SideEffect(permissionState::launchMultiplePermissionRequest)
    }
    // If the user denied the permission but a rationale should be shown, or the user sees
    // the permission for the first time, explain why the feature is needed by the app and allow
    // the user to be presented with the permission again or to not see the rationale any more.
    else if (permissionState.shouldShowRationale && showRationale) {
        AlertDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
            onDismissRequest = { showRationale = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        permissionState.launchMultiplePermissionRequest()
                        showRationale = false
                    },
                ) { Text(stringResource(com.example.weatherapplication.R.string.request_permission_button_label)) }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false }) {
                    Text(stringResource(com.example.weatherapplication.R.string.never_show_again_button_label))
                }
            },
            text = { Text(stringResource(R.string.location_permission_rationale)) },
        )
    } else {
        SideEffect {
            val permissionGranted = PermissionGranted(permissionState.allPermissionsGranted)
            onLocationPermissionChanged.invoke(permissionGranted)
        }
    }
    return permissionState
}