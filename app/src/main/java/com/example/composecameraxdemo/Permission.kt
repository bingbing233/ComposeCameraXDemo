package com.example.composecameraxdemo

import android.Manifest
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(onGranted: @Composable () -> Unit) {

    val permission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    if (permission.allPermissionsGranted) {
        onGranted()
    } else {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    permission.launchMultiplePermissionRequest()
                }) {
                    Text(text = "给吧")
                }
            },
            dismissButton = {
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "不了", color = Color.Gray)
                }
            },
            title = { Text(text = "权限请求") },
            text = { Text(text = "请求一下相机和存储权限咯") },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }
}