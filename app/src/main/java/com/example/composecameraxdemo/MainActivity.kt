package com.example.composecameraxdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecameraxdemo.ui.theme.ComposeCameraXDemoTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCameraXDemoTheme() {
                val viewModel: CameraXViewModel = viewModel()
                val torchState = remember {
                    viewModel.enableTorch
                }
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.isStatusBarVisible = false
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        RequestPermission {
                            CameraX(modifier = Modifier.fillMaxSize(), activity = this@MainActivity)
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                IconButton(onClick = { viewModel.switchFlash() }) {
                                    Icon(
                                        painterResource(
                                            id = if (torchState.value) {
                                                R.drawable.ic_baseline_flash_on_24
                                            } else {
                                                R.drawable.ic_baseline_flash_off_24
                                            }
                                        ),
                                        "", tint = Color.White
                                    )
                                }
                                FloatingActionButton(onClick = { viewModel.takePhoto() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_camera_24),
                                        "", tint = Color.White
                                    )
                                }
                                IconButton(onClick = { viewModel.flipCamera() }) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_baseline_flip_camera_android_24),
                                        "",tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
