package com.example.composecameraxdemo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.Executors

@SuppressLint("StaticFieldLeak")
class CameraXViewModel : ViewModel() {

    private val TAG = "CameraXViewModel"
    var enableTorch = mutableStateOf(false)
    private lateinit var previewView: PreviewView
    private lateinit var activity: ComponentActivity
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private lateinit var imageCapture: ImageCapture

    private val captureExecutor = Executors.newSingleThreadExecutor()

    /**
     * 初始化变量
     */
    fun init(activity: ComponentActivity, previewView: PreviewView) {
        this.previewView = previewView
        this.activity = activity
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener({
            bindPreview(cameraProviderFuture.get())
            bindImageCapture()
        }, ContextCompat.getMainExecutor(activity))
    }

    /**
     * 绑定预览
     */
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        preview = Preview.Builder().build()
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        camera = cameraProvider.bindToLifecycle(activity, cameraSelector, preview)
    }

    /**
     * 绑定拍摄
     */
    private fun bindImageCapture() {
        imageCapture =
            ImageCapture.Builder().setTargetRotation(previewView.display.rotation).build()
        cameraProviderFuture.get().bindToLifecycle(activity, cameraSelector, imageCapture)
    }

    /**
     * 拍照
     */
    @SuppressLint("SimpleDateFormat")
    fun takePhoto() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
        val date = simpleDateFormat.format(System.currentTimeMillis())
        val photoFile = createFile(getOutputDirectory(activity),".jpg")
        val outFileOption =
            ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outFileOption,
            captureExecutor,
            object : ImageCapture.OnImageSavedCallback {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.e(
                        TAG,
                        "onImageSaved: success -- path = ${outputFileResults.savedUri?.path}",
                    )
                    //insertImageToGallery(photoFile.name)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "onError: ", exception)
                }
            })
    }

    /**
     * 对焦
     */
    fun focus(x:Float,y:Float){
        val action = FocusMeteringAction.Builder(previewView.meteringPointFactory.createPoint(x,y)).build()
        camera.cameraControl.startFocusAndMetering(action)
    }

    /**
     * 翻转镜头
     */
    @SuppressLint("RestrictedApi")
    fun flipCamera(){
        cameraProviderFuture.get().unbindAll()
        when(cameraSelector.lensFacing){
            CameraSelector.LENS_FACING_BACK->{
                cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
            }
            CameraSelector.LENS_FACING_FRONT->{
                cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            }
        }
        cameraProviderFuture.get().bindToLifecycle(activity,cameraSelector,preview,imageCapture)
    }

    fun switchFlash(){
        enableTorch.value = !enableTorch.value!!
        camera.cameraControl.enableTorch(enableTorch.value!!)

    }

    /**
     * 图片插入相册（暂时用不到）
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertImageToGallery(imageName: String) {
        val resolver = MyApp.getAppContext().contentResolver
        val audioCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val newImage = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
        }
        kotlin.runCatching {
           resolver.insert(audioCollection, newImage)
        }.onSuccess {
            Log.e(TAG, "insertImageToGallery: ${it?.path}")
        }.onFailure {
            Log.e(TAG, "insertImageToGallery: ",it )
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createFile(baseFolder: File,extension: String) =
        File(
            baseFolder, SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
                .format(System.currentTimeMillis()) + extension
        )

    private fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }
}
