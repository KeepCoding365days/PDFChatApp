package com.example.pdfchat
import android.annotation.SuppressLint

import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pdfchat.ui.theme.PDFChatTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.firebase.inappmessaging.model.Button
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.rajat.pdfviewer.PdfViewerActivity
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.rajat.pdfviewer.util.saveTo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class MainActivity : ComponentActivity() {
    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDFChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FilePickerScreen()
                }
            }
        }
    }

    @Composable
    fun FilePickerScreen() {
        var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
        var change by remember {
            mutableStateOf(false)
        }

        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            selectedPdfUri = uri

            change=true;
        }
        selectedPdfUri?.let { home(it) }

        Column{
            Row() {
                Button(onClick = { filePickerLauncher.launch(arrayOf("application/pdf")) }) {
                    Text("Pick PDF File")
                }

                TextButton(onClick = { filePickerLauncher.launch(arrayOf("application/pdf")) }) {
                    Text("Audio")
                }
                TextButton(modifier= Modifier.background(androidx.compose.ui.graphics.Color.Black),onClick = {
                    val text = selectedPdfUri?.let { extractTextFromPdf(it, applicationContext) }
                    val url = "http://192.168.0.245/generate_summary/?text=$text"
                    val request = Request.Builder()
                        .url("https://4553-175-107-208-140.ngrok-free.app/generate_summary/?text=$text")
                        .get()
                        .build()
                    GlobalScope.launch(Dispatchers.IO) {

                        val response: Response =OkHttpClient().newCall(request).execute()
                        GlobalScope.launch(Dispatchers.Main){
                            if (response.isSuccessful) {
                                //Log.d(TAG,response.body().String())
                                Toast.makeText(
                                    applicationContext,
                                    response.body?.string() ?:"null" ,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Error in API calling",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }

                }) {
                    Text("Summarize")
                }
            }
            if (change){
                selectedPdfUri?.let { home(it) }
                change=false
            }
        }
    }


    private fun copyPdfFromAssetsToCache(fileName: String): File {
        val file = File(cacheDir, fileName)
        if (!file.exists()) {
            assets.open(fileName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file
    }

    @Composable
    private fun home(fileUri:Uri) {


        PdfRendererViewCompose(
            uri = fileUri,
            lifecycleOwner = LocalLifecycleOwner.current
        )
        extractTextFromPdf(fileUri,applicationContext)
    }
    private fun extractTextFromPdf(path: Uri,context:Context): String{
        var extractedText= ""
        context.contentResolver.openInputStream(path)?.use { inputStream ->
            val reader: PdfReader = PdfReader(inputStream)
            val n = reader.numberOfPages
            for (i in 0 until n) {
                extractedText += PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }
            }
        }
        Log.d(TAG,extractedText)
        return extractedText
    }

}


