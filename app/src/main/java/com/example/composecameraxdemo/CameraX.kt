package com.example.composecameraxdemo

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecameraxdemo.CameraXViewModel

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraX(activity:ComponentActivity,modifier: Modifier  = Modifier) {
    val viewModel: CameraXViewModel = viewModel()

    AndroidView(factory = { context->
       val previewView =  PreviewView(context)
        previewView.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    viewModel.focus(event.x,event.y)
                }
            }
            true
        }
        viewModel.init(activity,previewView)
        previewView
    }, modifier = modifier , update = {

    })
}